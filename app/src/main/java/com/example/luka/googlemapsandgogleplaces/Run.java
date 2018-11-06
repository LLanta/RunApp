package com.example.luka.googlemapsandgogleplaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "table_run")
class Run implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int uId;
    @TypeConverters({Converters.class})
    ArrayList<ArrayList<SerializableLatLng>> points = new ArrayList<>();
    String runDuration;
    double runDistance=0;
    @TypeConverters({Converters.class})
    SerializableLatLng centerOfRoute;
    double latMin;
    double latMax;
    double lngMin;
    double lngMax;

    public Run (){
    }

    public Run(ArrayList<ArrayList<SerializableLatLng>> points, String runDuration, double runDistance, SerializableLatLng centerOfRoute, double latMin, double latMax, double lngMin, double lngMax) {
        this.points.addAll(points);
        this.runDuration = runDuration;
        this.runDistance = runDistance;
        this.centerOfRoute = centerOfRoute;
        this.latMin = latMin;
        this.latMax = latMax;
        this.lngMin = lngMin;
        this.lngMax = lngMax;
    }
}
