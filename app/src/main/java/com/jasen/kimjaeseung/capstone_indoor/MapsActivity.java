package com.jasen.kimjaeseung.capstone_indoor;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private Marker mMarker;
    private Button mButton;
    private LatLng startPoint;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    private SensorManager sm;
    private Sensor accSensor;
    private Sensor magnetSensor;
    private Sensor stepSensor;
    float[] mGravity;
    float[] mGeomagnetic;
    float rotation = 0;
    private int step = 0;
    private boolean isStart = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        stepSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mButton = (Button) findViewById(R.id.main_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movingEvent();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        startPoint = new LatLng(37.450340, 126.657292);
        mMarker = mMap.addMarker(new MarkerOptions().position(startPoint).title("Start Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19));


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
//            movingEvent();
            if (isStart){
                movingEvent();
            }
            isStart = true;
        }

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

//                rotation = -orientation[0] * 360 / (2 * 3.14159f);

                rotation = (float) (Math.toDegrees(orientation[0]) + 360) % 360;

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void movingEvent() {
        LatLng newLatLng = SphericalUtil.computeOffset(startPoint, 1, rotation);

        if (mMarker.isVisible()) mMarker.remove();

        mMarker = mMap.addMarker(new MarkerOptions().position(startPoint).title("Start Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        arrayPoints.add(startPoint);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);

        startPoint = newLatLng;
    }
}
