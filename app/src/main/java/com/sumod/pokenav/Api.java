package com.sumod.pokenav;


import com.sumod.pokenav.model.PokemonHistory;
import com.sumod.pokenav.model.User;

import java.io.IOException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public class Api {

    public static OkHttpClient createClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        User currentUser = App.get(User.class);

        // Create a JWToken using the user'id as the subject
        final String jwtToken = Jwts.builder()
                .setSubject(currentUser.getId())
                .signWith(SignatureAlgorithm.HS512, App.JWT_KEY)
                .compact();

        client.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Customize the request
                Request request = original.newBuilder()
                        .header("Authorization", "JWT " + jwtToken)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();

                okhttp3.Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(interceptor);
        }

        return client.build();
    }


    public interface ApiService {
        /**
         * Use this endpoint to search for pokemons
         *
         * @param pokemonId The id of pokemon. If left blank, will query for all pokemons
         * @param latitude  The lat to search for.
         * @param longitude The long to search for.
         * @param distance  How far from the (lat, long) should we get the results for
         * @param page      And at what page should we get the results
         * @return {@link PokemonHistory.SearchResults} of the pokemons found
         */
        @GET("/pokemons")
        Call<PokemonHistory.SearchResults> searchPokemons(
                @Query("pokemonId") Number pokemonId,
                @Query("lat") Double latitude,
                @Query("long") Double longitude,
                @Query("distance") Double distance,
                @Query("page") Long page
        );


        /**
         * Submit a pokemon which has been found to the pokenav.
         *
         * @param pokemonId The id of the pokemon
         * @param latitude  The latitude at which the pokemon was found.
         * @param longitude The longitude at which the pokemon was found.
         * @return A {@link PokemonHistory} containing details of the find.
         */
        @POST("/pokemons")
        Call<PokemonHistory> submitPokemon(
                @Query("pokemonId") Number pokemonId,
                @Query("lat") Double latitude,
                @Query("long") Double longitude
        );


        /**
         * @param user
         * @return
         */
        @POST("/login")
        Call<User> login(@Body User user);
    }
}
