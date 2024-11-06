package com.example.magicmusic.GUI;

import static com.example.magicmusic.GUI.ListMusicActivity.CLIENT_ID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.API.PlaylistAPI;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.PlaylistAdapter;
import com.example.magicmusic.adapters.ImageSliderAdapter;
import com.example.magicmusic.models.Playlist;
import com.example.magicmusic.models.PlaylistResponse;
import com.example.magicmusic.models.TrackResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    List<Integer> imageList;
    RecyclerView recyclerView;
    List<Playlist> playlistList;
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
        popularPlaylistIds.add(500608490);
        popularPlaylistIds.add(500608900);
        popularPlaylistIds.add(500608899);
        popularPlaylistIds.add(500608901);
        popularPlaylistIds.add(500608471);
        popularPlaylistIds.add(500607433);
        popularPlaylistIds.add(500606825);
        popularPlaylistIds.add(500605606);
        popularPlaylistIds.add(500605176);


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

//        fetchAndInflate();
//        fetchAndInflatePopular();
        fetchPlaylistsByIds(popularPlaylistIds);
    }

    public void fetchAndInflate() {
        PlaylistAPI apiService = ApiClient.getClient().create(PlaylistAPI.class);
        Call<PlaylistResponse> call = apiService.getPlaylists(CLIENT_ID, "json", 10, null);
        Log.d("call: ", call.request().url().toString());
        call.enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playlistList = response.body().getResults();
                    // Cập nhật giao diện người dùng
                    PlaylistAdapter playlistAdapter = new PlaylistAdapter(playlistList, MainActivity.this, new PlaylistAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Playlist playlist) {
                            Log.d("Jamendo", "fetching playlist:"+playlist.getId());
                            Intent intent = new Intent(MainActivity.this, TrackListActivity.class);
                            intent.putExtra("playlistId", playlist.getId());
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(playlistAdapter);

                    for (Playlist playlist : playlistList) {
                        Log.d("Jamendo", "Track ID: " + playlist.getId());
                        Log.d("Jamendo", "Track Name: " + playlist.getName());
                    }
                    Log.d("Jamendo", response.body().toString());
                } else {
                    Log.e("Jamendo", "No tracks found or response failed.");
                }
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                Log.e("Jamendo", "Error: " + t.getMessage());
            }
        });

    }

    public void fetchPlaylistsByIds(List<Integer> popularPlaylistIds) {
        PlaylistAPI apiService = ApiClient.getClient().create(PlaylistAPI.class);

        // Danh sách để lưu tất cả các playlist đã tải về
        List<Playlist> allPlaylists = new ArrayList<>();

        for (int id : popularPlaylistIds) {
            Call<TrackResponse> call = apiService.getTracks(CLIENT_ID, "json", 4, id);

            call.enqueue(new Callback<TrackResponse>() {
                @Override
                public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Playlist> playlists = response.body().getResults();

                        // Thêm các playlist vào danh sách allPlaylists
                        allPlaylists.addAll(playlists);

                        // Cập nhật giao diện hoặc adapter sau khi nhận được phản hồi
                        PlaylistAdapter playlistAdapter = new PlaylistAdapter(allPlaylists, MainActivity.this, new PlaylistAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Playlist playlist) {
                                Log.d("Jamendo", "fetching playlist: " + playlist.getId());
                                Intent intent = new Intent(MainActivity.this, TrackListActivity.class);
                                intent.putExtra("playlistId", playlist.getId());
                                startActivity(intent);
                            }
                        });
                        recyclerView.setAdapter(playlistAdapter);

                        for (Playlist playlist : playlists) {
                            Log.d("Jamendo", "Playlist ID: " + playlist.getId());
                            Log.d("Jamendo", "Playlist Name: " + playlist.getName());
                        }
                    } else {
                        Log.e("Jamendo", "No playlists found or response failed for ID: " + id);
                    }
                }

                @Override
                public void onFailure(Call<TrackResponse> call, Throwable t) {
                    Log.e("Jamendo", "Error fetching playlist with ID " + id + ": " + t.getMessage());
                }
            });
        }
    }

}