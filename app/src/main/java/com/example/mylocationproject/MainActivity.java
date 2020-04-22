package com.example.mylocationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener {

    private static int SPLASH_TIME_OUT=3000;

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    String accountId;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();
        handleResult();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(intent!=null) {
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            this.intent  = new Intent(MainActivity.this,Location.class);
        } else {
            this.intent  = new Intent(MainActivity.this,Login.class);
        }
    }


    public void handleResult() {


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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
