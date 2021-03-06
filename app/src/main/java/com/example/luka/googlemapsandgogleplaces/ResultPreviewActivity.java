package com.example.luka.googlemapsandgogleplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import com.google.android.gms.maps.model.PolylineOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.example.luka.googlemapsandgogleplaces.Constants.*;


public class ResultPreviewActivity extends AppCompatActivity implements OnMapReadyCallback {

    //vars
    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    Run properties;
    TextView tvRunDuration;
    TextView tvRunDistance;
    Button btnSave;
    Button btnDismiss;
    Button btnDelete;
    boolean editMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_preview);
        getLocationPermission();
        initMap();
        initVars();
        manageRecivedIntents();

        initButtonListeners();
        GraphView graph = (GraphView) findViewById(R.id.graph);
        Log.d("Speed","array length in result activity: "+properties.avgSpeedTime.size());

        DataPoint points [] = new DataPoint[properties.avgSpeedTime.size()];
        for (int i = 0;i<properties.avgSpeedTime.size();i++){
            points[i]=new DataPoint(properties.avgSpeedTime.get(i),properties.avgSpeed.get(i));

        }
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(properties.maxSpeed);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(properties.avgSpeedTime.get(properties.avgSpeedTime.size()-1));

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.addSeries(series);
    }

    private void manageRecivedIntents() {
        Intent intent = getIntent();
        if (intent.getStringExtra(SOURCE_NAME).equals("MapActivity")) {
            properties = (Run) intent.getSerializableExtra(INTENT_PREVIEW);
            setTitle("Preview");
        } else if (intent.getStringExtra(SOURCE_NAME).equals("ResultListActivity")) {
            properties = (Run) intent.getSerializableExtra(INTENT_LIST_EDIT);
            editPreview();

        }
        fillRunInfo();
    }

    private void editPreview() {
        setTitle("Edit Preview");
        editMode=true;
        btnDelete.setVisibility(View.VISIBLE);
    }


    private void initButtonListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            Intent intent = new Intent(ResultPreviewActivity.this, ResultListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(INTENT_LIST, properties);

                if(editMode == true) {
                    intent.putExtra(OPERATION,"update");
                }
                else{
                    intent.putExtra(OPERATION,"insert");
                }

                mMap.clear();
                startActivity(intent);
            }
        });
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultPreviewActivity.this, ResultListActivity.class);
                intent.putExtra(INTENT_LIST, properties);
                intent.putExtra(OPERATION,"delete");
                finish();
                startActivity(intent);
            }
        });
    }

    private void fillRunInfo() {
        tvRunDuration.setText(properties.runDuration);
        tvRunDistance.setText(String.valueOf(properties.runDistance) + " m");
    }

    private void initVars() {
        tvRunDuration = findViewById(R.id.tvRunDuration);
        tvRunDistance = findViewById(R.id.tvRunDistance);
        btnSave = findViewById(R.id.btnSave);
        btnDismiss = findViewById(R.id.btnDismiss);
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.INVISIBLE);
        editMode = false;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ResultPreviewActivity.this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) { //On map ready enable blue dot
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            drawRunRoute();
            Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawRunRoute() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(properties.centerOfRoute.getLatLng(), DEFAULT_ZOOM));

        if (properties.points.get(0).isEmpty())
            return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < properties.points.size(); i++) {
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int y = 0; y < properties.points.get(i).size(); y++) {
                SerializableLatLng point = properties.points.get(i).get(y);
                options.add(point.getLatLng());
                builder.include(point.getLatLng());// optimize latter to not store all latlng points but only the farrest
            }
            mMap.addPolyline(options);
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);


    }
}
