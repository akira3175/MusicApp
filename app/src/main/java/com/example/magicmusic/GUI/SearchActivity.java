package com.example.magicmusic.GUI;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongAdapter;
import com.example.magicmusic.adapters.TrackAdapter;
import com.example.magicmusic.adapters.SongPlayerWidget;
import com.example.magicmusic.controllers.MusicController;
import com.example.magicmusic.models.AlbumTrackList;
import com.example.magicmusic.models.JamendoResponse;
import com.example.magicmusic.models.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private TextView resultsText;
    private static final String TAG = "SearchActivity";
    private RecyclerView recyclerView;
    private SongAdapter SongAdapter;
    private TrackAdapter trackAdapter;
    private List<Track> trackList;
    private TextView songTitle;
    private RelativeLayout listContent;
    private SongPlayerWidget songPlayerWidget;
    private ProgressBar progressBar;

    private int currentTrackIndex;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton playPreviousButton;
    private ImageButton playNextButton;
    private ImageButton loopButton;
    private int playFunction = 0;
    private int loopFunction = 1;
    private Track currentTrack;
    private MusicController musicController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchInput);
        resultsText = findViewById(R.id.resultsText);
        ImageButton searchButton = findViewById(R.id.searchButton);
        prevButton = findViewById(R.id.prevButton);
        songPlayerWidget = new SongPlayerWidget(SearchActivity.this);
        listContent = findViewById(R.id.song_player_widget_container);
        listContent.addView(songPlayerWidget);
        progressBar = findViewById(R.id.progressBar);

        musicController = new MusicController(this, (md) -> {
            hideLoadingScreen();
        });
        // Đặt callback khi Service kết nối thành công
        musicController.setOnServiceConnectedListener(() -> {
            if (musicController.isPlaying()) {
                Track track = musicController.getCurrentTrack();
                if(track == null) return;
                playFunction = 2;
                Log.d("ListMusicActivity", "Music is playing: " + track.getName());
                songPlayerWidget.setSongPlayerView(
                        track.getName(),
                        track.getArtist_name(),
                        track.getImage(),
                        playFunction,
                        loopFunction
                );
            } else {
                Log.d("ListMusicActivity", "Music is not playing");
            }
        });

        // Xử lý đóng intent search
        prevButton.setOnClickListener(v -> {
            finish();
        });

        // Xử lý khi người dùng nhấn nút tìm kiếm
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchInput.getText().toString();
                if (!keyword.isEmpty()) {
                    resultsText.setText("Kết quả tìm kiếm");
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
        Call<JamendoResponse> call = apiService.getTracks("json", 50);
        call.enqueue(new Callback<JamendoResponse>() {
            @Override
            public void onResponse(Call<JamendoResponse> call, Response<JamendoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    trackList = response.body().getResults();
                    // Cập nhật giao diện người dùng
                    SongAdapter = new SongAdapter(SearchActivity.this ,trackList);
                    recyclerView.setAdapter(SongAdapter);

                    // Thiết lập sự kiện khi bài hát được chọn
                    SongAdapter.setOnItemClickListener((track) -> {
                        currentTrack = track;
                        playFunction = 2; // 2 -> Trạng thái nhạc đang phát
                        songPlayerWidget.setSongPlayerView(
                                track.getName(),
                                track.getArtist_name(),
                                track.getImage(),
                                playFunction, loopFunction);
                        showLoadingScreen();
                        musicController.playTrack(track);
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
        // Xử lý logic cho các nút bấm
        Logic();
    }

    // Hàm tìm kiếm bài hát5
    private void searchTracks(String keyword, int limit) {
        ApiClient.getJamendoApi().searchTracks("json", limit, keyword)
                .enqueue(new Callback<JamendoResponse>() {
                    @Override
                    public void onResponse(Call<JamendoResponse> call, Response<JamendoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            trackList = response.body().getResults();
                            // Cập nhật giao diện người dùng
                            SongAdapter = new SongAdapter(SearchActivity.this ,trackList);
                            Log.d("Jamendo", "Size Tracks: " + SongAdapter.getItemCount());
                            recyclerView.setAdapter(SongAdapter);
                            // Thiết lập sự kiện khi bài hát được chọn
                            SongAdapter.setOnItemClickListener((track) -> {
                                currentTrack = track;
                                playFunction = 2; // 2 -> Trạng thái nhạc đang phát
                                songPlayerWidget.setSongPlayerView(
                                        track.getName(),
                                        track.getArtist_name(),
                                        track.getImage(),
                                        playFunction, loopFunction);
                                showLoadingScreen();
                                musicController.playTrack(track);
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

    // Set hình ảnh cho các nút bấm
    private void Logic() {
        playButton = songPlayerWidget.getRootView().findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTrackIndex != -1) {
                    if (musicController.isPlaying() && playFunction == 2) {      // Từ Play sang Pause
                        playFunction = 1;
                        musicController.pauseTrack();
                        songPlayerWidget.setPlayButtonState(playFunction);
                    } else if (!musicController.isPlaying() && playFunction == 1) {    // Từ Pause sang Play
                        playFunction = 2;
                        musicController.resumeTrack();
                        songPlayerWidget.setPlayButtonState(playFunction);
                    }
                }
            }
        });

        playPreviousButton = songPlayerWidget.getRootView().findViewById(R.id.play_back_button);
        playPreviousButton.setOnClickListener(v -> {
            currentTrack = getPreviousTrack();
            if (currentTrack != null) {
                playFunction = 2;
                showLoadingScreen();
                musicController.playTrack(currentTrack);
                songPlayerWidget.setSongPlayerView(
                        currentTrack.getName(),
                        currentTrack.getArtist_name(),
                        currentTrack.getImage(),
                        playFunction,
                        loopFunction
                );
            } else {
                Log.d("ListMusicActivity", "No previous track available");
            }
        });

        playNextButton = songPlayerWidget.getRootView().findViewById(R.id.play_next_button);
        playNextButton.setOnClickListener(v -> {
            currentTrack = getNextTrack();
            if (currentTrack != null) {
                playFunction = 2;
                showLoadingScreen();
                musicController.playTrack(currentTrack);
                songPlayerWidget.setSongPlayerView(
                        currentTrack.getName(),
                        currentTrack.getArtist_name(),
                        currentTrack.getImage(),
                        playFunction,
                        loopFunction
                );
            } else {
                Log.d("ListMusicActivity", "No next track available");
            }
        });

        loopButton = songPlayerWidget.getRootView().findViewById(R.id.play_mode_button);
        loopButton.setOnClickListener(v -> {
            loopFunction = (loopFunction % 3) + 1;  // Chuyển vòng từ NoRepeat (1) -> Repeat (2) -> Shuffle (3)
            songPlayerWidget.setLoopButtonState(loopFunction);
            Log.d("ListMusicActivity", "Loop mode changed to: " + loopFunction);
        });
    }

    private void showLoadingScreen() {
        Log.d("ListMusicActivity", "Loading screen shown");
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar
    }

    private void hideLoadingScreen() {
        Log.d("ListMusicActivity", "Loading screen hidden");
        progressBar.setVisibility(View.GONE); // Ẩn ProgressBar
    }

    private static <T> T getRandomItem(ArrayList<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public Track getPreviousTrack() {
        int index = trackList.indexOf(currentTrack);
        if (index > 0) {
            return trackList.get(index - 1);
        } else {
            return trackList.get(trackList.size() - 1);
        }
    }

    public Track getNextTrack() {
        int index = trackList.indexOf(currentTrack);
        if (index < trackList.size() - 1) {
            return trackList.get(index + 1);
        } else {
            return trackList.get(0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}