package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.test.Utils._3dVector;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Sensor acceleratorSensor;
    private Sensor gyroscopeSensor;
    private SensorManager sensorManager;
    private double x_velocity = 0;
    private double z_velocity = 0;

    private double x = 0;
    private double z = 0;

    private double lastEventTimestampGy;
    private double lastEventTimestampAc;
    private _3dVector theta;

    private _3dVector gyroscope;
    private _3dVector accelerator;
    private final int REFRESH_RATE = 20;
    private final double GRAVITY = 9.8;

    private ArrayList<Integer> heights;
    private ArrayList<Integer> surfaceX;


    private Boolean started = false;
    GraphView graphView;

    Button btnStart;
    Button btnEnd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Timer timer = new Timer();
        heights = new ArrayList<>();
        surfaceX = new ArrayList<>();
        btnStart = findViewById(R.id.btn_start);
        btnEnd = findViewById(R.id.btn_end);
        graphView = findViewById(R.id.idGraphView);

        theta = new _3dVector(0, 0, 0);
        gyroscope = new _3dVector(0, 0, 0);
        accelerator = new _3dVector(0, 0, 0);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = true;
                Toast.makeText(getBaseContext(), "Scanning Begun!", Toast.LENGTH_LONG).show();
                graphView.setVisibility(View.INVISIBLE);
            }
        });
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                started = false;
                Toast.makeText(getBaseContext(), "Scanning Finished!", Toast.LENGTH_LONG).show();
                timer.cancel();
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(data());
                graphView.addSeries(series);
                graphView.setVisibility(View.VISIBLE);
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleSensorEvent(gyroscope
                        , accelerator
                        , (double) (REFRESH_RATE) / 1000);
            }
        }, 0, REFRESH_RATE);

        initializeSensors();
        graphView.setTitle("My Graph View");
        graphView.setTitleColor(R.color.purple_200);
        graphView.setTitleTextSize(18);

    }

    private DataPoint[] data() {
        int n = Math.min(surfaceX.size(), heights.size());
        DataPoint[] values = new DataPoint[n];     //creating an object of type DataPoint[] of size 'n'
        for(int i=0;i<n;i++){
            DataPoint v = new DataPoint(surfaceX.get(i), heights.get(i));
            values[i] = v;
        }
        return values;
    }

    private void handleSensorEvent(_3dVector gyroscope, _3dVector accelerator, double deltaT){
        if(started) {
            theta = new _3dVector(gyroscope.x * deltaT + theta.x,
                    gyroscope.y * deltaT + theta.y,
                    gyroscope.z * deltaT + theta.z);


            double x_accelerator = accelerator.x * Math.cos(theta.y) - accelerator.z * Math.sin(theta.y);
            double z_accelerator = accelerator.x * Math.sin(theta.y) + accelerator.z * Math.cos(theta.y);
            double v2x = deltaT * x_accelerator + x_velocity;
            x += (v2x * v2x - x_velocity * x_velocity) / (2 * x_accelerator);
            x_velocity = v2x;


            double v2z = deltaT * z_accelerator + z_velocity;
            z += (v2z * v2z - z_velocity * z_velocity) / (2 * z_accelerator);
            z_velocity = v2z;
        }



    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acceleratorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(accelListener, acceleratorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private SensorEventListener gyroscopeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (started) {
                double deltaT = (sensorEvent.timestamp - lastEventTimestampGy) / 1e9;
                if (deltaT > 0) {
                    gyroscope = new _3dVector(sensorEvent.values[0],
                            sensorEvent.values[1],
                            sensorEvent.values[2]);
                }
                lastEventTimestampGy = sensorEvent.timestamp;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener accelListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (started) {
                double deltaT = (sensorEvent.timestamp - lastEventTimestampAc) / 1e9;
                if (deltaT > 0) {
                    accelerator = new _3dVector(sensorEvent.values[0],
                            sensorEvent.values[1],
                            sensorEvent.values[2]);
                }
                lastEventTimestampAc = sensorEvent.timestamp;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


}