package com.davidangbalu.meetup;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends Fragment {

    private CircleImageView profileImage;
    private EditText profileName;
    private EditText profileStatus;
    private Button profileBtn;
    private Button logoutBtn;
    private String currentUserID;
    private ProgressDialog loadingBar;

    private ProgressDialog mProgressDialog;

    private StorageReference mStorageReference;
    private StorageReference profileImagesRef;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private static final int RC_PICK=567;
    private ProgressBar profileProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstancedState) {
            super.onActivityCreated(savedInstancedState);

        Toolbar editProfileToolbar = getActivity().findViewById(R.id.edit_profile_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(editProfileToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Edit Profile");

        profileImage = getActivity().findViewById(R.id.edit_profile_image);
        profileName = getActivity().findViewById(R.id.profile_name);
        profileStatus = getActivity().findViewById(R.id.edit_profile_status);
        profileBtn = getActivity().findViewById(R.id.edit_profile_btn);
        logoutBtn = getActivity().findViewById(R.id.settings_logout);
        profileProgress = getActivity().findViewById(R.id.edit_profile_progress);
        loadingBar = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        profileImagesRef = FirebaseStorage.getInstance().getReference().child("images");

  logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });



    }


    //Intent to send user to Main Activity
    private void sendToMain() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }

    private void logOut() {

        mAuth.signOut();
        sendToLogin();
    }

    //Send user to Log in screen
    private void sendToLogin() {
        //Check if current user is logged in, return to Sign-in screen if not
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }

    }
}
