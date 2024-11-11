package com.example.magicmusic.API;

import com.example.magicmusic.models.JamendoResponse;
import com.example.magicmusic.models.PlaylistResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JamendoApi {
    @GET("tracks")
    Call<JamendoResponse> getTracks(
            @Query("format") String format,
            @Query("limit") int limit
    );

    @GET("playlists/tracks")
    Call<TracksFromPlaylistResponse> getTracksFromPlaylist(
            @Query("format") String format,
            @Query("id") String playlistId,
            @Query("limit") int limit
    );

    @GET("tracks")
    Call<JamendoResponse> searchTracks(
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("namesearch") String keyword
    );

    @GET("playlists")
    Call<PlaylistResponse> getPlaylists(
            @Query("format") String format,
            @Query("limit") int limit
    );

    @GET("favorites")
    Call<JamendoResponse> getFavorites(
            @Query("format") String format,
            @Query("limit") int limit
    );
}
