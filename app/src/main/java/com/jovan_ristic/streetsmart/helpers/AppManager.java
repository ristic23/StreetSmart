package com.jovan_ristic.streetsmart.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.jovan_ristic.streetsmart.Model.Question;
import com.jovan_ristic.streetsmart.Model.User;
import com.jovan_ristic.streetsmart.R;
import com.jovan_ristic.streetsmart.activity.MapActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ristic on 7/27/2017.
 */

public class AppManager implements GoogleMap.OnMarkerClickListener
{

    private Marker myLocationMarker;
    private List<Marker> friendMarkers;
    private List<Marker> questionsMarkers;
    private List<Marker> questionsMarkersUsers;
    private List<Question> questionsData;
    private List<Question> questionsDataUsers;
    private double latitude;
    private double longitude;
    private boolean googleMapActive;
    private GoogleMap googleMapAppManager;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseReference;
    private String myUserName;

    private int radiusSet;

    private static AppManager AppInstance = null;

    private View mFriendMarkerView, mQuestionMarkerView;
    private ImageView mFriendImageView, mQuestionImageView;
    private Circle myLocationCircle;
    protected AppManager() {
        // Exists only to defeat instantiation.
    }
    public static AppManager getInstance() {
        if(AppInstance == null) {
            AppInstance = new AppManager();
        }
        return AppInstance;
    }

    int  countQ;
    public void DefaultValues()
    {
        countQ = 100000;
        questionsMarkers = new ArrayList<>();
        questionsData = new ArrayList<>();
        questionsDataUsers = new ArrayList<>();
        radiusSet = -1;
        latitude = longitude = 0;
        googleMapActive = false;
        myLocationMarker = null;
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
//        mDatabase.child("SCfL29gz7aWBtr0vCjv6vXXyCkL2").removeValue();
//        mDatabase.child("TRNQnDzPlzZVdEow1b1IGkwEIGD3").removeValue();
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
    private MapActivity mapActivity;
    public void setContext(MapActivity c)
    {
        mapActivity = c;
    }

    public void setMarkerUsersLayout(Context context)
    {
        mFriendMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_friends_marker, null);
        mFriendImageView = (ImageView) mFriendMarkerView.findViewById(R.id.profile_image);
    }

