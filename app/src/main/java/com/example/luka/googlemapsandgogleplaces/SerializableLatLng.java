package com.example.luka.googlemapsandgogleplaces;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SerializableLatLng implements Serializable {
    public double lat;
    public double lon;
    public SerializableLatLng(LatLng orig) {
        this.lat=orig.latitude;
        this.lon = orig.longitude;

    }

    public LatLng getLatLng(){
        LatLng latLng= new LatLng(lat,lon);
        return latLng;
    }
}
