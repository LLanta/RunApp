package com.example.luka.googlemapsandgogleplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_preview);
        TextView tvMessage = findViewById(R.id.tvMessage);

        Intent intent = getIntent();
        RunProperties properties = (RunProperties) intent.getSerializableExtra("2001");

        tvMessage.setText(String.valueOf(properties.points.get(0).get(1).lat));
    }
}
