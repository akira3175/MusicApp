package com.example.magicmusic.GUI;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongAdapter;
import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.models.JamendoResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListMusicActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "ec0e93fa";
    private MediaPlayer mediaPlayer;
    private TextView songTitle;
    private Button playButton, pauseButton, stopButton;
    private String currentSongUrl;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<JamendoResponse.Track> trackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_music);

        songTitle = findViewById(R.id.song_title);
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        stopButton = findViewById(R.id.stop_button);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        JamendoApi apiService = ApiClient.getClient().create(JamendoApi.class);
        Call<JamendoResponse> call = apiService.getTracks(CLIENT_ID, "json", 10);

        call.enqueue(new Callback<JamendoResponse>() {
            @Override
            public void onResponse(Call<JamendoResponse> call, Response<JamendoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    trackList = response.body().getResults();
                    for (JamendoResponse.Track track : trackList) {
                        Log.d("Jamendo", "Track ID: " + track.getId());
                        Log.d("Jamendo", "Track Name: " + track.getName());
                        Log.d("Jamendo", "Artist: " + track.getArtist_name());
                        Log.d("Jamendo", "Preview URL: " + track.getAudio()); // Kiểm tra URL preview
                    }
                    // Cập nhật giao diện người dùng
                    songAdapter = new SongAdapter(ListMusicActivity.this, trackList);
                    recyclerView.setAdapter(songAdapter);

                    // Thiết lập sự kiện khi bài hát được chọn
                    songAdapter.setOnItemClickListener((track) -> {
                        setCurrentSong(track.getAudio(), track.getName());
                    });

                    Log.d("Jamendo", response.body().toString());
                } else {
                    Log.e("Jamendo", "No tracks found or response failed.");
                }
            }

            @Override
            public void onFailure(Call<JamendoResponse> call, Throwable t) {
                Log.e("Jamendo", "Error: " + t.getMessage());
            }
        });

        playButton.setOnClickListener(v -> playMusic());
        pauseButton.setOnClickListener(v -> pauseMusic());
        stopButton.setOnClickListener(v -> stopMusic());
    }

    private void playMusic() {
        if (currentSongUrl != null) {
            if (mediaPlayer != null) {
                mediaPlayer.release(); // Giải phóng nếu đang phát
            }
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(currentSongUrl);
                mediaPlayer.prepareAsync(); // Chuẩn bị bài hát
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start()); // Phát ngay khi đã chuẩn bị
            } catch (Exception e) {
                Log.e("MediaPlayer", "Error setting data source", e);
            }
        } else {
            Log.e("MediaPlayer", "Current song URL is null");
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); // Giải phóng tài nguyên
            mediaPlayer = null;
        }
    }

    public void setCurrentSong(String url, String name) {
        currentSongUrl = url;
        songTitle.setText(name); // Cập nhật tên bài hát
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Giải phóng tài nguyên khi không còn cần thiết
            mediaPlayer = null;
        }
    }
}
