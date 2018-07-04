package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jovan_ristic.streetsmart.R;

public class FriendsActivity extends AppCompatActivity implements View.OnClickListener
{
    ImageView btnProfile, btnMap, btnRankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_friends);
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
        btnRankList.setOnClickListener(this);
    }

    private void initLayout() {
        btnProfile = findViewById(R.id.profile_btn);
        btnMap = findViewById(R.id.map_btn);
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
                intent = new Intent(FriendsActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.map_btn:
            {
                intent = new Intent(FriendsActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.rankList_btn:
            {
                intent = new Intent(FriendsActivity.this, RankListActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }
}
