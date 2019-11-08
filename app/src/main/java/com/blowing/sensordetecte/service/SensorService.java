package com.blowing.sensordetecte.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;

    public SensorService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 开始注册监听传感器数据
     */
    private void startDetecte() {
        if (sensorManager != null) {
            //光照强度
            Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            //温度
            Sensor tempeRatureSensor =
                    sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

            Sensor mangeticSensor =  sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor,
                        SensorManager.SENSOR_DELAY_UI);
            }

            if (tempeRatureSensor != null) {

            }
         }
    }

}
