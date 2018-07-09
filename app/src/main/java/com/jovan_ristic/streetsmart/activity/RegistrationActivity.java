package com.jovan_ristic.streetsmart.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jovan_ristic.streetsmart.Model.Friend;
import com.jovan_ristic.streetsmart.Model.Question;
import com.jovan_ristic.streetsmart.Model.User;
import com.jovan_ristic.streetsmart.R;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity  implements View.OnClickListener
{

    TextView btnBack, btnFinish;
    private FirebaseAuth auth;

    EditText firstName, lastName, userName, password, confirmPassword, phoneNumber, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_registration);
        }
        catch(Exception | OutOfMemoryError e)
        {
            Toast.makeText(this, getResources().getString(R.string.errorMsg), Toast.LENGTH_SHORT).show();
        }

        auth = FirebaseAuth.getInstance();

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

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        userName = findViewById(R.id.userNameEditText);
        password = findViewById(R.id.enterPasswordEditText);
        confirmPassword = findViewById(R.id.confirmPasswordEditText);
        phoneNumber = findViewById(R.id.phoneNumberEditText);
        email = findViewById(R.id.emailEditText);


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
                final String firstNameS = firstName.getText().toString().trim();
                final String lastNameS = lastName.getText().toString().trim();
                final String userNameS = userName.getText().toString().trim();
                final String passwordS = password.getText().toString().trim();
                final String confirmPasswordS = confirmPassword.getText().toString().trim();
                final String phoneNumberS = phoneNumber.getText().toString().trim();
                final String emailS = email.getText().toString().trim();


                if (TextUtils.isEmpty(firstNameS)) {
                    Toast.makeText(getApplicationContext(), "Enter first name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lastNameS)) {
                    Toast.makeText(getApplicationContext(), "Enter last name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(userNameS)) {
                    Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordS)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPasswordS)) {
                    Toast.makeText(getApplicationContext(), "Enter confirm password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!passwordS.equalsIgnoreCase(confirmPasswordS))
                if (TextUtils.isEmpty(confirmPasswordS)) {
                    Toast.makeText(getApplicationContext(), "Passwords not equal!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phoneNumberS)) {
                    Toast.makeText(getApplicationContext(), "Enter phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(emailS)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                auth.createUserWithEmailAndPassword(emailS, passwordS)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try {
                                    if (!task.isSuccessful())
                                    {
                                        Toast.makeText(RegistrationActivity.this, "Authentication failed. " + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        {
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference parRef = database.getReference("users");
                                            DatabaseReference Ref = parRef.child(auth.getUid());

                                            User user = new User();
                                            user.setFirstName(firstNameS);
                                            user.setLastName(lastNameS);
                                            user.setUserName(userNameS);
                                            user.setPhone(phoneNumberS);
                                            user.setEmail(emailS);
                                            user.setImagePath("");
                                            user.setActiveQuestions(new ArrayList<Question>());
                                            user.setFriendsList(new ArrayList<Friend>());
                                            user.setRank(0);
                                            user.setTotalPoints(0);
                                            user.setLatitude(0);
                                            user.setLongitude(0);


                                            Ref.setValue(user);

                                            onBackPressed();
                                        }
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
                break;
            }
        }
}
