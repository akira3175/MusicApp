package com.example.magicmusic.GUI;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.magicmusic.adapters.SongPlayerWidget;
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
    private List<Track> trackList;
    private TextView songTitle;
    private RelativeLayout listContent;
    private SongPlayerWidget songPlayerWidget;

    private MediaPlayer mediaPlayer;
    private String currentSongUrl;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton playPreviousButton;
    private ImageButton playNextButton;
    private ImageButton loopButton;
    private int playFunction = 0;
    private int loopFunction = 1;
    private Track currentTrack;

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

        // Xử lý đóng intent search
        prevButton.setOnClickListener(v -> {
            finish();
            onDestroy();
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
                        setCurrentSong(track.getAudio());
                        stopMusic();
                        playFunction = 2; // 2 -> Trạng thái nhạc đang phát
                        songPlayerWidget.setPlayButtonState(playFunction); // set hình ảnh nút play hoặc pause
                        songPlayerWidget.setSongPlayerView(
                                track.getName(),
                                track.getArtist_name(),
                                track.getImage(),
                                playFunction, loopFunction);
                        playMusic(currentSongUrl);
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
                                setCurrentSong(track.getAudio());
                                stopMusic();
                                playFunction = 2; // 2 -> Trạng thái nhạc đang phát
                                songPlayerWidget.setPlayButtonState(playFunction); // set hình ảnh nút play hoặc pause
                                songPlayerWidget.setSongPlayerView(
                                        track.getName(),
                                        track.getArtist_name(),
                                        track.getImage(),
                                        playFunction, loopFunction);
                                playMusic(currentSongUrl);
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

    // Set hình ảnh cho các nút bấm
    private void Logic() {
        playButton = songPlayerWidget.getRootView().findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongUrl != null) {
                    if (mediaPlayer.isPlaying() && playFunction == 2) {      // Từ Play sang Pause
                        playFunction = 1;
                        pauseMusic();
                        songPlayerWidget.setPlayButtonState(playFunction); // set hình ảnh nút play hoặc pause
                    } else if (!mediaPlayer.isPlaying() && playFunction == 1) {    // Từ Pause sang Play
                        playFunction = 2;
                        continueMusic();
                        songPlayerWidget.setPlayButtonState(playFunction);
                    }
                }
            }
        });

        playPreviousButton = songPlayerWidget.getRootView().findViewById(R.id.play_back_button);
        // Chỉ thực hiện trong danh sách đã tìm kiếm
        playPreviousButton.setOnClickListener(v -> {
            if (currentTrack != null) {
                playFunction = 2;
                int prevIndex = (trackList.indexOf(currentTrack) - 1) < 0 ? trackList.size() - 1 : trackList.indexOf(currentTrack) - 1;
                currentTrack = trackList.get(prevIndex);
                setCurrentSong(currentTrack.getAudio());
                playMusic(currentSongUrl);
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
            if (currentTrack != null) {
                playFunction = 2;
                int nextIndex = (trackList.indexOf(currentTrack) + 1) == trackList.size() ? 0 : trackList.indexOf(currentTrack) + 1;
                currentTrack = trackList.get(nextIndex);
                setCurrentSong(currentTrack.getAudio());
                playMusic(currentSongUrl);
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

    private static <T> T getRandomItem(ArrayList<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

//    private AlbumTrackList getPreviousTrack() {
//        for (int i = 0; i < albumTrackLists.size(); i++) {
//            if (albumTrackLists.get(i).getCurrentSongUrl().equals(currentSongUrl) && i > 0) {
//                Log.d("ListMusicActivity", "Previous Track: " + albumTrackLists.get(i - 1).getCurrentSongName());
//                return albumTrackLists.get(i - 1);
//            }
//        }
//        Log.d("ListMusicActivity", "No previous track found for: " + currentSongUrl);
//        return null;
//    }

//    private AlbumTrackList getNextTrack() {
//        for (int i = 0; i < albumTrackLists.size(); i++) {
//            if (albumTrackLists.get(i).getCurrentSongUrl().equals(currentSongUrl) && i < albumTrackLists.size() - 1) {
//                Log.d("ListMusicActivity", "Next Track: " + albumTrackLists.get(i + 1).getCurrentSongName());
//                return albumTrackLists.get(i + 1);
//            }
//        }
//        Log.d("ListMusicActivity", "No next track found for: " + currentSongUrl);
//        return null;
//    }

    private void playMusic(String currentSongUrl) {
        if (currentSongUrl != null) {
            if (mediaPlayer != null) {
                mediaPlayer.release(); // Giải phóng nếu đang phát
            }
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(currentSongUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());

                // Cài đặt lặp lại khi bài hát kết thúc theo chế độ lặp hiện tại
                mediaPlayer.setOnCompletionListener(mp -> {
                    switch (loopFunction) {
                        case 1: // NoRepeat
                            Toast.makeText(SearchActivity.this, "No Repeat", Toast.LENGTH_SHORT).show();
//                            // Nếu không lặp lại, phát bài tiếp theo
//                            AlbumTrackList nextTrack = getNextTrack();
//                            if (nextTrack != null) {
//                                playMusic(nextTrack.getCurrentSongUrl());
//                                songPlayerWidget.setSongPlayerView(
//                                        nextTrack.getCurrentSongName(),
//                                        nextTrack.getCurrentSongArtist(),
//                                        nextTrack.getCurrentSongImage(),
//                                        playFunction,
//                                        loopFunction
//                                );
//                            }
                            break;
                        case 2: // Repeat
                            playMusic(currentSongUrl); // Phát lại bài hiện tại
                            break;
                        case 3: // Shuffle
//                            AlbumTrackList randomTrack = getRandomItem(albumTrackLists);
//                            if (randomTrack != null) {
//                                playMusic(randomTrack.getCurrentSongUrl());
//                                songPlayerWidget.setSongPlayerView(
//                                        randomTrack.getCurrentSongName(),
//                                        randomTrack.getCurrentSongArtist(),
//                                        randomTrack.getCurrentSongImage(),
//                                        playFunction,
//                                        loopFunction
//                                );
//                            }
                            break;
                    }
                });
                mediaPlayer.prepareAsync();  // Prepare the song
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());  // Start when prepared
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

    private void continueMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); // Giải phóng tài nguyên
            mediaPlayer = null;
        }
    }

    public void setCurrentSong(String url) {
        currentSongUrl = url;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}