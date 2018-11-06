package com.example.luka.googlemapsandgogleplaces;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

class SerializableLatLng implements Serializable {
    private double lat;
    private double lon;
    SerializableLatLng(LatLng orig) {
        this.lat=orig.latitude;
        this.lon = orig.longitude;

    }

    LatLng getLatLng(){
        return new LatLng(lat,lon);
    }
}
