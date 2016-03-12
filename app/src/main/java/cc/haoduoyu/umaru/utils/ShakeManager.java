package cc.haoduoyu.umaru.utils;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.apkfuns.logutils.LogUtils;

/**
 * 摇一摇管理器
 * Created by XP on 2016/3/11.
 */
public class ShakeManager {

    private static ShakeManager instance;
    private Context mContext;
    private Sensor mSensor;

    private ShakeManager(Context context) {
        mContext = context;
    }

    public static ShakeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ShakeManager(context);
        }
        return instance;
    }


    public void startShakeListener(final ISensor iSensor) {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = new Sensor(iSensor);
        sensorManager.registerListener(mSensor, sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

    }

    private class Sensor implements SensorEventListener {

        private ISensor iSensor;

        public Sensor(ISensor iSensor) {
            this.iSensor = iSensor;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
//            int sensorType = event.sensor.getType();
            //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
            float[] values = event.values;
//            LogUtils.d(sensorType);
            float force = Math.abs(values[0]) + Math.abs(values[1]) + Math.abs(values[2]);
//            LogUtils.d("sensor x values[0] = " + values[0] + " sensor y values[1] = " + values[1] + " sensor  values[2] = " + values[2]);
            if (iSensor != null)
                iSensor.onSensorChange(force);
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

        }
    }


    public void cancel() {
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        //取消传感器监听
        sensorManager.unregisterListener(mSensor);

    }

    public interface ISensor {
        void onSensorChange(float force);
    }

}
