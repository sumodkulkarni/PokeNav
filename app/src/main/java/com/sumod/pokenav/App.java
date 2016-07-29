package com.sumod.pokenav;


import android.app.Application;

import com.facebook.stetho.Stetho;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseStethoInterceptor;
import com.sumod.pokenav.model.Pokemon;
import com.sumod.pokenav.model.PokemonLog;

import javax.inject.Inject;

import dagger.ObjectGraph;
import lombok.Getter;


public class App extends Application {
    //    public final static String HOST = "http://192.168.1.101:3000/";
    public final static String HOST = "http://pokenav.schoolofandroid.com/";
    public final static String JWT_KEY = "c3Vtb2RrdWxrYXJuaQ==";


    @Getter static ObjectGraph applicationGraph;
    @Inject Api.ApiService api;


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
                .applicationId("pokenav")
                .clientKey("")
                .server("http://pokenav.schoolofandroid.com/parse")
                .build()
        );
    }
}
