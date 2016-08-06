package com.sumod.pokenav.activities;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sumod.pokenav.R;
import com.sumod.pokenav.fragments.ChatFragment;
import com.sumod.pokenav.fragments.FragmentDrawer;
import com.sumod.pokenav.fragments.HeatmapFragment_;
import com.sumod.pokenav.fragments.HomeFragment;
import com.sumod.pokenav.utils.PrefManager;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_FINE_LOCATION = 100;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private Fragment heatMapFragment;
    private SupportMapFragment supportMapFragment;
    private android.support.v4.app.FragmentManager supportFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportMapFragment = SupportMapFragment.newInstance();
        heatMapFragment = new HeatmapFragment_();

        PrefManager.putPrefs(this, PrefManager.PREF_MAIN_ACT_LAUNCH, true);

        Log.d(TAG, String.valueOf(PrefManager.getPrefs(this, PrefManager.PREF_MAIN_ACT_LAUNCH, Boolean.class)));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
        supportFragmentManager = getSupportFragmentManager();

        supportMapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }


    private void displayView(int position) {
        Fragment fragment = null;

        if (supportMapFragment.isAdded()) {
            supportFragmentManager.beginTransaction().hide(supportMapFragment).commit();
        }

        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                title = getString(R.string.title_map);
                getSupportActionBar().setTitle(title);
                setupMapFragment();
                break;
            case 2:
                fragment = new ChatFragment();
                title = getString(R.string.title_chat);
                break;
            case 3:
                drawerFragment.showFeedback();
                break;
            default:
                break;
        }

        if (fragment != null) {
            findViewById(R.id.map_container).setVisibility(View.GONE);
            findViewById(R.id.container_body).setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }


    private void setupMapFragment() {
        findViewById(R.id.container_body).setVisibility(View.GONE);
        findViewById(R.id.map_container).setVisibility(View.VISIBLE);

        if (!heatMapFragment.isAdded()) {
            supportFragmentManager.beginTransaction().add(R.id.map_container, heatMapFragment).commit();
        } else {
            supportFragmentManager.beginTransaction().show(heatMapFragment).commit();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location = service.getLastKnownLocation(provider);
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13));
    }
}
