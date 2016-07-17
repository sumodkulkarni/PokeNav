package com.sumod.pokenav.fragments;

/**
 * Created by sumodkulkarni on 16/7/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.sumod.pokenav.Constants;
import com.sumod.pokenav.R;
import com.sumod.pokenav.activities.AddActivity;
import com.sumod.pokenav.activities.AddActivity_;
import com.sumod.pokenav.utils.PrefManager;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionMenu fabAdd;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "On View Created Launched!");

        if ((Boolean) PrefManager.getPrefs(getContext(), PrefManager.PREF_MAIN_ACT_LAUNCH, Boolean.class)){
            Log.d(TAG, "If statement passed");
            coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.frag_home_coordinatorLayout);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Welcome, " + PrefManager.getPrefs(getContext(), PrefManager.PREF_USER_NAME, String.class) + "!",
                            Snackbar.LENGTH_LONG);
            snackbar.show();
            PrefManager.putPrefs(getContext(), PrefManager.PREF_MAIN_ACT_LAUNCH, false);
        }

        fabAdd = (FloatingActionMenu) view.findViewById(R.id.fab_menu_add);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);

        fabAdd.setClosedOnTouchOutside(true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddActivity_.class);
                intent.putExtra("add_item", Constants.ADD_POKEMON);
                startActivity(intent);
                fabAdd.close(true);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddActivity_.class);
                intent.putExtra("add_item", Constants.ADD_GYM);
                startActivity(intent);
                fabAdd.close(true);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddActivity_.class);
                intent.putExtra("add_item", Constants.ADD_POKESTOP);
                startActivity(intent);
                fabAdd.close(true);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
