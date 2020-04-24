package com.example.mylocationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class Profile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Toolbar toolbar;
    Intent intent;
    public ImageView ivProfilePhoto;
    TextView tvUserName;
    TextView tvUserMail;
    TextView tvUserId;

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //this.setTitle("Profile");

        toolbar = findViewById(R.id.tbProfile);
        toolbar.setTitle("Profile");

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ivProfilePhoto =findViewById(R.id.ivProfilePhoto);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserMail = findViewById(R.id.tvUserMail);
        tvUserId = findViewById(R.id.tvUserId);
        tvUserId.setVisibility(View.INVISIBLE);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
    }


    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if(account.getId()!=null) {

                tvUserName.setText(account.getDisplayName().toUpperCase());
                tvUserMail.setText(account.getEmail());
                tvUserId.setText(account.getId());

                Picasso.get().load(account.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(ivProfilePhoto);
            }else {

                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            startActivity(new Intent(Profile.this, Login.class));
                            finish();
                        } else
                            Toast.makeText(Profile.this, "LOGOUT FAILED", Toast.LENGTH_SHORT).show();
                    }
                });

                intent = new Intent(this, Login.class);
                startActivity(intent);
            }

        } else {
            startActivity(new Intent(Profile.this,Login.class));
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result=opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.menu_logout){

            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()){
                        startActivity(new Intent(Profile.this,Login.class));
                        finish();
                    }else Toast.makeText(Profile.this, "LOGOUT FAILED", Toast.LENGTH_SHORT).show();
                }
            });

            intent =new Intent(this,Login.class);
            startActivity(intent);
        }

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this,Location.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.menu_profile);
        item.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.menu_settings);
        item2.setVisible(false);
        return true;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,Location.class);
        startActivity(intent);
        finish();
    }

}



