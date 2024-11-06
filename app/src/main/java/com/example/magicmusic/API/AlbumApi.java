package com.example.magicmusic.API;

import com.example.magicmusic.models.AlbumResponse;
import com.example.magicmusic.models.TrackResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AlbumApi {
  @GET("albums") // Chỉ cần gọi đến "tracks"
  Call<AlbumResponse> getAlbums(
          @Query("client_id") String clientId,
          @Query("format") String format,
          @Query("limit") int limit,
          @Query("order") String order,
          @Query("search") String search
  );

//  @GET("tracks") // Endpoint cho tracks
//  Call<TrackResponse> getTracks(
//          @Query("client_id") String clientId,
//          @Query("format") String format,
//          @Query("limit") int limit,
//          @Query("order") String order
//  );
}
