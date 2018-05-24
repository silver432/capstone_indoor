package com.jasen.kimjaeseung.capstone_indoor;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mMarker;
    private Button mButton;
    private LatLng startPoint;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mButton = (Button)findViewById(R.id.main_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r = new Random();
                int randomHeading = r.nextInt(360);
                LatLng newLatLng = SphericalUtil.computeOffset(startPoint, 2, randomHeading);

                if (mMarker.isVisible()) mMarker.remove();

                mMarker = mMap.addMarker(new MarkerOptions().position(startPoint).title("Start Point"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));

                // Zoom in the Google Map
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19));

                startPoint = newLatLng;

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.RED);
                polylineOptions.width(5);
                arrayPoints.add(newLatLng);
                polylineOptions.addAll(arrayPoints);
                mMap.addPolyline(polylineOptions);
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

}
