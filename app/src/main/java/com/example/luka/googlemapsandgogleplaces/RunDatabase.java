package com.example.luka.googlemapsandgogleplaces;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Run.class}, version = 1)
public abstract class RunDatabase extends RoomDatabase {

    private static RunDatabase instance;

    public abstract RunDao runDao();

    public static synchronized RunDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    RunDatabase.class, "run_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private RunDao runDao;

        private PopulateDbAsyncTask(RunDatabase db) {
            runDao = db.runDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<ArrayList<SerializableLatLng>> points = new ArrayList<>();
            ArrayList<SerializableLatLng> arr = new ArrayList<>();
            arr.add(new SerializableLatLng(new LatLng(34,54)));
            points.add(arr);
            runDao.insert(new Run(points,"02:54:00",43,new SerializableLatLng(new LatLng(2,2)),0,0,0,0));
            return null;
        }
    }

}
