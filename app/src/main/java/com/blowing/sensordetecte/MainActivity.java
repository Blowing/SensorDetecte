package com.blowing.sensordetecte;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.blowing.sensordetecte.service.SensorService;

public class MainActivity extends AppCompatActivity {

    private String action = "com.blowing.data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, SensorService.class);
            startService(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1000);
        }

        registerReceiver(dataReceiver, new IntentFilter(action));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, SensorService.class);
            startService(intent);
        }
    }


    @Override
    protected void onDestroy() {
        if (dataReceiver != null) {
            unregisterReceiver(dataReceiver);
        }
        super.onDestroy();
    }

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (action.equals(intent.getAction())) {

            }
        }
    };
}
