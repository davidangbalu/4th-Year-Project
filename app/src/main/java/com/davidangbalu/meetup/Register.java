package com.davidangbalu.meetup;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {


    //Variables
    private EditText regEmail;
    private EditText regPassword;
    private EditText regConfirmPassword;
    private Button regBtn;
    private Button regLoginBtn;
    private ProgressBar regProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialized
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        regEmail = (EditText) findViewById(R.id.reg_email);
        regPassword = (EditText) findViewById(R.id.reg_password);
        regConfirmPassword = (EditText) findViewById(R.id.reg_confirm);
        regBtn = (Button) findViewById(R.id.reg_create);
        regLoginBtn = (Button) findViewById(R.id.reg_login);
        regProgress = (ProgressBar) findViewById(R.id.reg_progress);


        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        //Upon clicking already have an account button, send user to LoginActivity
        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regIntent = new Intent(Register.this, Login.class);
                startActivity(regIntent);
            }
        });

        //Upon clicking Register button, store details to authentication
        //Create unique identifier for newly registered user and store in Firebase Database
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = regEmail.getText().toString();
                String password = regPassword.getText().toString();
                String confirmPass = regConfirmPassword.getText().toString();

                regProgress.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPass)){
                    if (password.equals(confirmPass)) {

                        regProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    //Intent editProfileIntent = new Intent(RegisterActivity.this, EditProfileActivity.class);
                                    //startActivity(editProfileIntent);

                                    String currentUserID = mAuth.getCurrentUser().getUid();
                                    rootRef.child("Users").child(currentUserID).setValue("null");

                                    sendToMain();
                                    finish();

                                } else{

                                    String errormessage = task.getException().getMessage();
                                    Toast.makeText(Register.this, "Error: " + errormessage, Toast.LENGTH_LONG);
                                }

                            }
                        });

                    } else {
                        Toast.makeText(Register.this, "Confirm Password and Password Field Doesn't Match.", Toast.LENGTH_LONG);
                    }
                }
            }
        });
    }

    //If user is currently logged in, send to MainActivity
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    //Send user to MainActivity
    private void sendToMain() {
        Intent mainIntent = new Intent(Register.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
