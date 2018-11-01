package com.example.luka.googlemapsandgogleplaces;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, Serializable {

    //CONSTANTS - in separate class
    private static final int REQUEST_CHECK_SETTINGS = 9003;

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 20;
    private static final String INTENT_PREVIEW = "2001";

    //callback
    private LocationCallback mLocationCallback;

    //vars
    private boolean mTracingEnabled=false;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocation;
    private RunProperties properties;
    Polyline line;
    Button btnStart;
    Button btnStop;
    Button btnPause;
    Date currentTime;
    TextView tvTime;
    TextView tvDistance;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime ;

    Handler handler;

    int Seconds, Minutes, MilliSeconds ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initVars();
        locationCallbackInit();
        getLocationPermission();
        initListener();
    }

    private void initVars() {
        handler = new Handler() ;
        properties= new RunProperties();
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvDistance.setText("0 m");
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTime.setText("00:00:00");
        MillisecondTime = 0L ;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        MapDisplayUtil.resetConstrains();

    }

    private void initListener() {
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);

                properties.points.add(new ArrayList<SerializableLatLng>());

                mTracingEnabled = true;
                mRequestingLocationUpdates = true;


                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setFastestInterval(5000);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(mLocationRequest);
                SettingsClient client = LocationServices.getSettingsClient(MapActivity.this);
                Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

                task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        locationSettingsResponse.getLocationSettingsStates();
                        startLocationUpdates();
                    }
                });

                task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MapActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                        }
                    }
                });
            }
        });
        btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);
                stopLocationUpdates();
                mTracingEnabled=false;
            }
        });
        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                properties.centerOfRoute = new SerializableLatLng(MapDisplayUtil.getCenter());
                properties.latMin = MapDisplayUtil.minLat;
                properties.latMax = MapDisplayUtil.maxLat;
                properties.lngMin = MapDisplayUtil.minLng;
                properties.lngMax = MapDisplayUtil.maxLng;

                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);
                stopLocationUpdates();
                mTracingEnabled=false;

                Intent intent = new Intent(MapActivity.this, ResultPreviewActivity.class);
                intent.putExtra(INTENT_PREVIEW, properties);
                startActivity(intent);
                mMap.clear();
            }
        });
    }


    private void locationCallbackInit() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    //List<LatLng> points = new ArrayList<>();

                    SerializableLatLng serLatLng = new SerializableLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                    properties.points.get(properties.points.size()-1).add(serLatLng);
                    //points.add(new LatLng(location.getLatitude(),location.getLongitude()));
                    PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                    for (int z = 0; z < properties.points.get(properties.points.size()-1).size(); z++) {
                        SerializableLatLng point = properties.points.get(properties.points.size()-1).get(z);

                        options.add(point.getLatLng());
                        MapDisplayUtil.isMinLat(point.getLatLng().latitude);
                        MapDisplayUtil.isMaxLat(point.getLatLng().latitude);
                        MapDisplayUtil.isMinLng(point.getLatLng().longitude);
                        MapDisplayUtil.isMaxLng(point.getLatLng().longitude);
                        if(properties.points.get(properties.points.size()-1).size()>1){
                            Log.d("array", "Lenght of outer array "+ properties.points.size());
                            Log.d("array", "Lenght of inner array "+ properties.points.get(properties.points.size()-1).size());
                            Log.d("array", "z value "+ z);
                            SerializableLatLng prev = properties.points.get(properties.points.size()-1).get(properties.points.get(properties.points.size()-1).size()-1);
                            updateDistanceAndSpeed(prev.getLatLng(), point.getLatLng());
                        }
                    }
                    line = mMap.addPolyline(options);
                    moveCamera(MapDisplayUtil.getCenter(),DEFAULT_ZOOM);
                    //properties.points.addAll(properties.polygonIndex, Arrays.asList(points));
                    Log.d(TAG, "onLocationResult: drawng route");
                    Toast.makeText(MapActivity.this, "Coords: Lat: " + currentLocation.getLatitude() + ", Long: " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void updateDistanceAndSpeed(LatLng prev, LatLng current) {
        Location locationA = new Location("point A");

        locationA.setLatitude(prev.latitude);
        locationA.setLongitude(prev.longitude);
        Log.d("speedA","lat: "+prev.latitude+" long: "+ prev.longitude);



        Location locationB = new Location("point B");

        locationB.setLatitude(current.latitude);
        locationB.setLongitude(current.longitude);

        Log.d("speedB","lat: "+current.latitude+" long: "+ current.longitude);
        float distance = locationA.distanceTo(locationB);
        Log.d(TAG, "distance local: "+distance);
        Log.d(TAG, "distance global: "+properties.distance);

        properties.distance +=distance;
        tvDistance.setText(distance+" m"+ properties.distance);
        //Toast.makeText(MapActivity.this, "Update speed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates&&mTracingEnabled) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //On map ready enable blue dot
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getDeviceLocation();
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getdevice location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if (mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            currentLocation = (Location) task.getResult();
                            //properties.points.get(properties.polygonIndex);
                            //points.add(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);
                        }
                        else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this,"Unable to find current location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: security exception"+ e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to "+ latLng.latitude+", "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    private void getLocationPermission(){
        String[] permissions ={Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }
            else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted=false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length>0){
                    for(int i = 0 ; i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted=true;
                    //initialize map
                    initMap();
                }
            }
        }
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            tvTime.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };
}
