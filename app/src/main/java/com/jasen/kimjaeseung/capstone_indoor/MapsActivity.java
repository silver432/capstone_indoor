package com.jasen.kimjaeseung.capstone_indoor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private LatLng point1 = new LatLng(37.450562,126.657607);
    private LatLng point2 = new LatLng(37.450616,126.657165);
    private LatLng point3 = new LatLng(37.450206,126.657410);
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

//        mButton = (Button) findViewById(R.id.main_button);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                movingEvent();
//            }
//        });
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
        mMarker = mMap.addMarker(new MarkerOptions().position(startPoint).title("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_face_black_18dp)).flat(true));
        mMap.addMarker(new MarkerOptions().position(point1).title("Starbucks").icon(BitmapDescriptorFactory.fromResource(R.drawable.starbucks)));
        mMap.addMarker(new MarkerOptions().position(point2).title("McDonalds").icon(BitmapDescriptorFactory.fromResource(R.drawable.mcdonald)));
        mMap.addMarker(new MarkerOptions().position(point3).title("Nike").icon(BitmapDescriptorFactory.fromResource(R.drawable.nike)));
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
            if (isStart){
                movingEvent();
                if (distance(mMarker.getPosition().latitude,mMarker.getPosition().longitude,point1.latitude,point1.longitude)<5){   //point1에 가까울때
                    createNoti(R.drawable.starbucks,"Welcome to Starbucks!","starbucks");
                }
                if (distance(mMarker.getPosition().latitude,mMarker.getPosition().longitude,point2.latitude,point2.longitude)<5){   //point2에 가까울때
                    createNoti(R.drawable.mcdonald,"Welcome to McDonalds","mcdonalds");
                }
                if (distance(mMarker.getPosition().latitude,mMarker.getPosition().longitude,point3.latitude,point3.longitude)<5){   //point3에 가까울때
                    createNoti(R.drawable.nike,"Welcome to Nike","nike");
                }
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

        mMarker = mMap.addMarker(new MarkerOptions().position(startPoint).title("My location").icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_face_black_18dp)).flat(true));
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

    public float distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    @SuppressWarnings("deprecation")
    private void createNoti(int drawable,String text,String shopName){
        Intent intent = new Intent(this,AdActivity.class);
        intent.putExtra("ad",shopName);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.baseline_face_black_18dp)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), drawable))
                        .setContentTitle("Ad Information")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setContentIntent(resultPendingIntent)
                        .setContentText(text);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,mBuilder.build());
    }
}
