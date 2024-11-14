package com.example.magicmusic.GUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongAdapter;
import com.example.magicmusic.adapters.SongContentWidget;
import com.example.magicmusic.adapters.SongPlayerWidget;
import com.example.magicmusic.adapters.TrackAdapter;
import com.example.magicmusic.models.AlbumTrackList;
import com.example.magicmusic.models.Playlist;
import com.example.magicmusic.models.Track;
import com.example.magicmusic.models.PlaylistResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListMusicActivity extends AppCompatActivity {
    public static final String CLIENT_ID = "ec0e93fa";
    //for lazy loading
    private int currentPage = 1;  // Trang hiện tại
    private final int PAGE_SIZE = 10; // Số lượng bản nhạc mỗi trang
    private boolean isLoading = false; // Đánh dấu khi đang tải dữ liệu
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    private List<Track> tracks = new ArrayList<>();

    private TextView songTitle;
//    private ImageButton playButton, pauseButton, stopButton;
    private String currentSongUrl;
    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_music);

        trackList = findViewById(R.id.song_list);
        trackList.setLayoutManager(new LinearLayoutManager(this));
        trackAdapter = new TrackAdapter(tracks, this, new TrackAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Track track, int index) {
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
        setupScrollListener();

        songPlayerWidget = new SongPlayerWidget(ListMusicActivity.this);
        listContent = findViewById(R.id.song_player_widget_container);
        listContent.addView(songPlayerWidget);

        progressBar = findViewById(R.id.progressBar);
        showLoadingScreen();
        SongContentView();

        Header();
        Logic();
        fetchAndInflateTracksDelay();
    }

    private void Header() {
        //Nút Back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListMusicActivity.this, MainActivity.class);
                startActivity(intent);
//                finish();
            }
        });
    }

    //test lazy loading
    public void fetchAndInflateTracksDelay() {
        if (isLoading) return; // Kiểm tra xem có đang tải không để tránh gọi nhiều lần
        isLoading = true; // Đánh dấu trạng thái là đang tải

        showLoadingScreen(); // Hiển thị loading screen

        Intent intent = getIntent();
        int playlistId = intent.getIntExtra("playlistId", 500089797);

        int offset = (currentPage - 1) * PAGE_SIZE;
        JamendoApi apiService = ApiClient.getClient().create(JamendoApi.class);
        Call<PlaylistResponse> call = apiService.getPlaylistTracks("json", playlistId + "", PAGE_SIZE - 1, offset);

        call.enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                // Tạo độ trễ 1 giây trước khi xử lý kết quả
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    isLoading = false; // Hoàn thành tải
                    if (response.isSuccessful() && response.body() != null) {
                        List<Playlist> playlists = response.body().getResults();
                        List<Track> newTracks = new ArrayList<>();
                        if (!playlists.isEmpty()) {
                            newTracks.addAll(playlists.get(0).getTracks());
                        }
                        // Lấy danh sách các track từ playlist
                        if (!playlists.isEmpty()) {
                            tracks.addAll(playlists.get(0).getTracks());

                            // Lưu các Track vào albumTrackLists
                            for (Track track : newTracks) {
                                albumTrackLists.add(new AlbumTrackList(track.getAudio(), track.getName(), track.getArtist_name(), track.getImage(), true, false));
                                // In ra URL hình ảnh để kiểm tra
                                Log.d("Track Image URL", "Image URL: " + track.getImage());
                            }
                            trackAdapter.notifyDataSetChanged();
                        }

                        Log.d("Fetched: ", response.body().toString());
                    } else {
                        Log.e("Jamendo", "No tracks found or response failed.");
                    }

                    hideLoadingScreen(); // Ẩn loading screen sau khi hết delay
                }, 1000); // Độ trễ 1 giây
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                // Ẩn loading screen ngay lập tức khi có lỗi
                isLoading = false;
                hideLoadingScreen();
                Log.e("Jamendo", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void setupScrollListener() {
        trackList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) trackList.getLayoutManager();
                if (layoutManager != null && !isLoading && layoutManager.findLastVisibleItemPosition() == tracks.size() - 1) {
                    // Đã cuộn đến cuối danh sách
                    currentPage++;
                    fetchAndInflateTracksDelay(); // Tải thêm dữ liệu
                }
            }
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
