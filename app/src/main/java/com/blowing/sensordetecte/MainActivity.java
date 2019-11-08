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
import android.widget.TextView;

import com.blowing.sensordetecte.constant.Constant;
import com.blowing.sensordetecte.service.SensorService;

public class MainActivity extends AppCompatActivity {


    private TextView lightTv; // 光照
    private TextView tempratureTv;// 电池温度
    private TextView manticTv; // 磁场强度
    private TextView voiceTv; // 声音强度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        registerReceiver(dataReceiver, new IntentFilter(Constant.action));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, SensorService.class);
            startService(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1000);
        }

    }

    private void initView() {
        lightTv = findViewById(R.id.light);
        manticTv = findViewById(R.id.mantic);
        tempratureTv = findViewById(R.id.temprature);
        voiceTv = findViewById(R.id.voice);

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
            if (Constant.action.equals(intent.getAction())) {
                if (lightTv != null) {
                    lightTv.setText(intent.getFloatExtra(Constant.LIGHT, 7.9f) + " lx");
                }

                if (tempratureTv != null) {
                    tempratureTv.setText(intent.getFloatExtra(Constant.TEMPERATUR, 25f)+ "℃");
                }

                if (manticTv != null) {
                    manticTv.setText(String.format("%.1f",intent.getFloatExtra(Constant.MAGNETIC,
                            35))+
                            " µT");
                }

                if (voiceTv != null) {
                    voiceTv.setText(String.format("%.1f",intent.getFloatExtra(Constant.VOICE, 40))+
                            " dB");
                }
            }
        }
    };
}
