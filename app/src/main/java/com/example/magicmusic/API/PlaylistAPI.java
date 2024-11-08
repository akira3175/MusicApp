package com.example.magicmusic.API;

import com.example.magicmusic.models.PlaylistResponse;
import com.example.magicmusic.models.TrackResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaylistAPI {
  @GET("playlists")
  Call<PlaylistResponse> getPlaylists(
          @Query("client_id") String clientId,
          @Query("format") String format,
          @Query("limit") int limit,
          @Query("id") Integer id
  );

  @GET("playlists/tracks")
  Call<TrackResponse> getTracks(
          @Query("client_id") String clientId,
          @Query("format") String format,
          @Query("limit") int limit,
          @Query("id") Integer id
  );
}
