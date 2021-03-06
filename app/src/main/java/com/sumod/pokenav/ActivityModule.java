package com.sumod.pokenav;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sumod.pokenav.activities.LoginActivity;
import com.sumod.pokenav.fragments.FragmentDrawer;
import com.sumod.pokenav.utils.AlertDialogBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(
        injects = {
                LoginActivity.class,
                FragmentDrawer.class,
                App.class,
        },
        library = true
)
public class ActivityModule {
    private final Context context;
    private final Gson gson;


    public ActivityModule(Context context) {
        this.context = context;

        final GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }


    @Provides
    @Singleton
    public Context provideContext() {
        return context;
    }


    @Provides
    @Singleton
    public Gson providesGson() {
        return gson;
    }


    @Provides
    public AlertDialogBuilder provideAlertDialogBuilder(Context context) {
        return new AlertDialogBuilder.Impl();
    }
}
