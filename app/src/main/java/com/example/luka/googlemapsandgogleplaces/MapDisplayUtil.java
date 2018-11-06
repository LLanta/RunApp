package com.example.luka.googlemapsandgogleplaces;


import com.google.android.gms.maps.model.LatLng;

class MapDisplayUtil {
    static double  minLat,maxLat,minLng,maxLng;

    static void resetConstrains(){
        minLat = 90;
        maxLat = -90;

        minLng=180;
        maxLng=-180;
    }

    static boolean isMinLat(double min){
        if(min <= minLat){
            minLat = min;
            return true;
        }
        else return false;
    }

    static boolean isMaxLat(double max){
        if(max >= maxLat){
            maxLat = max;
            return true;
        }
        else return false;
    }

    static boolean isMinLng(double min){
        if(min <= minLng){
            minLng = min;
            return true;
        }
        else return false;
    }

    static boolean isMaxLng(double max){
        if(max >= maxLng){
            maxLng = max;
            return true;
        }
        else return false;
    }

    static LatLng getCenter (){
        return new LatLng((maxLat+minLat)/2,(maxLng+minLng)/2);
    }

}
