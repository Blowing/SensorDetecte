package com.blowing.sensordetecte;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.blowing.sensordetecte.constant.Constant;
import com.blowing.sensordetecte.service.SensorService;
import com.blowing.sensordetecte.util.WindowUtil;

public class MainActivity extends AppCompatActivity {


    private TextView lightTv; // 光照
    private TextView tempratureTv;// 电池温度
    private TextView manticTv; // 磁场强度
    private TextView voiceTv; // 声音强度

    private TextView lightTip;
    private TextView temperatureTip;
    private TextView manticTip;
    private TextView voiceTip;

    private boolean isBackground = false; //是否是在后台

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        registerReceiver(dataReceiver, new IntentFilter(Constant.action));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.canDrawOverlays(this))
            {
                //有悬浮窗权限开启服务绑定 绑定权限
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, SensorService.class);
                    startService(intent);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO
                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SYSTEM_ALERT_WINDOW},
                            1000);
                }

            }else{
                //没有悬浮窗权限m,去开启悬浮窗权限
                try{
                    Intent  intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivityForResult(intent, 10001);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        isBackground = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBackground = false;
    }

    private void initView() {
        lightTv = findViewById(R.id.light);
        manticTv = findViewById(R.id.mantic);
        tempratureTv = findViewById(R.id.temprature);
        voiceTv = findViewById(R.id.voice);

        lightTip = findViewById(R.id.light_tip);
        temperatureTip = findViewById(R.id.temprature_tip);
        manticTip = findViewById(R.id.mantic_tip);
        voiceTip = findViewById(R.id.voice_tip);
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

    /**
     * 2.光照强度在0-50000LUX时，在实时数据后面显示‘安全光照’;
     * 在50000-100000LUX时，在实时数据后显示‘较强光照’；
     * 当数据超过100000LUX时，在实时数据后显示‘危险光照，及时避离’，并进行系统提示：内容为（‘当前光照强度超过100000LUX，危险光照，及时避离’）APP
     * 在后台运行，无论有没有打开APP，只要传感器检测数据大于100000Lux（1000000uT，60度，120dB，），都会系统提示，点击一下就会关闭提示。
     * 3．磁场、电池温度和声压的设计和（2）中一样，磁场的分区为0-10000uT‘安全辐射’；10000-1000000uT‘较强辐射’；大于1000000uT
     * ‘危险辐射，及时避离’（系统提示）。 电池温度的分区为0-45‘安全温度’；45-60‘较高温度’；大于60‘危险温度，停止使用’（系统提示）。
     * 声压的分区0-85dB‘安全声压；85-120‘较强噪音’；大于120‘危险噪音，及时避离’（系统提示）。（系统提示：当前----，-----。）
     */

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.action.equals(intent.getAction())) {
                if (lightTv != null) {

                    float light = intent.getFloatExtra(Constant.LIGHT, 7.9f);
                    String tip;
                    if (light <=50000) {
                        tip = "安全光照";
                    } else if (light <= 100000) {
                        tip = "较强光照";
                    } else {
                        tip = "危险光照，及时避离";
                        showTip("当前光照强度超过100000LUX，危险光照，及时避离");
//                        WindowUtil.showPopupWindow(getApplicationContext(), "");
                    }
                    lightTv.setText(light + " lx");
                    lightTip.setText(tip);
                }

                if (tempratureTv != null) {
                    float temprature = intent.getFloatExtra(Constant.TEMPERATUR, 25f);
                    String tip;
                    if (temprature <= 45) {
                        tip = "安全温度";
                    } else if (temprature <= 60) {
                        tip = "较高温度";
                    } else {
                        tip = "危险温度,停止使用";
                        showTip(tip);
                    }
                    temperatureTip.setText(tip);
                    tempratureTv.setText(temprature+ "℃");
                }

                if (manticTv != null) {
                    float mantic = intent.getFloatExtra(Constant.MAGNETIC,
                            35);
                    String tip;
                    if (mantic <= 10000) {
                        tip = "安全辐射";
                    } else if (mantic <= 1000000) {
                        tip = "较强辐射";
                    } else {
                        tip = "危险辐射，及时避离";
                        showTip(tip);
                    }
                    manticTip.setText(tip);
                    manticTv.setText(String.format("%.1f",mantic)+
                            " µT");
                }

                if (voiceTv != null) {
                    float voice = intent.getFloatExtra(Constant.VOICE, 40);
                    String tip;
                    if (voice <= 85) {
                        tip = "完全声压";
                    } else if(voice <= 120){
                        tip = "较强噪音";
                    } else {
                        tip = "危险噪音，及时避离";
                        showTip("当前声压"+voice+"db，"+ tip);
                    }
                    voiceTip.setText(tip);
                    voiceTv.setText(String.format("%.1f",voice)+
                            " dB");
                }
            }
        }
    };

    private void showTip(String tip) {
        if (isBackground) {
            WindowUtil.showPopupWindow(getApplicationContext(), tip);
        }
    }
}
