package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jovan_ristic.streetsmart.R;

public class MapActivity extends AppCompatActivity  implements View.OnClickListener
{
    ImageView btnProfile, btnFriends, btnRankList;

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
        initLayout();
        initListeners();

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
}
