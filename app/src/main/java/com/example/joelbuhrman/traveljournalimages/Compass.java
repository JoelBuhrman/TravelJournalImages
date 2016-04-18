package com.example.joelbuhrman.traveljournalimages;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

/**
 * Created by JoelBuhrman on 16-04-18.
 */
public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";
    private TextView textView;
    private String cam;
    private SensorManager sensorManager;
    private Sensor gsensor;
    private Sensor msensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;


    public Compass(Context context, TextView textView, String cam) {
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.textView = textView;
        this.cam = cam;
    }

    public void start() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void changeCam() {
        if (cam.equals("front")) {
            cam = "back";
        } else {
            cam = "front";
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];

                // mGravity = event.values;

                // Log.e(TAG, Float.toString(mGravity[0]));
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];
                // Log.e(TAG, Float.toString(event.values[0]));

            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Log.d(TAG, "azimuth (rad): " + azimuth);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;
                // Log.d(TAG, "azimuth (deg): " + azimuth);

                if (azimuth < 22.5 || azimuth > 337.5) {
                    if (cam.equals("back")) {
                        textView.setText("N");
                    } else {
                        textView.setText("S");
                    }
                } else if (azimuth >= 22.5 && azimuth < 67.5) {

                    if (cam.equals("back")) {
                        textView.setText("NE");
                    } else {
                        textView.setText("SW");
                    }
                } else if (azimuth > 67.5 && azimuth <= 112.5) {

                    if (cam.equals("back")) {
                        textView.setText("E");
                    } else {
                        textView.setText("W");
                    }
                } else if (azimuth > 112.5 && azimuth <= 157.5) {

                    if (cam.equals("back")) {
                        textView.setText("SE");
                    } else {
                        textView.setText("NW");
                    }
                } else if (azimuth > 157.5 && azimuth <= 202.5) {

                    if (cam.equals("back")) {
                        textView.setText("S");
                    } else {
                        textView.setText("N");
                    }
                } else if (azimuth > 202.5 && azimuth <= 247.5) {

                    if (cam.equals("back")) {
                        textView.setText("SW");
                    } else {
                        textView.setText("NE");
                    }
                } else if (azimuth > 247.5 && azimuth <= 292.5) {

                    if (cam.equals("back")) {
                        textView.setText("W");
                    } else {
                        textView.setText("E");
                    }

                } else if (azimuth >= 292.5 && azimuth < 337.5) {

                    if (cam.equals("back")) {
                        textView.setText("NW");
                    } else {
                        textView.setText("SE");
                    }
                }


            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}