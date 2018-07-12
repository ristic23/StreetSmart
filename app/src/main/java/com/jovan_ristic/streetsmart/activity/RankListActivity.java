package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.jovan_ristic.streetsmart.adapter.QuestionAdapter;
import com.jovan_ristic.streetsmart.adapter.RankListAdapter;
import com.jovan_ristic.streetsmart.helpers.AppManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RankListActivity extends AppCompatActivity implements View.OnClickListener
{
    ImageView btnProfile, btnMap, btnFriends;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference parRef;
    private DatabaseReference Ref;
    private User user;

    private RecyclerView rankListRecyclerView;
    private RankListAdapter rankListAdapter;
    private RecyclerView.LayoutManager rankListLayoutManager;

    private ArrayList<RankUser> rankUsers;

    private TextView firstPlace, secondPlace, thirdPlace;
    private String myUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_rank_list);
        }
        catch(Exception | OutOfMemoryError e)
        {
            Toast.makeText(this, getResources().getString(R.string.errorMsg), Toast.LENGTH_SHORT).show();
            finish();
        }
        auth = FirebaseAuth.getInstance();
        initLayout();
        initListeners();
        database = FirebaseDatabase.getInstance();
        parRef = database.getReference("users");
        Ref = parRef.child(auth.getUid());
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myUserName = dataSnapshot.getValue(User.class).getUserName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        parRef.addValueEventListener(new ValueEventListener() {
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
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                Map singleUser = (Map) entry.getValue();
                String userName = String.valueOf(singleUser.get("userName"));
                long userPoints = (long) singleUser.get("totalPoints");
                rankUsers.add(new RankUser(userName, (int)userPoints));
            }
            bubbleSort();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("LOCATION_ALL_USERS = ", "ERROR");
        }
    }

    private void bubbleSort() {
        int n = rankUsers.size();
        RankUser[] arr = new RankUser[n];
        RankUser temp = new RankUser();

        for (int i = 0; i < n; i++)
            arr[i] = new RankUser(rankUsers.get(i).userName, rankUsers.get(i).points);

        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (arr[j - 1].points < arr[j].points) {
                    //swap elements
                    temp.points = arr[j - 1].points;
                    temp.userName = arr[j - 1].userName;
                    arr[j - 1].points = arr[j].points;
                    arr[j - 1].userName = arr[j].userName;
                    arr[j].points = temp.points;
                    arr[j].userName = temp.userName;
                }
            }
        }
        rankUsers = new ArrayList<>();
        for (int i = 0; i < n; i++)
        {
            if(arr[i].userName.equalsIgnoreCase(myUserName))
            {
                Ref.child("rank").setValue(i+1);
            }

            rankUsers.add(arr[i]);
        }
        if(rankUsers.size() > 0)
            firstPlace.setText(rankUsers.get(0).userName + "  -  " + String.valueOf(rankUsers.get(0).points));
        if(rankUsers.size() > 1)
            secondPlace.setText(rankUsers.get(1).userName + "  -  " + String.valueOf(rankUsers.get(1).points));
        if(rankUsers.size() > 2)
        {
            thirdPlace.setText(rankUsers.get(2).userName + "  -  " + String.valueOf(rankUsers.get(2).points));
            rankUsers.remove(2);
        }
        if(rankUsers.size() > 1)
            rankUsers.remove(1);
        if(rankUsers.size() > 0)
            rankUsers.remove(0);



        rankListAdapter.setList(rankUsers);
    }

    private void initListeners() {
        btnProfile.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnFriends.setOnClickListener(this);
    }

    private void initLayout() {
        btnProfile = findViewById(R.id.profile_btn);
        btnMap = findViewById(R.id.map_btn);
        btnFriends = findViewById(R.id.friends_btn);
        rankUsers = new ArrayList<>();
        firstPlace = findViewById(R.id.firstTextView);
        secondPlace = findViewById(R.id.secondTextView);
        thirdPlace = findViewById(R.id.thirdTextView);

        rankListRecyclerView = findViewById(R.id.rankListRecyclerView);
        rankListRecyclerView.setHasFixedSize(true);
        rankListLayoutManager = new LinearLayoutManager(RankListActivity.this);
        rankListRecyclerView.setLayoutManager(rankListLayoutManager);
        rankListAdapter = new RankListAdapter(RankListActivity.this, new ArrayList<RankUser>(),RankListActivity.this);
        rankListRecyclerView.setAdapter(rankListAdapter);
    }


    @Override
    public void onClick(View view)
    {
        Intent intent;
        switch (view.getId())
        {
            case R.id.profile_btn:
            {
                intent = new Intent(RankListActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.map_btn:
            {
                intent = new Intent(RankListActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.friends_btn:
            {
                intent = new Intent(RankListActivity.this, FriendsActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    public class RankUser
    {
        public String userName;
        public int points;

        public RankUser()
        {
            userName = "";
            points = -1;
        }

        public RankUser(String n, int p)
        {
            userName = n;
            points = p;
        }
    }
}
