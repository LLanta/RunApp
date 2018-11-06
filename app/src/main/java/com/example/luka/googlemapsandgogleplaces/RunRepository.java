package com.example.luka.googlemapsandgogleplaces;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class RunRepository {
    private RunDao runDao;
    private LiveData<List<Run>> allRuns;

    public RunRepository(Application application){
        RunDatabase database = RunDatabase.getInstance(application);
        runDao = database.runDao();
        allRuns = runDao.getAllRuns();
    }

    public void insert (Run run){
        new InsertRunAsyncTask(runDao).execute(run);
    }

    public void update (Run run){
        new UpdateRunAsyncTask(runDao).execute(run);
    }

    public void delete (Run run){
        new DeleteRunAsyncTask(runDao).execute(run);
    }

    public void deleteAllRuns (){
        new DeleteAllRunsAsyncTask(runDao).execute();
    }

    public LiveData<List<Run>> getAllRuns (){
        return allRuns;
    }

    private static class InsertRunAsyncTask extends AsyncTask <Run,Void,Void>{
        private RunDao runDao;

        private InsertRunAsyncTask(RunDao runDao){
            this.runDao=runDao;
        }

        @Override
        protected Void doInBackground(Run... runs) {
            runDao.insert(runs[0]);
            return null;
        }
    }
    private static class UpdateRunAsyncTask extends AsyncTask <Run,Void,Void>{
        private RunDao runDao;

        private UpdateRunAsyncTask(RunDao runDao){
            this.runDao=runDao;
        }

        @Override
        protected Void doInBackground(Run... runs) {
            runDao.update(runs[0]);
            return null;
        }
    }
    private static class DeleteRunAsyncTask extends AsyncTask <Run,Void,Void>{
        private RunDao runDao;

        private DeleteRunAsyncTask(RunDao runDao){
            this.runDao=runDao;
        }

        @Override
        protected Void doInBackground(Run... runs) {
            runDao.delete(runs[0]);
            return null;
        }
    }
    private static class DeleteAllRunsAsyncTask extends AsyncTask <Void,Void,Void>{
        private RunDao runDao;

        private DeleteAllRunsAsyncTask(RunDao runDao){
            this.runDao=runDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            runDao.deleteAllRuns();
            return null;
        }
    }
}
