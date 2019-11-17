package com.blowing.sensordetecte.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.blowing.sensordetecte.MyMediaRecorder;
import com.blowing.sensordetecte.constant.Constant;
import com.blowing.sensordetecte.util.FileUtil;

import java.io.File;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private MyMediaRecorder mRecorder;
    private int msgWhat = 0x123;
    private int refreshTime = 100;

    private float light ;
    private float temperatur;
    private float voice;
    private float magnetic;

    public SensorService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("wujie", "service初始化");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRecorder = new MyMediaRecorder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startDetecte();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        unregisterReceiver(mBatInfoReceiver);
        if (mRecorder != null) {
            mRecorder.delete();
        }
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LIGHT:
                light = value;
                Log.i("wujie", "光照"+ value);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetic = Math.abs(value);
                break;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 开始注册监听传感器数据
     */
    private void startDetecte() {
        // 检测光照和磁场
        if (sensorManager != null) {
            //光照强度
            Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

            //磁场
            Sensor magneticSensor =  sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor,
                        SensorManager.SENSOR_DELAY_UI);
                Log.i("wujie1", "注册光照");
            }


            if (magneticSensor != null) {
                Log.i("wujie1", "注册磁场");
                sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
            }
         }
        // 检测声压
        File file = FileUtil.createFile("temp.amr");
        if (file != null) {
            Log.v("file", "file =" + file.getAbsolutePath());
            startRecord(file);
        }

        // 检测电池温度
        registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msgWhat != msg.what) {
                return;
            }
            float volume = mRecorder.getMaxAmplitude();  //获取声压值
            if(volume > 0 && volume < 1000000) {
               voice =  20 * (float)(Math.log10(volume));  //将声压值转为分贝值
            }

            // 发送数据给前端展示
            Intent intent = new Intent(Constant.action);
            intent.putExtra(Constant.LIGHT, light);
            intent.putExtra(Constant.VOICE, voice);
            intent.putExtra(Constant.MAGNETIC, magnetic);
            intent.putExtra(Constant.TEMPERATUR,temperatur);
            sendBroadcast(intent);

            handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
        }
    };

    private void startListenAudio() {
        handler.sendEmptyMessageDelayed(msgWhat, refreshTime);
    }

    /**
     * 开始记录
     * @param fFile
     */
    public void startRecord(File fFile){
        try{
            mRecorder.setMyRecAudioFile(fFile);
            if (mRecorder.startRecorder()) {
                startListenAudio();
            }else{
                Toast.makeText(this, "启动录音失败", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Toast.makeText(this, "录音机已被占用或录音权限被禁止", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    /**
     * 注册电池信息的监听器
     */
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {

                int batteryL = intent.getIntExtra("level", 0);	  //目前电量
                int batteryT = intent.getIntExtra("temperature", 0);  //电池温度
                temperatur = batteryT/10.0f;
                Log.i("wujie1", "电量"+batteryL + "---" + "温度"+ batteryT);
            }
        }
    };

}
