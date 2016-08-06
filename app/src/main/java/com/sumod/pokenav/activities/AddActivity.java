package com.sumod.pokenav.activities;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseUser;
import com.sumod.pokenav.Constants;
import com.sumod.pokenav.R;
import com.sumod.pokenav.model.PokemonLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.activity_add)
public class AddActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    @ViewById(R.id.add_map_title) TextView titleView;
    @ViewById(R.id.map_container) FrameLayout mapFrameLayout;
    @ViewById(R.id.map_relativeLayout) RelativeLayout relativeLayout;
    @ViewById(R.id.submit_button) Button submitButton;

    private static final String TAG = "AddActivity";
    private SupportMapFragment supportMapFragment;
    private android.support.v4.app.FragmentManager supportFragmentManager;

    private int whatToadd;
    private String markerTitle;
    private GoogleMap googleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        supportMapFragment = SupportMapFragment.newInstance();
        supportFragmentManager = getSupportFragmentManager();
        supportMapFragment.getMapAsync(this);
        setupMapFragment();

        whatToadd = getIntent().getIntExtra("add_item", Constants.ADD_POKEMON);
        Log.d(TAG, String.valueOf(getIntent().getIntExtra("add_item", Constants.ADD_POKEMON)));
    }


    @AfterViews
    protected void afterViews() {
        setUpTitleView(whatToadd);

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                submitButton.setVisibility(View.GONE);
                return false;
            }
        });
    }


    private void setUpTitleView(int whatToadd) {
        String text = "Drag the marker to a ";

        switch (whatToadd) {
            case Constants.ADD_POKEMON:
                markerTitle = "Pokemon";
                break;
            case Constants.ADD_GYM:
                markerTitle = "Gym";
                break;
            case Constants.ADD_POKESTOP:
                markerTitle = "PokeStop";
                break;
        }
        titleView.setText(text + markerTitle);
    }


    private void setupMapFragment() {
        if (!supportMapFragment.isAdded()) {
            supportFragmentManager.beginTransaction().add(R.id.map_container, supportMapFragment).commit();
        } else {
            supportFragmentManager.beginTransaction().show(supportMapFragment).commit();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraChangeListener(this);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location = service.getLastKnownLocation(provider);
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18));

    }


    @Click(R.id.submit_button)
    void submitButtonClick() {
        Toast.makeText(AddActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
        /*LatLng target = googleMap.getCameraPosition().target;

        // TODO: Add pokemon's nDex in here
        addPokemon(target.latitude, target.longitude, 1);*/
    }


    void addPokemon(double latitude, double longitude, int pokemonNdex) {
        PokemonLog pokemonLog = new PokemonLog();
        pokemonLog.setLocation(latitude, longitude);
        pokemonLog.setCreator(ParseUser.getCurrentUser());
//        pokemonLog.setPokemon();
        pokemonLog.saveInBackground();
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        submitButton.setVisibility(View.VISIBLE);
        Log.d(TAG, cameraPosition.target.latitude + "/" + cameraPosition.target.longitude);
    }
}
