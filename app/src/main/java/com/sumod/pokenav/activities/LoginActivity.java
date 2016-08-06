package com.sumod.pokenav.activities;


import android.Manifest;
import android.app.ProgressDialog;
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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sumod.pokenav.R;
import com.sumod.pokenav.activities.base.InjectableActivity;
import com.sumod.pokenav.utils.PrefManager;


public class LoginActivity extends InjectableActivity implements
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_FINE_LOCATION = 100;
    private static int RC_SIGN_IN = 1331;
    private static String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private RelativeLayout relativeLayout;
    private SignInButton mGoogleSignInButton;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if the user already has been logged in
        if (doesSessionTokenExist()) signInWithSessionToken();

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


//        mGoogleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mGoogleSignInButton.setScopes(gso.getScopeArray());
        mGoogleSignInButton.setOnClickListener(this);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
    }


    private boolean doesSessionTokenExist() {
        String session = PrefManager.getSessionToken(getApplicationContext());
        return session != null && !session.isEmpty();
    }


    private void signInWithSessionToken() {
        String session = PrefManager.getSessionToken(getApplicationContext());

        if (session != null && !session.isEmpty()) {
            // If the user was logged in before, then we login him in again and move him into the
            // next activity.
            final ProgressDialog progress = ProgressDialog.show(this, "Logging in",
                    "This will only take a moment", true);

            ParseUser.becomeInBackground(session, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    progress.dismiss();
                    moveOn();
                }
            });
        }
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
        if (doesSessionTokenExist()) signInWithSessionToken();
        else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
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
        Log.d(TAG, "blah" + result.getStatus());
//        Log.d(TAG, "blah");

        if (result.isSuccess()) {
            Log.d(TAG, "google oAuth authenticated!");
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();

            final ParseUser user = new ParseUser();
            user.setUsername(acct.getEmail());
            user.setPassword(acct.getEmail());
            user.setEmail(acct.getEmail());

            user.put("name", acct.getDisplayName());
            user.put("avatar", acct.getPhotoUrl().toString());

            progress = ProgressDialog.show(this, "Logging in",
                    "This will only take a moment", true);

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {


                    if (e == null) onLoginOrSignup(user);
                    else {
                        if (e.getCode() == 202) { // user is already signed up
                            ParseUser.logInInBackground(acct.getEmail(), acct.getEmail(), new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (e == null) onLoginOrSignup(user);
                                    else {
                                        progress.dismiss();
                                        Log.e(TAG, e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            progress.dismiss();
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {

            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Oops! Somethings not right. Please try again later",
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This function is called whenever the user successfully signs up or logins into the Parse
     * server
     *
     * @param user The logged in user from Parse.
     */
    private void onLoginOrSignup(ParseUser user) {
        progress.dismiss();

        Log.d(TAG, "prase signed up!");
        Log.d(TAG, "prase session token!" + user.getSessionToken());

        // Hooray! Let them use the app now.
        PrefManager.putPrefs(getApplicationContext(), PrefManager.PREF_SESSION_TOKEN, user.getSessionToken());
        askLocationPermissionsAndMoveOn();
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

        switch (requestCode) {

            case REQUEST_CODE_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    moveOn();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Please grant location permissions for the app to perform.", Snackbar.LENGTH_LONG)
                            .setAction("Grant", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    askLocationPermissionsAndMoveOn();
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


    private void askLocationPermissionsAndMoveOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);

            // After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else moveOn();
    }


    private void moveOn() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

