package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.jovan_ristic.streetsmart.Model.User;
import com.jovan_ristic.streetsmart.R;
import com.jovan_ristic.streetsmart.helpers.AppManager;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    ImageView btnMap, btnFriends, btnRankList, btnEdit;
    ImageView btnLogOut;

    TextView rankNumber, friendsNumber;
    TextView firstName, lastName, phone;


    User user;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

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
        }


        auth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference Ref = mDatabase.child(auth.getUid());
        AppManager.getInstance().DefaultValues();
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = new User();
                user.setFirstName(dataSnapshot.getValue(User.class).getFirstName());
                user.setLastName(dataSnapshot.getValue(User.class).getLastName());
                user.setPhone(dataSnapshot.getValue(User.class).getPhone());
                user.setRank(dataSnapshot.getValue(User.class).getRank());

                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                phone.setText(user.getPhone());
//                rankNumber.setText(user.getRank());

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

        initLayout();
        initListeners();

    }

    private void initListeners() {
        btnMap.setOnClickListener(this);
        btnFriends.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnRankList.setOnClickListener(this);
        friendsNumber.setOnClickListener(this);
        rankNumber.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
    }

    private void initLayout() {
        btnMap = findViewById(R.id.map_btn);
        btnFriends = findViewById(R.id.friends_btn);
        btnRankList = findViewById(R.id.rankList_btn);
        btnEdit = findViewById(R.id.editBtn);

        friendsNumber = findViewById(R.id.friendsNumber);
        rankNumber = findViewById(R.id.rankNumber);

        btnLogOut = findViewById(R.id.log_out_Btn);

        firstName = findViewById(R.id.firstNameTextView);
        lastName = findViewById(R.id.lastNameTextView);
        phone = findViewById(R.id.numberTextView);
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
        }
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
