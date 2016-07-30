package com.sumod.pokenav.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sumod.pokenav.R;
import com.sumod.pokenav.model.PokemonLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


@EFragment(R.layout.fragment_heatmap)
public class HeatmapFragment extends Fragment implements OnMapReadyCallback {
    SupportMapFragment mapsFragment;
    @ViewById View mapContainer;

    GoogleMap googleMap;
    TileOverlayOptions heatmapOverlayOptions;
    ProgressDialog progressDialog;


    @AfterViews
    void afterViews() {
        if (mapsFragment == null) {
            mapsFragment = new SupportMapFragment();
            mapsFragment.getMapAsync(this);

            getChildFragmentManager().beginTransaction()
                    .add(R.id.mapContainer, mapsFragment)
                    .commit();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        progressDialog = ProgressDialog.show(getContext(), "Finding Pokemons",
                "This will only take a moment", true);

        ParseQuery query = ParseQuery.getQuery(PokemonLog.PARSE_CLASSNAME);
        query.findInBackground(new FindCallback<PokemonLog>() {
            @Override
            public void done(List<PokemonLog> pokemonLogs, ParseException e) {
                progressDialog.dismiss();

                if (e != null) {
                    e.printStackTrace();
                    // Show a snackbar with a reload option here
                    Toast.makeText(getContext(), "Pokemons could not be fetched :(", Toast.LENGTH_LONG).show();
                    return;
                }

                addPokemonHeatmap(pokemonLogs);
            }
        });

        // Move the map to the user's current location
        LocationManager locationManager = (LocationManager) getContext().
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));
        }

        googleMap.setMyLocationEnabled(true);
    }


    private void addPokemonHeatmap(List<PokemonLog> pokemonLogs) {
        List<LatLng> list = new ArrayList<>();

        if (heatmapOverlayOptions != null) {
            // Remove the previous heatmap overlay. Maybe this step is not needed..
        }

        // Get the list of all geoPoints from the pokemon logs.
        for (PokemonLog pokemonLog : pokemonLogs) {
            list.add(new LatLng(pokemonLog.getLocation().getLatitude(),
                    pokemonLog.getLocation().getLongitude()));

        }

        // Prepare the tile overlay, using the heat map tile provider.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        heatmapOverlayOptions = new TileOverlayOptions().tileProvider(provider);

        // Add the tile overlay to the map
        googleMap.addTileOverlay(heatmapOverlayOptions);
    }
}
