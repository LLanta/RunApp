package com.example.luka.googlemapsandgogleplaces;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isServicesUpToDate()){
            init();
        }
    }

    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);
            }
        });
    }
    
    public boolean isServicesUpToDate(){
        Log.d(TAG, "isServicesUpToDate: checking if services are up to date");
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(availability == ConnectionResult.SUCCESS){
            //everything is fine and user can make g play services request
            Log.d(TAG, "isServicesUpToDate: user can use services");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availability)){
            //resolvable error appeared
            Log.d(TAG, "isServicesUpToDate: resolvable error appeared");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, availability,ERROR_DIALOG_REQUEST);//getting dialog from google
            dialog.show();
        }
        else{
            Toast.makeText(this,"We can't make request ",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
