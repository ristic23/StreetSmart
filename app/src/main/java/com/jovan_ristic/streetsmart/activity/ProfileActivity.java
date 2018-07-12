package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jovan_ristic.streetsmart.Model.Friend;
import com.jovan_ristic.streetsmart.Model.Question;
import com.jovan_ristic.streetsmart.Model.User;
import com.jovan_ristic.streetsmart.R;
import com.jovan_ristic.streetsmart.adapter.QuestionAdapter;
import com.jovan_ristic.streetsmart.helpers.AppManager;
import com.jovan_ristic.streetsmart.helpers.GlideApp;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    ImageView btnMap, btnFriends, btnRankList, btnEdit;
    ImageView btnLogOut, profileImage;

    TextView rankNumber, friendsNumber, questionsNumber;
    TextView firstName, lastName, phone;

    private RecyclerView questionsRecyclerView;
    private QuestionAdapter questionsAdapter;
    private RecyclerView.LayoutManager questionsLayoutManager;
    private DatabaseReference mDatabase, Ref;
    User user;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private TextView headerQuestion, bodyQuestion, answerA, answerB, answerC, answerD, btnCloseQuestionPopUp;
    private CheckBox checkA, checkB, checkC, checkD;
    private ConstraintLayout questionPopUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_profile);
        }
        catch(Exception | OutOfMemoryError e)
        {
            Toast.makeText(this, getResources().getString(R.string.errorMsg), Toast.LENGTH_SHORT).show();
            finish();
        }
        user = new User();
        initLayout();
        initListeners();
        try
        {
            SharedPreferences sharedPrefPicture = ProfileActivity.this.getSharedPreferences("PHOTO_DATA", MODE_PRIVATE);
            String path = sharedPrefPicture.getString("PhotoPath", "");
            if(profileImage != null)
            {
                if(sharedPrefPicture.getBoolean("CameraSet", false))
                    profileImage.setRotation(90f);
                GlideApp.with(ProfileActivity.this).load(path).into(profileImage);
            }
        }
        catch(Exception|OutOfMemoryError outOfMemoryError)
        {
            Toast.makeText(this, "Error loading profileimage", Toast.LENGTH_SHORT).show();
        }
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        Ref = mDatabase.child(auth.getUid());

        AppManager.getInstance().DefaultValues();
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user.setFirstName(dataSnapshot.getValue(User.class).getFirstName());
                user.setLastName(dataSnapshot.getValue(User.class).getLastName());
                user.setPhone(dataSnapshot.getValue(User.class).getPhone());
                user.setRank(dataSnapshot.getValue(User.class).getRank());
                user.setFriendsList(dataSnapshot.getValue(User.class).getFriendsList());
                user.setActiveQuestions(dataSnapshot.getValue(User.class).getActiveQuestions());

                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                phone.setText(user.getPhone());
                rankNumber.setText(String.valueOf(user.getRank()));
                friendsNumber.setText(String.valueOf(user.getFriendsList().size()));
                questionsNumber.setText(String.valueOf(user.getActiveQuestions().size()));

                questionsAdapter.setQuestions(user.getActiveQuestions());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                }
            }
        };



    }

    private void initListeners() {
        btnMap.setOnClickListener(this);
        btnFriends.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnRankList.setOnClickListener(this);
        friendsNumber.setOnClickListener(this);
        rankNumber.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnCloseQuestionPopUp.setOnClickListener(this);
    }

    private void initLayout() {
        btnMap = findViewById(R.id.map_btn);
        btnFriends = findViewById(R.id.friends_btn);
        btnRankList = findViewById(R.id.rankList_btn);
        btnEdit = findViewById(R.id.editBtn);

        profileImage = findViewById(R.id.profileImage);

        friendsNumber = findViewById(R.id.friendsNumber);
        rankNumber = findViewById(R.id.rankNumber);

        btnLogOut = findViewById(R.id.log_out_Btn);

        firstName = findViewById(R.id.firstNameTextView);
        lastName = findViewById(R.id.lastNameTextView);
        phone = findViewById(R.id.numberTextView);
        questionsNumber = findViewById(R.id.questionsNumber);

        questionsRecyclerView = findViewById(R.id.activeQuestionRecyclerView);
        questionsRecyclerView.setHasFixedSize(true);
        questionsLayoutManager = new LinearLayoutManager(ProfileActivity.this);
        questionsRecyclerView.setLayoutManager(questionsLayoutManager);
        questionsAdapter = new QuestionAdapter(ProfileActivity.this, user.getActiveQuestions(),ProfileActivity.this);
        questionsRecyclerView.setAdapter(questionsAdapter);

        headerQuestion = (TextView) findViewById(R.id.titleQuestion);
        bodyQuestion = (TextView) findViewById(R.id.questionBody);
        answerA = (TextView) findViewById(R.id.answerA);
        answerB = (TextView) findViewById(R.id.answerB);
        answerC = (TextView) findViewById(R.id.answerC);
        answerD = (TextView) findViewById(R.id.answerD);

        checkA = (CheckBox) findViewById(R.id.checkA);
        checkB = (CheckBox) findViewById(R.id.checkB);
        checkC = (CheckBox) findViewById(R.id.checkC);
        checkD = (CheckBox) findViewById(R.id.checkD);
        questionPopUp = findViewById(R.id.questionPopUp);

        btnCloseQuestionPopUp = (TextView) findViewById(R.id.backBtn);
    }


    @Override
    public void onClick(View view)
    {
        Intent intent;
        switch (view.getId())
        {
            case R.id.log_out_Btn:
            {
                auth.signOut();
                break;
            }
            case R.id.map_btn:
            {
                intent = new Intent(ProfileActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.friends_btn:
            {
                intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.rankList_btn:
            {
                intent = new Intent(ProfileActivity.this, RankListActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.editBtn:
            {
                intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.rankNumber:
            {
                intent = new Intent(ProfileActivity.this, RankListActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.friendsNumber:
            {
                intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.backBtn:
            {
                questionPopUp.setVisibility(View.GONE);
                break;
            }
        }
    }

    public void showPickedQuestion(int position)
    {
        Question tempQ = user.getActiveQuestions().get(position);

        headerQuestion.setText(tempQ.getHeaderQ());
        bodyQuestion.setText(tempQ.getBodyQ());
        answerA.setText(tempQ.getaA());
        answerB.setText(tempQ.getaB());
        answerC.setText(tempQ.getaC());
        answerD.setText(tempQ.getaD());

        checkA.setChecked(tempQ.isBooleanA());
        checkB.setChecked(tempQ.isBooleanB());
        checkC.setChecked(tempQ.isBooleanC());
        checkD.setChecked(tempQ.isBooleanD());

        questionPopUp.setVisibility(View.VISIBLE);
    }

    public void deleteQuestion(int position)
    {
        boolean checkIfLast = false;
        if(user.getActiveQuestions().size() == 1)
            checkIfLast = true;
        Ref.child("activeQuestions").setValue(user.getActiveQuestions().remove(position));
        if(checkIfLast)
        {
            user.setActiveQuestions(new ArrayList<Question>());
            Ref.child("activeQuestions").setValue(user.getActiveQuestions());
        }
        questionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
