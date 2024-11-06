package com.example.magicmusic.GUI;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.MusicAdapter;
import com.example.magicmusic.models.JamendoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private TextView resultsText;
    private static final String TAG = "SearchActivity";
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private List<JamendoResponse.Track> trackList;
    private ImageButton playButton;
    private TextView songTitle;

    private MediaPlayer mediaPlayer;
    private String currentSongUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchInput);
        resultsText = findViewById(R.id.resultsText);
        Button searchButton = findViewById(R.id.searchButton);
        playButton = (ImageButton) findViewById(R.id.btnPlay);
        playButton.setTag(android.R.drawable.ic_media_play);
        playButton.setImageResource(android.R.drawable.ic_media_play);

        // Xử lý khi người dùng nhấn nút tìm kiếm
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchInput.getText().toString();
                if (!keyword.isEmpty()) {
                    searchTracks(keyword, 10);
                } else {
                    resultsText.setText("Vui lòng nhập từ khóa.");
                }
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        JamendoApi apiService = ApiClient.getClient().create(JamendoApi.class);
        Call<JamendoResponse> call = apiService.getTracks("json", 10);

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
                    musicAdapter = new MusicAdapter(SearchActivity.this ,trackList);
                    recyclerView.setAdapter(musicAdapter);

                    // Thiết lập sự kiện khi bài hát được chọn
                    musicAdapter.setOnItemClickListener((track) -> {
                        setCurrentSong(track.getAudio(), track.getName());
                        stopMusic();
                        playMusic();
                        playButton.setImageResource(android.R.drawable.ic_media_pause); // Đổi nút thành "Pause" sau khi phát nhạc
                        Toast.makeText(getApplicationContext(), "Clicked: " + track.getName(), Toast.LENGTH_SHORT).show();
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

        playButton.setOnClickListener(v -> {
            ImageButton button = (ImageButton) v; // Chuyển đổi v thành ImageButton

            // Kiểm tra ảnh hiện tại bằng cách so sánh tag
            if (button.getTag() != null && (int) button.getTag() == android.R.drawable.ic_media_play) {
                // Thực hiện hành động phát nhạc
                playMusic(); // Gọi phương thức phát nhạc
                playButton.setImageResource(android.R.drawable.ic_media_pause);
                playButton.setTag(android.R.drawable.ic_media_pause); // Cập nhật tag
            } else {
                // Thực hiện hành động dừng nhạc
                pauseMusic(); // Gọi phương thức dừng nhạc
                playButton.setImageResource(android.R.drawable.ic_media_play);
                playButton.setTag(android.R.drawable.ic_media_play); // Cập nhật tag
            }
        });
    }

    // Hàm tìm kiếm bài hát5
    private void searchTracks(String keyword, int limit) {
        ApiClient.getJamendoApi().searchTracks("json", limit, keyword)
                .enqueue(new Callback<JamendoResponse>() {
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
                            musicAdapter = new MusicAdapter(SearchActivity.this ,trackList);
                            recyclerView.setAdapter(musicAdapter);

                            // Thiết lập sự kiện khi bài hát được chọn
                            musicAdapter.setOnItemClickListener((track) -> {
                                setCurrentSong(track.getAudio(), track.getName());
                                stopMusic();
                                playMusic();
                                playButton.setImageResource(android.R.drawable.ic_media_play);
                                Toast.makeText(getApplicationContext(), "Clicked: " + track.getName(), Toast.LENGTH_SHORT).show();
                            });

                            Log.d("Jamendo", response.body().toString());
                        } else {
                            Log.e("Jamendo", "No tracks found or response failed.");
                        }
                    }

                    @Override
                    public void onFailure(Call<JamendoResponse> call, Throwable t) {
                        Log.e(TAG, "API Error: " + t.getMessage());
                        resultsText.setText("Đã xảy ra lỗi khi tìm kiếm.");
                    }
                });
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

    private void resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start(); // Tiếp tục phát nhạc từ vị trí đã tạm dừng
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
        songTitle = (TextView) findViewById(R.id.txtMusicPlay);
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