package com.example.luka.googlemapsandgogleplaces;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RunProperties implements Serializable {
    public List<List<SerializableLatLng>> points = new ArrayList<>();
    public long runDuration;
    public float distance=0;
    public SerializableLatLng centerOfRoute;
    public double latMin;
    public double latMax;
    public double lngMin;
    public double lngMax;


}
