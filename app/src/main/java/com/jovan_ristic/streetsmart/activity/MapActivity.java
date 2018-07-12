package com.jovan_ristic.streetsmart.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import com.jovan_ristic.streetsmart.helpers.AppManager;
import com.jovan_ristic.streetsmart.helpers.GPSTracker;

public class MapActivity extends AppCompatActivity  implements View.OnClickListener, OnMapReadyCallback
{
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference parRef;
    private DatabaseReference Ref;

    private ImageView btnProfile, btnFriends, btnRankList, btnAddQuestion, searchBtn;

    private User user;
    private ConstraintLayout addQuestionPopUp, searchPopUp;
    private TextView btnCloseNewQuestion, btnAdd, btnCloseSearch, radiusSetTextView;
    private EditText headerQuestion, bodyQuestion, answerA, answerB, answerC, answerD;
    private CheckBox checkA, checkB, checkC, checkD;

    private SeekBar radiusSeekBar;
    private Switch usersSwitch;
    private boolean isMarkerClicked = false;
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
            finish();
        }
        gps = new GPSTracker(this);
        auth = FirebaseAuth.getInstance();

        if(!gps.isGPSEnabled())
            gps.showGPSSettingsAlert();
        if(!gps.isNetworkEnabled())
            gps.showWifiSettingsAlert();
        initLayout();
        initListeners();
        database = FirebaseDatabase.getInstance();
        parRef = database.getReference("users");
        Ref = parRef.child(auth.getUid());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
        }

        AppManager.getInstance().setMarkerUsersLayout(this);
        AppManager.getInstance().setMarkerQuestionsLayout(this);

        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = new User();
                user.setFirstName(dataSnapshot.getValue(User.class).getFirstName());
                user.setLastName(dataSnapshot.getValue(User.class).getLastName());
                user.setPhone(dataSnapshot.getValue(User.class).getPhone());
                user.setRank(dataSnapshot.getValue(User.class).getRank());
                user.setFriendsList(dataSnapshot.getValue(User.class).getFriendsList());
                user.setActiveQuestions(dataSnapshot.getValue(User.class).getActiveQuestions());
                user.setTotalPoints(dataSnapshot.getValue(User.class).getTotalPoints());

                if(user.getActiveQuestions().size() > 0) {
                    AppManager.getInstance().setQuestionsData(user.getActiveQuestions());
//                    AppManager.getInstance().setQuestionMarkers();
                    AppManager.getInstance().getUsersLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        AppManager.getInstance().setContext(MapActivity.this);
    }

    private void initListeners() {
        btnProfile.setOnClickListener(this);
        btnFriends.setOnClickListener(this);
        btnRankList.setOnClickListener(this);
        btnAddQuestion.setOnClickListener(this);

        btnAdd.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        btnCloseSearch.setOnClickListener(this);
    }

    private void initLayout() {
        btnProfile = findViewById(R.id.profile_btn);
        btnFriends = findViewById(R.id.friends_btn);
        btnRankList = findViewById(R.id.rankList_btn);
        btnAddQuestion = findViewById(R.id.addQuestion);
        addQuestionPopUp = findViewById(R.id.questionPopUp);
        searchPopUp = findViewById(R.id.searchPopUp);
        searchBtn = findViewById(R.id.searchBtn);
        btnCloseSearch = findViewById(R.id.closeSearch);

        radiusSeekBar = findViewById(R.id.seekBarRadius);
        usersSwitch = findViewById(R.id.allUserSwitch);
        radiusSetTextView = findViewById(R.id.textViewDistanceSet);

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b)
            {
                radiusSetTextView.setText(String.valueOf(progressValue) + " m");
                AppManager.getInstance().setRadiusSet(progressValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        usersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    AppManager.getInstance().setRadiusSet(-1);
                }
                else
                {
                    AppManager.getInstance().setRadiusSet(radiusSeekBar.getProgress());
                }
                AppManager.getInstance().getUsersLocation();
            }
        });

        btnCloseNewQuestion = (TextView) findViewById(R.id.backBtn);
        btnAdd = (TextView) findViewById(R.id.finishBtn);

        headerQuestion = (EditText) findViewById(R.id.titleQuestion);
        bodyQuestion = (EditText) findViewById(R.id.questionBody);
        answerA = (EditText) findViewById(R.id.answerA);
        answerB = (EditText) findViewById(R.id.answerB);
        answerC = (EditText) findViewById(R.id.answerC);
        answerD = (EditText) findViewById(R.id.answerD);

        checkA = (CheckBox) findViewById(R.id.checkA);
        checkB = (CheckBox) findViewById(R.id.checkB);
        checkC = (CheckBox) findViewById(R.id.checkC);
        checkD = (CheckBox) findViewById(R.id.checkD);

        btnCloseNewQuestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addQuestionPopUp.setVisibility(View.GONE);
            }
        });

        //region CheckBoxs

                checkA.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        checkA.setChecked(true);
                        checkB.setChecked(false);
                        checkC.setChecked(false);
                        checkD.setChecked(false);
                    }
                });
                checkB.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        checkA.setChecked(false);
                        checkB.setChecked(true);
                        checkC.setChecked(false);
                        checkD.setChecked(false);
                    }
                });
                checkC.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        checkA.setChecked(false);
                        checkB.setChecked(false);
                        checkC.setChecked(true);
                        checkD.setChecked(false);
                    }
                });
                checkD.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        checkA.setChecked(false);
                        checkB.setChecked(false);
                        checkC.setChecked(false);
                        checkD.setChecked(true);
                    }
                });
                //endregion
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
            case R.id.searchBtn:
            {
                searchPopUp.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.closeSearch:
            {
                searchPopUp.setVisibility(View.GONE);
                AppManager.getInstance().getUsersLocation();
                break;
            }
            case R.id.finishBtn:
            {
                if(!isMarkerClicked) {
                    final String headerQ = headerQuestion.getText().toString().trim();
                    final String bodyQ = bodyQuestion.getText().toString().trim();
                    final String aA = answerA.getText().toString().trim();
                    final String aB = answerB.getText().toString().trim();
                    final String aC = answerC.getText().toString().trim();
                    final String aD = answerD.getText().toString().trim();


                    if (TextUtils.isEmpty(headerQ)) {
                        Toast.makeText(getApplicationContext(), "Enter question title!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(bodyQ)) {
                        Toast.makeText(getApplicationContext(), "Enter question!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(aA)) {
                        Toast.makeText(getApplicationContext(), "Enter answer A!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(aB)) {
                        Toast.makeText(getApplicationContext(), "Enter answer B!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(aC)) {
                        Toast.makeText(getApplicationContext(), "Enter answer C!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(aD)) {
                        Toast.makeText(getApplicationContext(), "Enter answer D!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!checkA.isChecked() && !checkB.isChecked() && !checkC.isChecked() && !checkD.isChecked() ) {
                        Toast.makeText(getApplicationContext(), "Check correct answer!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Question newQuestion = new Question();
                    newQuestion.setHeaderQ(headerQ);
                    newQuestion.setBodyQ(bodyQ);
                    newQuestion.setaA(aA);
                    newQuestion.setaB(aB);
                    newQuestion.setaC(aC);
                    newQuestion.setaD(aD);
                    newQuestion.setBooleanA(checkA.isChecked());
                    newQuestion.setBooleanB(checkB.isChecked());
                    newQuestion.setBooleanC(checkC.isChecked());
                    newQuestion.setBooleanD(checkD.isChecked());
                    newQuestion.setLongitude(AppManager.getInstance().getLongitude());
                    newQuestion.setLatitude(AppManager.getInstance().getLatitude());

                    user.addNewQuestion(newQuestion);
                    Ref.child("activeQuestions").setValue(user.getActiveQuestions());
//                    AppManager.getInstance().setQuestionMarkers();
                    addQuestionPopUp.setVisibility(View.GONE);
                }
                else
                {
                    if(checkA.isChecked())
                    {
                        if(questionPicked.isBooleanA())
                            correctAnswer();
                        else
                            Toast.makeText(getApplicationContext(), "Incorrect answer!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(checkB.isChecked())
                        {
                            if(questionPicked.isBooleanB())
                                correctAnswer();
                            else
                                Toast.makeText(getApplicationContext(), "Incorrect answer!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(checkC.isChecked())
                            {
                                if(questionPicked.isBooleanC())
                                    correctAnswer();
                                else
                                    Toast.makeText(getApplicationContext(), "Incorrect answer!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if(checkD.isChecked())
                                {
                                    if(questionPicked.isBooleanD())
                                        correctAnswer();
                                    else
                                        Toast.makeText(getApplicationContext(), "Incorrect answer!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "Choose one answer!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
                break;
            }
            case R.id.addQuestion:
            {
                addQuestionPopUp.setVisibility(View.VISIBLE);
                headerQuestion.setFocusable(true);
                bodyQuestion.setFocusable(true);
                answerA.setFocusable(true);
                answerB.setFocusable(true);
                answerC.setFocusable(true);
                answerD.setFocusable(true);
                headerQuestion.setText("");
                bodyQuestion.setText("");
                answerA.setText("");
                answerB.setText("");
                answerC.setText("");
                answerD.setText("");
                btnAdd.setText(getResources().getString(R.string.addText));
                break;
            }
        }
    }

    private void correctAnswer()
    {
        Toast.makeText(getApplicationContext(), "You won 10 points!", Toast.LENGTH_LONG).show();
        int points = user.getTotalPoints();
        points += 10;
        Ref.child("totalPoints").setValue(points);
        addQuestionPopUp.setVisibility(View.GONE);
        isMarkerClicked = false;
    }

    Question questionPicked;
    public void markerIsClicked(Question q)
    {
        isMarkerClicked = true;
        questionPicked = q;

        headerQuestion.setText(questionPicked.getHeaderQ());
        bodyQuestion.setText(questionPicked.getBodyQ());
        answerA.setText(questionPicked.getaA());
        answerB.setText(questionPicked.getaB());
        answerC.setText(questionPicked.getaC());
        answerD.setText(questionPicked.getaD());
        checkA.setChecked(false);
        checkB.setChecked(false);
        checkC.setChecked(false);
        checkD.setChecked(false);
        headerQuestion.setFocusable(false);
        bodyQuestion.setFocusable(false);
        answerA.setFocusable(false);
        answerB.setFocusable(false);
        answerC.setFocusable(false);
        answerD.setFocusable(false);
        btnAdd.setText("Confirm");
        addQuestionPopUp.setVisibility(View.VISIBLE);
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