    public void setMarkerQuestionsLayout(Context context)
    {
        mQuestionMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_questions_marker, null);
        mQuestionImageView = (ImageView) mQuestionMarkerView.findViewById(R.id.question_image);
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
        try {
            if (friendMarkers != null) {
                for (Marker ma : friendMarkers) {
                    ma.remove();
                }
            } else {
                friendMarkers = new ArrayList<>();
            }
            if (questionsMarkersUsers != null) {
                for (Marker ma : questionsMarkersUsers) {
                    ma.remove();
                }
            } else {
                questionsMarkersUsers = new ArrayList<>();
            }
            countQ = 100000;
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                Map singleUser = (Map) entry.getValue();
                LatLng myCoordinate = new LatLng((double) singleUser.get("latitude"), (double) singleUser.get("longitude"));
                String name = String.valueOf(singleUser.get("userName"));
                double distanceCalculated = distance(latitude, longitude, myCoordinate.latitude, myCoordinate.longitude);
                if (radiusSet == -1 || (radiusSet) >= distanceCalculated) {
                    if (!myUserName.equalsIgnoreCase(name) && googleMapAppManager != null) {
                        MarkerOptions tempMarker = new MarkerOptions().position(myCoordinate).title(name)
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mFriendMarkerView, R.drawable.avatar)));
                        friendMarkers.add(googleMapAppManager.addMarker(tempMarker));
                    }
                }


                    ArrayList<HashMap> tempQs = (ArrayList<HashMap>) singleUser.get("activeQuestions");
                if(tempQs != null) {
                    if (!myUserName.equalsIgnoreCase(name) && googleMapAppManager != null) {
                        for (int i = 0; i < tempQs.size(); i++) {
                            Question newQuestion = new Question();
                            newQuestion.setLongitude((double) (tempQs.get(i).get("longitude")));
                            newQuestion.setLatitude((double) (tempQs.get(i).get("latitude")));
                            distanceCalculated = distance(latitude, longitude, newQuestion.getLatitude(), newQuestion.getLongitude());
                            if (radiusSet == -1 || (radiusSet) >= distanceCalculated) {
                                newQuestion.setHeaderQ(String.valueOf(tempQs.get(i).get("headerQ")));
                                newQuestion.setBodyQ(String.valueOf(tempQs.get(i).get("bodyQ")));
                                newQuestion.setaA(String.valueOf(tempQs.get(i).get("aA")));
                                newQuestion.setaB(String.valueOf(tempQs.get(i).get("aB")));
                                newQuestion.setaC(String.valueOf(tempQs.get(i).get("aC")));
                                newQuestion.setaD(String.valueOf(tempQs.get(i).get("aD")));
                                newQuestion.setBooleanA((boolean) (tempQs.get(i).get("booleanA")));
                                newQuestion.setBooleanB((boolean) (tempQs.get(i).get("booleanB")));
                                newQuestion.setBooleanC((boolean) (tempQs.get(i).get("booleanC")));
                                newQuestion.setBooleanD((boolean) (tempQs.get(i).get("booleanD")));

                                questionsData.add(newQuestion);
                                LatLng myCoordinate2 = new LatLng(newQuestion.getLatitude(), newQuestion.getLongitude());
                                questionsMarkersUsers.add(googleMapAppManager.addMarker(new MarkerOptions().position(myCoordinate2)
                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapForQuestion(mQuestionImageView, R.drawable.question_marker)))));
                                questionsMarkersUsers.get(questionsMarkersUsers.size() - 1).setTag(countQ);
                                countQ++;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("LOCATION_ALL_USERS = ", "ERROR");
        }
    }

//    public void setQuestionMarkers()
//    {
//        if(questionsMarkers != null)
//        {
//            for (Marker ma: questionsMarkers)
//            {
//                ma.remove();
//                indexQ = 0;
//                countQ = 100000;
//            }
//        }
//        else
//        {
//            questionsMarkers = new ArrayList<>();
//        }
//        for (Question ma: questionsData)
//        {
//            LatLng myCoordinate = new LatLng(ma.getLatitude(), ma.getLongitude());
//            if (googleMapAppManager != null) {
//                questionsMarkers.add(googleMapAppManager.addMarker(new MarkerOptions().position(myCoordinate).title(ma.getHeaderQ())
//                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapForQuestion(mQuestionImageView, R.drawable.question_marker)))));
//
//            }
//        }
//    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        Integer clickCount = (Integer) marker.getTag();
        if(clickCount != null)
        {
            mapActivity.markerIsClicked(questionsData.get(clickCount % 100000));
        }
        return false;
    }

    private Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId) {

        mFriendImageView.setImageResource(resId);
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
    private Bitmap getMarkerBitmapForQuestion(View view, @DrawableRes int resId) {

        mQuestionImageView.setImageResource(resId);
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
        this.googleMapAppManager.setOnMarkerClickListener(this);
    }

    public void setMyLocationMarker()
    {
        if(myLocationMarker != null)
            myLocationMarker.remove();

        myLocationCircle = googleMapAppManager.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(150).strokeColor(Color.RED));
        myLocationCircle.setVisible(false);

        LatLng myCoor = new LatLng(latitude,longitude);
        CameraPosition MyLocationCam = CameraPosition.builder().target(myCoor).zoom(getZoomLevel(myLocationCircle)).build();
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

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344; //km
        dist *= 1000; //m

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public int getRadiusSet() {
        return radiusSet;
    }

    public void setRadiusSet(int radiusSet) {
        this.radiusSet = radiusSet;
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

    public void setQuestionsData(List<Question> questionsData) {
        this.questionsData = questionsData;
    }



}
