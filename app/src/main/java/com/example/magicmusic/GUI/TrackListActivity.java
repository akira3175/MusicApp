package com.example.magicmusic.GUI;

import static com.example.magicmusic.GUI.ListMusicActivity.CLIENT_ID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magicmusic.API.AlbumApi;
import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.TrackAdapter;
import com.example.magicmusic.models.Playlist;
import com.example.magicmusic.models.TrackResponse;
import com.example.magicmusic.models.Track;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackListActivity extends AppCompatActivity {

    RecyclerView trackList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        trackList = findViewById(R.id.song_list);
        trackList.setLayoutManager(new LinearLayoutManager(this));

        fetchAndInflateTracks();
    }

    public void fetchAndInflateTracks() {
        Intent intent = getIntent();
        int playlistId = intent.getIntExtra("playlistId", 500089797);

        AlbumApi apiService = ApiClient.getClient().create(AlbumApi.class);
        Call<TrackResponse> call = apiService.getTracks(CLIENT_ID, "json", 10, playlistId);
        Log.d("call: ", call.request().url().toString());

        call.enqueue(new Callback<TrackResponse>() {
            @Override
            public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Playlist> playlists = response.body().getResults();
                    Log.d("Results size", "List Size: " + playlists.size());
                    List<Track> tracks = new ArrayList<>();
                    if(!playlists.isEmpty()) {
                        tracks.addAll(playlists.get(0).getTracks());
                    }

                    Log.d("tracks size", "Track List Size: " + tracks.size());
                    TrackAdapter trackAdapter = new TrackAdapter(tracks, TrackListActivity.this, new TrackAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Track track) {
                            Toast.makeText(TrackListActivity.this, track.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    trackList.setAdapter(trackAdapter);
                    Log.d("Fetched: ", response.body().toString());
                } else {
                    Log.e("Jamendo", "No tracks found or response failed.");
                }
            }

            @Override
            public void onFailure(Call<TrackResponse> call, Throwable t) {
                Log.e("Jamendo", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }


}
