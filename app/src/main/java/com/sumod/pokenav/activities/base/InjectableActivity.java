package com.sumod.pokenav.activities.base;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sumod.pokenav.App;


public class InjectableActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.inject(this);
    }
}
