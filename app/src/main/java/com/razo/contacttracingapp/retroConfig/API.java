package com.razo.contacttracingapp.retroConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private static Retrofit retrofit;
    public static APIInterface getClient(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        ConnectionPool connectionPool = new ConnectionPool(10, 10, TimeUnit.MINUTES);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectionPool(connectionPool)
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES);
        httpClient.interceptors().add(logging);
        httpClient.interceptors().add(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Connection", "Keep-Alive")
                    .method(original.method(), original.body())
                    .build();

            Response response = chain.proceed(request);

            if (!response.isSuccessful() || response.code()==503) {
                connectionPool.evictAll();
                return chain.proceed(request);
            } else {
                // Customize or return the response
                return response;
            }
        });
        OkHttpClient client = httpClient.build();


        retrofit = new Retrofit.Builder()
                .baseUrl(retro_config.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APIInterface apiInterface = retrofit.create(APIInterface.class);
        return apiInterface;
    }
}
