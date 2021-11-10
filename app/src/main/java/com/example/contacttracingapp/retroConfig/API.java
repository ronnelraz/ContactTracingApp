package com.example.contacttracingapp.retroConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private static Retrofit retrofit;
    public static APIInterface getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(retro_config.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        APIInterface apiInterface = retrofit.create(APIInterface.class);
        return apiInterface;
    }
}
