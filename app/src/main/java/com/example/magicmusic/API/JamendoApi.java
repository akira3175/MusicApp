package com.example.magicmusic.API;

import com.example.magicmusic.models.JamendoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JamendoApi {
    @GET("tracks") // Chỉ cần gọi đến "tracks"
    Call<JamendoResponse> getTracks(
            @Query("client_id") String clientId,
            @Query("format") String format,
            @Query("limit") int limit
    );
}
