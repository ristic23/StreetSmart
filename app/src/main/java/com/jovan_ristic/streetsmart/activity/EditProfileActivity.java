package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jovan_ristic.streetsmart.R;

public class EditProfileActivity extends AppCompatActivity  implements View.OnClickListener
{

    TextView btnBack, btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_edit_profile);
        }
        catch(Exception | OutOfMemoryError e)
        {
            Toast.makeText(this, getResources().getString(R.string.errorMsg), Toast.LENGTH_SHORT).show();
            finish();
        }
        initLayout();
        initListeners();

    }

    private void initListeners() {
        btnBack.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

    }

    private void initLayout() {
        btnBack = findViewById(R.id.backBtn);
        btnFinish = findViewById(R.id.finishBtn);


    }


    @Override
    public void onClick(View view)
    {
        Intent intent;
        switch (view.getId())
        {
            case R.id.backBtn:
            {
                onBackPressed();
                break;
            }
            case R.id.finishBtn:
            {
                //update user data
                onBackPressed();
                break;
            }
        }
    }
}
