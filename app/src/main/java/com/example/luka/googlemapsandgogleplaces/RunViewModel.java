package com.example.luka.googlemapsandgogleplaces;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RunViewModel extends AndroidViewModel {
    RunRepository repository;
    LiveData<List<Run>> allRuns;

    public RunViewModel(@NonNull Application application) {
        super(application);
        repository = new RunRepository(application);
        allRuns = repository.getAllRuns();
    }

    public void insert(Run run) {
        repository.insert(run);
    }

    public void update(Run run) {
        repository.update(run);
    }

    public void delete(Run run) {
        repository.delete(run);
    }

    public void deleteAll() {
        repository.deleteAllRuns();
    }

    public LiveData<List<Run>> getAllRuns() {
        return allRuns;
    }
}
