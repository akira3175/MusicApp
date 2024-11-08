package com.example.magicmusic.GUI;

import static com.example.magicmusic.GUI.ListMusicActivity.CLIENT_ID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
//import com.example.magicmusic.API.PlaylistAPI;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.PlaylistAdapter;
import com.example.magicmusic.adapters.ImageSliderAdapter;
import com.example.magicmusic.models.Playlist;
import com.example.magicmusic.models.PlaylistResponse;
//import com.example.magicmusic.models.TrackResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;

import retrofit2.Callback;
import retrofit2.Response;

/*
*   author: truong
* */

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    List<Integer> imageList;
    RecyclerView recyclerView;
//    ProgressBar progressBar;
    ArrayList<Integer> popularPlaylistIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //popular playlistID
        popularPlaylistIds.addAll(List.of(500608490, 500608900, 500608899
                , 500608901, 500608471, 500607433, 500606825, 500605606, 500605176, 500602528, 500599669));


        //slider
        viewPager = findViewById(R.id.slider);
        imageList = Arrays.asList(
                R.drawable.slider1,
                R.drawable.slider2,
                R.drawable.slider3,
                R.drawable.slider4
        );
        ImageSliderAdapter adapter = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapter);
        CircleIndicator3 indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);


        //playlists
        recyclerView = findViewById(R.id.list);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        fetchPlaylistsByIds(popularPlaylistIds);


    }

    public void fetchPlaylistsByIds(List<Integer> popularPlaylistIds) {
        // Danh sách để lưu tất cả các playlist đã tải về
        List<Playlist> allPlaylists = new ArrayList<>();
        // Cập nhật giao diện hoặc adapter sau khi nhận được phản hồi
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(allPlaylists, MainActivity.this, new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Playlist playlist) {
                Log.d("Jamendo", "fetching playlist: " + playlist.getId());
                Intent intent = new Intent(MainActivity.this, DemoPlaylistTrackActivity.class);
                intent.putExtra("playlistId", playlist.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(playlistAdapter);

        JamendoApi apiService = ApiClient.getClient().create(JamendoApi.class);
        for (int id : popularPlaylistIds) {
            Call<PlaylistResponse> call = apiService.getPlaylistTracks("json", id + "", 4);
            Log.d("call: ", call.request().url().toString());
            call.enqueue(new Callback<PlaylistResponse>() {
                @Override
                public void onResponse(@NonNull Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Playlist> playlists = response.body().getResults();

                        for (Playlist playlist : playlists) {
                            playlistAdapter.insertItem(playlist);
                        }

                        for (Playlist playlist : playlists) {
                            Log.d("Jamendo", "Playlist ID: " + playlist.getId());
                            Log.d("Jamendo", "Playlist Name: " + playlist.getName());
                        }
                    } else {
                        Log.e("Jamendo", "No playlists found or response failed for ID: " + id);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PlaylistResponse> call, @NonNull Throwable t) {
                    Log.e("Jamendo", "Error fetching playlist with ID " + id + ": " + t.getMessage());
                }
            });
        }
    }



}