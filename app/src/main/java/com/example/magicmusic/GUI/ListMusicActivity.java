package com.example.magicmusic.GUI;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magicmusic.API.AlbumApi;
import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongAdapter;
import com.example.magicmusic.adapters.SongContentWidget;
import com.example.magicmusic.adapters.SongPlayerWidget;
import com.example.magicmusic.adapters.TrackAdapter;
import com.example.magicmusic.models.AlbumTrackList;
import com.example.magicmusic.models.Playlist;
import com.example.magicmusic.models.Track;
import com.example.magicmusic.models.TrackResponse;


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
//    private ImageButton playButton, pauseButton, stopButton;
    private String currentSongUrl;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
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
    RecyclerView trackList;
//    private String currentSongUrl;
//    private List<JamendoResponse.Track> trackList;  // Use Track from TracksFromPlaylistResponse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_music);

        trackList = findViewById(R.id.song_list);
        trackList.setLayoutManager(new LinearLayoutManager(this));


//        AlbumApi apiService = ApiClient.getClient().create(AlbumApi.class);
//        Call<TrackResponse> call = apiService.getTracks(CLIENT_ID, "json", 10, playlistId);
//        Log.d("call: ", call.request().url().toString());

//        call.enqueue(new Callback<JamendoResponse>() {
//            @Override
//            public void onResponse(Call<JamendoResponse> call, Response<JamendoResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    trackList = response.body().getResults();
//                    for (Track track : trackList) {
//                        Log.d("Jamendo", "Track ID: " + track.getId());
//                        Log.d("Jamendo", "Track Name: " + track.getName());
//                        Log.d("Jamendo", "Artist: " + track.getArtist_name());
//                        Log.d("Jamendo", "Preview URL: " + track.getAudio()); // Kiểm tra URL preview
//                        albumTrackLists.add(new AlbumTrackList(track.getAudio(), track.getName(), track.getArtist_name(), null, true, false));
//                        Log.d("Jamendo", "Preview URL: " + track.getAudio());  // Check preview URL
//                    }
//                    // Cập nhật giao diện người dùng
//                    songAdapter = new SongAdapter(ListMusicActivity.this, trackList);
////                    recyclerView.setAdapter(songAdapter);
//                    SongContentView();
//
//                    // Thiết lập sự kiện khi bài hát được chọn
//                    songAdapter.setOnItemClickListener((track) -> {
//                        setCurrentSong(track.getAudio(), track.getName());
//                    });
//
//                    Log.d("Jamendo", response.body().toString());
//                } else {
//                    Log.e("Jamendo", "No tracks found or response failed.");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JamendoResponse> call, Throwable t) {
//                Log.e("Jamendo", "Error: " + t.getMessage());
//            }
//        });



//        scrollViewContainer = findViewById(R.id.scroll_view_container);
        songPlayerWidget = new SongPlayerWidget(ListMusicActivity.this);
        listContent = findViewById(R.id.song_player_widget_container);
        listContent.addView(songPlayerWidget);

        Header();
//        SongContentView();
        Logic();
        fetchAndInflateTracks();
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
                    albumTrackLists.clear(); // Xóa danh sách cũ nếu có

                    // Lấy danh sách các track từ playlist
                    if (!playlists.isEmpty()) {
                        tracks.addAll(playlists.get(0).getTracks());

                        // Lưu các Track vào albumTrackLists
                        for (Track track : tracks) {
                            albumTrackLists.add(new AlbumTrackList(track.getAudio(), track.getName(), track.getArtist_name(), track.getImage(), true, false));
                            // In ra URL hình ảnh để kiểm tra
                            Log.d("Track Image URL", "Image URL: " + track.getImage());
                        }
                    }

                    Log.d("tracks size", "Track List Size: " + tracks.size());
                    TrackAdapter trackAdapter = new TrackAdapter(tracks, ListMusicActivity.this, new TrackAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Track track) {
//                            Toast.makeText(ListMusicActivity.this, track.getName(), Toast.LENGTH_SHORT).show();
                            // Khi một bài hát được nhấn, phát nhạc trực tiếp
                            currentSongUrl = track.getAudio();
                            playMusic(currentSongUrl);

                            // Cập nhật thông tin bài hát đang phát trên SongPlayerWidget
                            playFunction = 2; // Đặt trạng thái phát
                            songPlayerWidget.setSongPlayerView(
                                    track.getName(),
                                    track.getArtist_name(),
                                    track.getImage(),
                                    playFunction,
                                    loopFunction
                            );
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

    private void SongContentView() {
//        TextView svNoContentNotify = findViewById(R.id.scroll_view_notify);
//        scrollViewContainer.removeAllViews(); // Xóa các view cũ nếu có
        if (albumTrackLists == null || albumTrackLists.isEmpty()) {
//            svNoContentNotify.setText(R.string.favorite_empty_list);
//            svNoContentNotify.setVisibility(View.VISIBLE);
        } else {
//            svNoContentNotify.setVisibility(View.GONE);
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
                        previousTrack.getCurrentSongImage(),
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
                        nextTrack.getCurrentSongImage(),
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
                            // Nếu không lặp lại, phát bài tiếp theo
                            AlbumTrackList nextTrack = getNextTrack();
                            if (nextTrack != null) {
                                playMusic(nextTrack.getCurrentSongUrl());
                                songPlayerWidget.setSongPlayerView(
                                        nextTrack.getCurrentSongName(),
                                        nextTrack.getCurrentSongArtist(),
                                        nextTrack.getCurrentSongImage(),
                                        playFunction,
                                        loopFunction
                                );
                            }
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
                                        randomTrack.getCurrentSongImage(),
                                        playFunction,
                                        loopFunction
                                );
                            }
                            break;
                    }
                });
//                mediaPlayer.prepareAsync();  // Prepare the song
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

    public void setCurrentSong(String url, String name) {
        currentSongUrl = url;
        songTitle.setText(name);  // Update song title
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
