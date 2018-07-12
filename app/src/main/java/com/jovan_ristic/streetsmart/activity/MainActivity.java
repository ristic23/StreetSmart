package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jovan_ristic.streetsmart.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    TextView btnSignIn, btnRegistration;

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_main);
        }
        catch(Exception | OutOfMemoryError e)
        {
            Toast.makeText(this, getResources().getString(R.string.errorMsg), Toast.LENGTH_SHORT).show();
            finish();
        }
        auth = FirebaseAuth.getInstance();


        initLayout();
        initListeners();

    }

    private void initListeners() {
        btnSignIn.setOnClickListener(this);
        btnRegistration.setOnClickListener(this);

    }

    private void initLayout() {
        btnSignIn = findViewById(R.id.signInBtn);
        btnRegistration = findViewById(R.id.registrationText);


        inputEmail = findViewById(R.id.editTextUserName);
        inputPassword = findViewById(R.id.editTextPassword);

    }


    @Override
    public void onClick(View view)
    {
        Intent intent;
        switch (view.getId())
        {
            case R.id.signInBtn:
            {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful())
                                {
                                    if (password.length() < 6) {
                                        Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Sign in failed. " + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                break;
            }
            case R.id.registrationText:
            {
                intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
