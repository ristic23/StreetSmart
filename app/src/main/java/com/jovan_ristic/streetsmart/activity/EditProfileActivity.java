package com.jovan_ristic.streetsmart.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jovan_ristic.streetsmart.R;

public class EditProfileActivity extends AppCompatActivity {

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
        }
    }
}
