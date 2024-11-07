package com.example.magicmusic.API;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.jamendo.com/v3.0/";
    private static final String CLIENT_ID = "ec0e93fa";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // Thời gian chờ kết nối
                    .writeTimeout(30, TimeUnit.SECONDS)   // Thời gian chờ ghi dữ liệu
                    .readTimeout(30, TimeUnit.SECONDS)    // Thời gian chờ đọc dữ liệu
                    .build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            HttpUrl originalUrl = originalRequest.url();

                            // Thêm client_id vào query params
                            HttpUrl urlWithClientId = originalUrl.newBuilder()
                                    .addQueryParameter("client_id", CLIENT_ID)
                                    .build();

                            // Tạo request mới với URL mới
                            Request newRequest = originalRequest.newBuilder()
                                    .url(urlWithClientId)
                                    .build();

                            return chain.proceed(newRequest);
                        }
                    })
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static JamendoApi getJamendoApi() {
        return getClient().create(JamendoApi.class);
    }
}
