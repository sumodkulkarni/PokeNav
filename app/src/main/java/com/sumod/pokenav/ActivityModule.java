package com.sumod.pokenav;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sumod.pokenav.activities.LoginActivity;
import com.sumod.pokenav.model.User;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;


@Module(
        injects = {
                LoginActivity.class,
                User.class,

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
    @Singleton
    public User providesUser() {
        return new User();
    }


    @Provides
    @Singleton
    public Api.ApiService providesApi(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.HOST)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(Api.createClient())
                .build();

        return retrofit.create(Api.ApiService.class);
    }
}
