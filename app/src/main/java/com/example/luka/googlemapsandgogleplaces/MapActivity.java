package com.example.luka.googlemapsandgogleplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.maps.android.SphericalUtil;
import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.luka.googlemapsandgogleplaces.Constants.*;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, Serializable {

    private static final String TAG = "MapActivity";

    //callback
    private LocationCallback mLocationCallback;

    //vars
    private boolean mTracingEnabled = false;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocation;
    private Run properties;
    Polyline line;
    Button btnStart;
    Button btnStop;
    Button btnPause;
    TextView tvTime;
    TextView tvDistance;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime;

    Handler handler;

    int Seconds, Minutes, MilliSeconds;
    int deltaSeconds=0;
    double deltaMeters=0;

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
        handler = new Handler();
        properties = new Run();
        tvDistance = findViewById(R.id.tvDistance);
        tvDistance.setText("0 m");
        tvTime = findViewById(R.id.tvTime);
        tvTime.setText("00:00:00");
        MillisecondTime = 0L;
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
                mLocationRequest.setFastestInterval(2000);

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
                mTracingEnabled = false;
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
                properties.runDuration = (String) tvTime.getText();

                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);
                stopLocationUpdates();
                mTracingEnabled = false;

                Intent intent = new Intent(MapActivity.this, ResultPreviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(INTENT_PREVIEW, properties);
                intent.putExtra(SOURCE_NAME, "MapActivity");
                mMap.clear();

                startActivity(intent);

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
                Location location = null;

                for (Location tempLocation : locationResult.getLocations()) {
                    if (location == null) {
                        location = tempLocation;
                    }
                    if (tempLocation.hasAccuracy())
                        if (tempLocation.getAccuracy() < location.getAccuracy()) {
                            location = tempLocation;
                        }
                }


                SerializableLatLng serLatLng = new SerializableLatLng(new LatLng(location != null ? location.getLatitude() : 0, location != null ? location.getLongitude() : 0));
                properties.points.get(properties.points.size() - 1).add(serLatLng);
                //points.add(new LatLng(location.getLatitude(),location.getLongitude()));
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                for (int z = 0; z < properties.points.get(properties.points.size() - 1).size(); z++) {
                    SerializableLatLng point = properties.points.get(properties.points.size() - 1).get(z);

                    options.add(point.getLatLng());
                    MapDisplayUtil.isMinLat(point.getLatLng().latitude);
                    MapDisplayUtil.isMaxLat(point.getLatLng().latitude);
                    MapDisplayUtil.isMinLng(point.getLatLng().longitude);
                    MapDisplayUtil.isMaxLng(point.getLatLng().longitude);
                }
                line = mMap.addPolyline(options);
                moveCamera(MapDisplayUtil.getCenter());
                Log.d(TAG, "onLocationResult: drawng route");
                Toast.makeText(MapActivity.this, "Coords: Lat: " + currentLocation.getLatitude() + ", Long: " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                updateDistanceAndSpeed();
            }
        };
    }

    private void updateDistanceAndSpeed() { // method called everytime location info is recieved so ve can calculate speed from delta s and delta t
        ArrayList<LatLng> arr = new ArrayList<>();
        DataPoint dataPoint;

        for (SerializableLatLng point : properties.points.get(properties.points.size() - 1)) {
            arr.add(point.getLatLng());
        }

        properties.runDistance = SphericalUtil.computeLength(arr);
        tvDistance.setText(" m" + properties.runDistance);
        deltaSeconds = Seconds -deltaSeconds;
        deltaMeters = properties.runDistance - deltaMeters;
        dataPoint = new DataPoint(Seconds,deltaMeters/deltaSeconds);

        if(properties.avgSpeed.size()!=0&&dataPoint.getY()>properties.avgSpeed.get(properties.avgSpeed.size()-1).getY()) {
            properties.maxSpeed = dataPoint.getY();
        }
        properties.avgSpeed.add(dataPoint);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates && mTracingEnabled) {
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

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getdevice location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation != null ? currentLocation.getLatitude() : 0, currentLocation != null ? currentLocation.getLongitude() : 0));
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "Unable to find current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: security exception" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng) {
        Log.d(TAG, "moveCamera: moving the camera to " + latLng.latitude + ", " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize map
                    initMap();
                }
            }
        }
    }

    public Runnable runnable;

    {
        runnable = new Runnable() {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
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
}
