package com.example.luka.googlemapsandgogleplaces;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RunProperties implements Parcelable {
    public List<List<LatLng>> points = new ArrayList<>();
    public long runDuration;

    protected RunProperties(Parcel in) {
        runDuration = in.readLong();
        points = in.readArrayList(ArrayList.class.getClassLoader());
    }

    public RunProperties(long runDuration, List<List<LatLng>> points) {
        this.runDuration=runDuration;
        this.points=points;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(runDuration);
        dest.writeList(points);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RunProperties> CREATOR = new Parcelable.Creator<RunProperties>() {
        @Override
        public RunProperties createFromParcel(Parcel in) {
            return new RunProperties(in);
        }

        @Override
        public RunProperties[] newArray(int size) {
            return new RunProperties[size];
        }
    };
}
