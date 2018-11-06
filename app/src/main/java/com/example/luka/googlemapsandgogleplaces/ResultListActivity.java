package com.example.luka.googlemapsandgogleplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import static com.example.luka.googlemapsandgogleplaces.Constants.INTENT_LIST;

public class ResultListActivity extends AppCompatActivity {
    Run properties;
    RecyclerView recyclerView;
    RunAdapter adapter;
    RunViewModel runViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);
        Intent intent = getIntent();
        properties = (Run) intent.getSerializableExtra(INTENT_LIST);
        Log.d("Prop",properties.runDuration);

        recyclerView = findViewById(R.id.rv_run_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();

        adapter = new RunAdapter();
        recyclerView.setAdapter(adapter);
        initViewModel();
    }

    private void initViewModel() {
        runViewModel = ViewModelProviders.of(this).get(RunViewModel.class);
        ((RunViewModel) runViewModel).getAllRuns().observe(this, new Observer<List<Run>>() {
            @Override
            public void onChanged(List<Run> runs) {
                adapter.setRuns(runs);
            }
        });
    }
}
