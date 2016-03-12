package cc.haoduoyu.umaru.utils;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 摇一摇管理器
 * Created by XP on 2016/3/11.
 */
public class ShakeManager {

    private static ShakeManager instance;
    private Context mContext;
    private Sensor mSensor;
    /**
     * 传感器检测变化的时间间隔
     */
    private static final int UPTATE_INTERVAL_TIME = 100;
    private long mLastUpdateTime;

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
            //现在检测时间
            long currentUpdateTime = System.currentTimeMillis();
            //两次检测的时间间隔
            long timeInterval = currentUpdateTime - mLastUpdateTime;
            if (timeInterval < UPTATE_INTERVAL_TIME) {
                return;
            }
            //现在的时间变成last时间
            mLastUpdateTime = currentUpdateTime;
//            int sensorType = event.sensor.getType();
            //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
            float[] values = event.values;
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
