package com.example.magicmusic.GUI;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongAdapter;
import com.example.magicmusic.models.JamendoResponse;
import com.example.magicmusic.models.Playlist;
import com.example.magicmusic.models.PlaylistResponse;
import com.example.magicmusic.models.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        JamendoApi apiService = ApiClient.getClient().create(JamendoApi.class);
        Call<PlaylistResponse> call = apiService.getPlaylists("json", 10);
        call.enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Playlist> playlists = response.body().getResults();

                    if (playlists != null && !playlists.isEmpty()) {
                        for (Playlist playlist : playlists) {
                            Log.d("Jamendo", "Playlist ID: " + playlist.getId());
                            Log.d("Jamendo", "Playlist Name: " + playlist.getName());
//                            Log.d("Jamendo", "Track Count: " + playlist.getTrackCount());
                            List<Track> tracks = playlist.getTracks(); // Lấy danh sách track

                            if (tracks != null && !tracks.isEmpty()) { // Thật ra thì méo có Track trong api này
                                for (Track track : tracks) {
                                    Log.d("Jamendo", "Track ID: " + track.getId());
                                    Log.d("Jamendo", "Track Name: " + track.getName());
                                    Log.d("Jamendo", "Artist: " + track.getArtist_name());
                                    Log.d("Jamendo", "Preview URL: " + track.getAudio());
                                }
                            } else {
                                Log.e("Jamendo", "No tracks found in playlist ID: " + playlist.getId());
                            }
                        }
                    } else {
                        Log.e("Jamendo", "No playlists found or playlists list is empty.");
                    }
                } else {
                    Log.e("Jamendo", "Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                Log.e("Jamendo", "Error: " + t.getMessage());
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



}