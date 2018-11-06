package com.example.luka.googlemapsandgogleplaces;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RunDao {

    @Insert
    void insert (Run run);

    @Update
    void update (Run run);

    @Delete
    void delete (Run run);

    @Query("DELETE FROM table_run")
    void deleteAllRuns();

    @Query("SELECT * FROM table_run")
    LiveData<List<Run>> getAllRuns();
}
