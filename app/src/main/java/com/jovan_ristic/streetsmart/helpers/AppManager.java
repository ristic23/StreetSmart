package com.jovan_ristic.streetsmart.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jovan_ristic.streetsmart.Model.User;
import com.jovan_ristic.streetsmart.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ristic on 7/27/2017.
 */

public class AppManager
{

    private Marker myLocationMarker;
    private ArrayList<Marker> friendMarkers;
    private double latitude;
    private double longitude;
    private boolean googleMapActive;
    private GoogleMap googleMapAppManager;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseReference;
    private String myUserName;

    private static AppManager AppInstance = null;

    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    protected AppManager() {
        // Exists only to defeat instantiation.
    }
    public static AppManager getInstance() {
        if(AppInstance == null) {
            AppInstance = new AppManager();
        }
        return AppInstance;
    }

    public void DefaultValues()
    {
        latitude = longitude = 0;
        googleMapActive = false;
        myLocationMarker = null;
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child("1Z2UrE8ISaficmXqHNe2jhx3ZKM2").removeValue();
        mDatabase.child("1Ffdl3lJdQfii0Zyu2rvkMiSYEV2").removeValue();
        databaseReference = mDatabase.child(auth.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myUserName = dataSnapshot.getValue(User.class).getUserName();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void setMarkerLayout(Context context)
    {
        mCustomMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);
    }

    public void getUsersLocation()
    {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collectLocationUsers((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void collectLocationUsers(Map<String, Object> users)
    {
        try
        {
            if(friendMarkers != null)
            {
                for (Marker ma: friendMarkers)
                {
                    ma.remove();
                }
            }
            else
            {
                friendMarkers = new ArrayList<>();
            }
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                Map singleUser = (Map) entry.getValue();
                LatLng myCoordinate = new LatLng((double) singleUser.get("latitude"), (double) singleUser.get("longitude"));
                String name = String.valueOf(singleUser.get("userName"));
//                if (!myUserName.equalsIgnoreCase(name) && googleMapAppManager != null)
//                    friendMarkers.add(googleMapAppManager.addMarker(new MarkerOptions().position(myCoordinate).title(name).rotation(180f)));

                if (!myUserName.equalsIgnoreCase(name) && googleMapAppManager != null)
                    friendMarkers.add(googleMapAppManager.addMarker(new MarkerOptions().position(myCoordinate).title(name)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView,R.drawable.avatar)))));

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("LOCATION_ALL_USERS = ", "ERROR");
        }
    }

    private Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId) {

        mMarkerImageView.setImageResource(resId);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

    public boolean isGoogleMapActive() {
        return googleMapActive;
    }

    public void setGoogleMapActive(boolean googleMapActive) {
        this.googleMapActive = googleMapActive;
    }


    public GoogleMap getGoogleMapAppManager() {
        return googleMapAppManager;
    }

    public void setGoogleMapAppManager(GoogleMap googleMapAppManager) {
        this.googleMapAppManager = googleMapAppManager;
    }

    public void setMyLocationMarker()
    {
        if(myLocationMarker != null)
            myLocationMarker.remove();

        LatLng myCoor = new LatLng(latitude,longitude);
        CameraPosition MyLocationCam = CameraPosition.builder().target(myCoor).zoom(150f).build();
        googleMapAppManager.animateCamera(CameraUpdateFactory.newCameraPosition(MyLocationCam));

        myLocationMarker = googleMapAppManager.addMarker(new MarkerOptions().position(myCoor).title("My Loc"));

        databaseReference.child("latitude").setValue(latitude);
        databaseReference.child("longitude").setValue(longitude);

    }

    private float getZoomLevel(Circle myLocationCircle) {
        float zoomLevel = 1;

        if (myLocationCircle != null){
            double radius = myLocationCircle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }




}
