package com.sumod.pokenav.activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sumod.pokenav.Api;
import com.sumod.pokenav.R;
import com.sumod.pokenav.activities.base.InjectableActivity;
import com.sumod.pokenav.model.User;
import com.sumod.pokenav.utils.PrefManager;

import javax.inject.Inject;

import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends InjectableActivity implements
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static int RC_SIGN_IN = 1331;
    private static String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout relativeLayout;
    private SignInButton mGoogleSignInButton;

    private static final int REQUEST_CODE_FINE_LOCATION = 100;

    @Inject Api.ApiService apiService;
    @Inject User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (((Boolean) PrefManager.getPrefs(this, PrefManager.PREF_REGISTRATION_DONE, Boolean.class))) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        relativeLayout = (RelativeLayout) findViewById(R.id.login_RelativeLayout);
        mGoogleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        mGoogleSignInButton.setOnClickListener(this);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                if (isInternetAvailable()) signIn();
                break;
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();

            currentUser.populateWithGoogle(acct);
            apiService.login(currentUser).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Response<User> response) {
                    // TODO: Login successful! Move to the next user here.

                    PrefManager.putPrefs(getApplicationContext(), PrefManager.PREF_USER_NAME, acct.getDisplayName());
                    PrefManager.putPrefs(getApplicationContext(), PrefManager.PREF_EMAIL, acct.getEmail());
                    PrefManager.putPrefs(getApplicationContext(), PrefManager.PREF_REGISTRATION_DONE, true);
                    PrefManager.putPrefs(getApplicationContext(), PrefManager.PREF_USER_PROFILE_PICTURE, acct.getPhotoUrl());
                    askLocationPermissions();
                }


                @Override
                public void onFailure(Throwable t) {
                    // TODO: Our server denied access. Maybe this guy is banned?
                }
            });
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Oops! Somethings not right. Please try again later",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Snackbar snackbar = Snackbar
                .make(relativeLayout, "Something's wrong. Please try after sometime", Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode){

            case REQUEST_CODE_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Please grant location permissions for the app to perform.", Snackbar.LENGTH_LONG)
                            .setAction("Grant", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            askLocationPermissions();
                        }
                    });
                    snackbar.show();
                }
                break;
        }
    }


    private boolean isInternetAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isInternetAvailable();
                        }
                    });

            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
            return false;
        }

        return true;
    }


    private void askLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            finish();
        }
    }
}

