package com.sumod.pokenav.fragments;


import android.support.v4.app.Fragment;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.sumod.pokenav.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


@EFragment(R.layout.fragment_heatmap)
public class HeatmapFragment extends Fragment implements OnMapReadyCallback {
    SupportMapFragment mapsFragment;
    @ViewById View mapContainer;


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
        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(-37.1886, 145.708));
        list.add(new LatLng(-37.8361, 144.845));
        list.add(new LatLng(-38.4034, 144.192));
        list.add(new LatLng(-38.7597, 143.67));
        list.add(new LatLng(-36.9672, 141.083));
        list.add(new LatLng(-37.1886, 145.708));
        list.add(new LatLng(-37.1886, 145.708));

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();

        googleMap.setMyLocationEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-37.1886, 145.708), 16));

        // Add a tile overlay to the map, using the heat map tile provider.
        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
