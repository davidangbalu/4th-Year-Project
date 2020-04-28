package com.davidangbalu.meetup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef;
    private DatabaseReference groupRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        groupRef = FirebaseDatabase.getInstance().getReference().child("Events");




        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch(item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new Home();
                            break;
                        case R.id.nav_settings:
                            selectedFragment = new Settings();
                            break;
                        case R.id.nav_contacts:
                            selectedFragment = new Contacts();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                            selectedFragment).commit();

                    return true;
                }
            };



    //Get if User is logged in or Exists
    @Override
    protected void onStart() {

        super.onStart();

        if(currentUser == null){

            sendToLogin();

        } else {

            VerifyExistence();

        }
    }

    //Verify that the current user exists
    private void VerifyExistence() {

        final String currentUserID = mAuth.getCurrentUser().getUid();

        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.child("name").exists())){
                    String name = dataSnapshot.child("name").getValue().toString();
                    Toast.makeText(MainActivity.this,"Welcome " + name, Toast.LENGTH_SHORT).show();
                }
                else {

                    //sendToEditProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Send user to Log in screen
    private void sendToLogin() {
        //Check if current user is logged in, return to Sign-in screen if not
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(MainActivity.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

    }


    //Send user to Settings/Edit Profile
    //private void sendToEditProfile(){
        //Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //startActivity(intent);
        //finish();
    //}

    //Logout
    private void logOut() {

        mAuth.signOut();
        sendToLogin();
    }


}


