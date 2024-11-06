package com.example.magicmusic.GUI;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongAdapter;
import com.example.magicmusic.adapters.SongContentWidget;
import com.example.magicmusic.adapters.SongPlayerWidget;
import com.example.magicmusic.models.AlbumTrackList;
import com.example.magicmusic.models.FavoriteTrackList;
import com.example.magicmusic.models.JamendoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListMusicActivity extends AppCompatActivity {
    public static final String CLIENT_ID = "ec0e93fa";
    private MediaPlayer mediaPlayer;
    private TextView songTitle;
    private SongAdapter songAdapter;
    private List<JamendoResponse.Track> trackList;
    private RelativeLayout listContent;
    private SongPlayerWidget songPlayerWidget;
    private LinearLayout scrollViewContainer;
    private ArrayList<AlbumTrackList> albumTrackLists = new ArrayList<>();
    private ImageButton playButton;
    private ImageButton playPreviousButton;
    private ImageButton playNextButton;
    private ImageButton loopButton;
    private int playFunction = 0;
    private int loopFunction = 1;
    private String currentSongUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_music);


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
                        albumTrackLists.add(new AlbumTrackList(track.getAudio(), track.getName(), track.getArtist_name(), null, true, false));
                    }
                    // Cập nhật giao diện người dùng
                    songAdapter = new SongAdapter(ListMusicActivity.this, trackList);
//                    recyclerView.setAdapter(songAdapter);
                    SongContentView();

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

        scrollViewContainer = findViewById(R.id.scroll_view_container);
        songPlayerWidget = new SongPlayerWidget(ListMusicActivity.this);
        listContent = findViewById(R.id.song_player_widget_container);
        listContent.addView(songPlayerWidget);

        Header();
        SongContentView();
        Logic();
    }

    private void Header() {
        //Nút Back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListMusicActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SongContentView() {
        TextView svNoContentNotify = findViewById(R.id.scroll_view_notify);
        scrollViewContainer.removeAllViews(); // Xóa các view cũ nếu có
        if (albumTrackLists == null || albumTrackLists.isEmpty()) {
            svNoContentNotify.setText(R.string.favorite_empty_list);
            svNoContentNotify.setVisibility(View.VISIBLE);
        } else {
            svNoContentNotify.setVisibility(View.GONE);
            for (AlbumTrackList track : albumTrackLists) {
                SongContentWidget songContentWidget = new SongContentWidget(this);
                songContentWidget.setSongName(track.getCurrentSongName());
                songContentWidget.setSongArtist(track.getCurrentSongArtist());
                songContentWidget.setSongUrl(track.getCurrentSongUrl());
                // Khi nhấn vào bài trên list
                songContentWidget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playFunction = 2;
                        songPlayerWidget.setSongPlayerView(
                                songContentWidget.getSongName(),
                                songContentWidget.getSongArtist(),
                                playFunction,
                                loopFunction
                        );
                        playMusic(songContentWidget.getSongUrl());
                        // Cập nhật bài hiện tại
                        currentSongUrl = songContentWidget.getSongUrl();
                    }
                });
                scrollViewContainer.addView(songContentWidget);
            }
        }
    }

    private void Logic() {
        playButton = songPlayerWidget.getRootView().findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongUrl != null) {
                    if (mediaPlayer.isPlaying() && playFunction == 2) {      // Từ Play sang Pause
                        playFunction = 1;
                        pauseMusic();
                        songPlayerWidget.setPlayButtonState(playFunction);
                    } else if (!mediaPlayer.isPlaying() && playFunction == 1) {    // Từ Pause sang Play
                        playFunction = 2;
                        continueMusic();
                        songPlayerWidget.setPlayButtonState(playFunction);
                    }
                }
            }
        });

        playPreviousButton = songPlayerWidget.getRootView().findViewById(R.id.play_back_button);
        playPreviousButton.setOnClickListener(v -> {
            AlbumTrackList previousTrack = getPreviousTrack();
            if (previousTrack != null) {
                playFunction = 2;
                currentSongUrl = previousTrack.getCurrentSongUrl();
                playMusic(currentSongUrl);
                songPlayerWidget.setSongPlayerView(
                        previousTrack.getCurrentSongName(),
                        previousTrack.getCurrentSongArtist(),
                        playFunction,
                        loopFunction
                );
            } else {
                Log.d("ListMusicActivity", "No previous track available");
            }
        });

        playNextButton = songPlayerWidget.getRootView().findViewById(R.id.play_next_button);
        playNextButton.setOnClickListener(v -> {
            AlbumTrackList nextTrack = getNextTrack();
            if (nextTrack != null) {
                playFunction = 2;
                currentSongUrl = nextTrack.getCurrentSongUrl();
                playMusic(currentSongUrl);
                songPlayerWidget.setSongPlayerView(
                        nextTrack.getCurrentSongName(),
                        nextTrack.getCurrentSongArtist(),
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

    private AlbumTrackList getPreviousTrack() {
        for (int i = 0; i < albumTrackLists.size(); i++) {
            if (albumTrackLists.get(i).getCurrentSongUrl().equals(currentSongUrl) && i > 0) {
                Log.d("ListMusicActivity", "Previous Track: " + albumTrackLists.get(i - 1).getCurrentSongName());
                return albumTrackLists.get(i - 1);
            }
        }
        Log.d("ListMusicActivity", "No previous track found for: " + currentSongUrl);
        return null;
    }

    private AlbumTrackList getNextTrack() {
        for (int i = 0; i < albumTrackLists.size(); i++) {
            if (albumTrackLists.get(i).getCurrentSongUrl().equals(currentSongUrl) && i < albumTrackLists.size() - 1) {
                Log.d("ListMusicActivity", "Next Track: " + albumTrackLists.get(i + 1).getCurrentSongName());
                return albumTrackLists.get(i + 1);
            }
        }
        Log.d("ListMusicActivity", "No next track found for: " + currentSongUrl);
        return null;
    }

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
                            stopMusic(); // Dừng nếu không lặp lại
                            break;
                        case 2: // Repeat
                            playMusic(currentSongUrl); // Phát lại bài hiện tại
                            break;
                        case 3: // Shuffle
                            AlbumTrackList randomTrack = getRandomItem(albumTrackLists);
                            if (randomTrack != null) {
                                playMusic(randomTrack.getCurrentSongUrl());
                                songPlayerWidget.setSongPlayerView(
                                        randomTrack.getCurrentSongName(),
                                        randomTrack.getCurrentSongArtist(),
                                        playFunction,
                                        loopFunction
                                );
                            }
                            break;
                    }
                });
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
