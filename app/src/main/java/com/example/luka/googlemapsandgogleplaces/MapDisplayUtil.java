package com.example.luka.googlemapsandgogleplaces;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class MapDisplayUtil {
    public static double  minLat,maxLat,minLng,maxLng;

    public static void resetConstrains(){
        minLat = 90;
        maxLat = -90;

        minLng=180;
        maxLng=-180;
    }

    public static boolean isMinLat(double min){
        if(min <= minLat){
            minLat = min;
            return true;
        }
        else return false;
    }

    public static boolean isMaxLat(double max){
        if(max >= maxLat){
            maxLat = max;
            return true;
        }
        else return false;
    }

    public static boolean isMinLng(double min){
        if(min <= minLng){
            minLng = min;
            return true;
        }
        else return false;
    }

    public static boolean isMaxLng(double max){
        if(max >= maxLng){
            maxLng = max;
            return true;
        }
        else return false;
    }

    public static LatLng getCenter (){
        Log.d("LAT", "minLat: "+ minLat+" , maxLat: "+ maxLat+" center Lat: "+ (maxLat+minLat)/2);
        Log.d("LNG", "minLng: "+ minLng+" , maxLng: "+ maxLng+" center Lng: "+ (maxLng+minLng)/2);

        return new LatLng((maxLat+minLat)/2,(maxLng+minLng)/2);
    }

}
