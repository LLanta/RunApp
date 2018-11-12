package com.example.luka.googlemapsandgogleplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import static com.example.luka.googlemapsandgogleplaces.Constants.EDIT_RUN_REQUEST;
import static com.example.luka.googlemapsandgogleplaces.Constants.INTENT_LIST;
import static com.example.luka.googlemapsandgogleplaces.Constants.INTENT_LIST_EDIT;
import static com.example.luka.googlemapsandgogleplaces.Constants.OPERATION;
import static com.example.luka.googlemapsandgogleplaces.Constants.SOURCE_NAME;

public class ResultListActivity extends AppCompatActivity {
    Run properties;
    RecyclerView recyclerView;
    RunAdapter adapter;
    RunViewModel runViewModel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);
        initAll();

    }

    private void initAll() {

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Run list");
        setSupportActionBar(toolbar);

        DrawerUtil.getDrawer(this,toolbar);

        recyclerView = findViewById(R.id.rv_run_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();

        adapter = new RunAdapter();
        recyclerView.setAdapter(adapter);

        runViewModel = ViewModelProviders.of(this).get(RunViewModel.class);
        ((RunViewModel) runViewModel).getAllRuns().observe(this, new Observer<List<Run>>() {
            @Override
            public void onChanged(List<Run> runs) {
                adapter.setRuns(runs);
            }
        });

        Intent intent = getIntent();
        properties = (Run) intent.getSerializableExtra(INTENT_LIST);
        String operation = intent.getStringExtra(OPERATION);
        if (operation!=null) {
            updateList(operation);
        }

        adapter.setOnItemClickListener(new RunAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Run run) {
                Intent intent = new Intent(ResultListActivity.this, ResultPreviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(INTENT_LIST_EDIT, run);
                intent.putExtra(SOURCE_NAME, "ResultListActivity");
                startActivity(intent);
            }
        });
    }

    private void updateList(String operation) {
        switch (operation) {
            case "insert":
                insertRun();
                break;
            case "update":
                updateRun();
                break;
            case "delete":
                deleteRun();
                break;
        }
    }

    private void updateRun() {
        runViewModel.update(properties);
    }


    private void insertRun() {
        runViewModel.insert(properties);
    }

    private void deleteRun() {
        runViewModel.delete(properties);
    }

}
