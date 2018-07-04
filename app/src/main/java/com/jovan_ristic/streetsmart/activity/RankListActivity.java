package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jovan_ristic.streetsmart.R;

public class RankListActivity extends AppCompatActivity implements View.OnClickListener
{
    ImageView btnProfile, btnMap, btnFriends;

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
        }
        initLayout();
        initListeners();

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
}
