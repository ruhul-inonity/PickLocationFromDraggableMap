package com.inonity.picklocationfromdraggablemap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import static com.inonity.picklocationfromdraggablemap.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private TextView resutText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT < 22)
            setStatusBarTranslucent(false);
        else
            setStatusBarTranslucent(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        resutText = (TextView) findViewById(R.id.dragg_result);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        configureCameraIdle();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraIdleListener(onCameraIdleListener);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }}



    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = mMap.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(MapsActivity.this);

                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = addressList.get(0).getAddressLine(0);
                        String country = addressList.get(0).getCountryName();
                        if (!locality.isEmpty() && !country.isEmpty())
                            resutText.setText(locality + "  " + country);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


}
