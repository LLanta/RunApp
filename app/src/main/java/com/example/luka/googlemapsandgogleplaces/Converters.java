package com.example.luka.googlemapsandgogleplaces;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.series.DataPoint;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static ArrayList<DataPoint> fromStringDataPointSpeed(String value) {
        Type listType = new TypeToken<ArrayList<DataPoint>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayListDataPointSpeed(ArrayList<DataPoint> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static ArrayList<ArrayList<SerializableLatLng>> fromString(String value) {
        Type listType = new TypeToken<ArrayList<ArrayList<SerializableLatLng>>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<ArrayList<SerializableLatLng>> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static SerializableLatLng fromStringLatLng(String value) {
        Type type = new TypeToken<SerializableLatLng>() {
        }.getType();
        return new Gson().fromJson(value, type);
    }

    @TypeConverter
    public static String fromLatLng(SerializableLatLng latLng) {
        Gson gson = new Gson();
        String json = gson.toJson(latLng);
        return json;
    }

}


