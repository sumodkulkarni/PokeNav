package com.sumod.pokenav;


import android.app.Application;

import com.facebook.stetho.Stetho;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseStethoInterceptor;
import com.sumod.pokenav.model.Pokemon;
import com.sumod.pokenav.model.PokemonLog;

import dagger.ObjectGraph;
import lombok.Getter;


public class App extends Application {
    public final static String PARSE_HOST = "http://pokenav.schoolofandroid.com/parse";
    public final static String PARSE_APPID = "pokenav";

    @Getter static ObjectGraph applicationGraph;


    public static <T> T inject(T instance) {
        if (applicationGraph == null) return null;
        return applicationGraph.inject(instance);
    }


    public static <T> T get(Class<T> instance) {
        if (applicationGraph == null) return null;
        return applicationGraph.get(instance);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        applicationGraph = ObjectGraph.create().plus(new ActivityModule(this));
        App.inject(this);

        // Setup parse
        Stetho.initializeWithDefaults(this);
        ParseObject.registerSubclass(Pokemon.class);
        ParseObject.registerSubclass(PokemonLog.class);
        Parse.addParseNetworkInterceptor(new ParseStethoInterceptor());
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(PARSE_APPID)
                .clientKey("")
                .server(PARSE_HOST)
                .build()
        );
    }
}
