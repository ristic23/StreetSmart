package com.jovan_ristic.streetsmart.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jovan_ristic.streetsmart.R;
import com.jovan_ristic.streetsmart.helpers.AppManager;
import com.jovan_ristic.streetsmart.helpers.GPSTracker;

public class MapActivity extends AppCompatActivity  implements View.OnClickListener, OnMapReadyCallback
{
    ImageView btnProfile, btnFriends, btnRankList;

    private GoogleMap mMap;
    public static final int REQUEST_LOCATION_CODE = 99;

    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_map);
        }
        catch(Exception | OutOfMemoryError e)
        {
            Toast.makeText(this, getResources().getString(R.string.errorMsg), Toast.LENGTH_SHORT).show();
        }
        gps = new GPSTracker(this);

        if(!gps.isGPSEnabled())
            gps.showGPSSettingsAlert();
        if(!gps.isNetworkEnabled())
            gps.showWifiSettingsAlert();
        initLayout();
        initListeners();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
        }

        AppManager.getInstance().setMarkerLayout(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
                        mapFragment.getMapAsync(this);
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(MapActivity.this);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        AppManager.getInstance().setGoogleMapAppManager(googleMap);
        AppManager.getInstance().setGoogleMapActive(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        if(AppManager.getInstance().getLatitude() != 0 || AppManager.getInstance().getLongitude() != 0)
        {
            AppManager.getInstance().setMyLocationMarker();
        }
        else
        {
            LatLng nisMarker = new LatLng(43.323239, 21.895246);
            mMap.addMarker(new MarkerOptions().position(nisMarker).title("Marker in Nis"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(nisMarker));
        }
        AppManager.getInstance().getUsersLocation();
    }

    private void initListeners() {
        btnProfile.setOnClickListener(this);
        btnFriends.setOnClickListener(this);
        btnRankList.setOnClickListener(this);
    }

    private void initLayout() {
        btnProfile = findViewById(R.id.profile_btn);
        btnFriends = findViewById(R.id.friends_btn);
        btnRankList = findViewById(R.id.rankList_btn);

    }


    @Override
    public void onClick(View view)
    {
        Intent intent;
        switch (view.getId())
        {
            case R.id.profile_btn:
            {
                intent = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.friends_btn:
            {
                intent = new Intent(MapActivity.this, FriendsActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.rankList_btn:
            {
                intent = new Intent(MapActivity.this, RankListActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps.stopUsingGPS();

    }
}
