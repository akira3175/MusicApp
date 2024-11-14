package com.example.magicmusic.GUI;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongContentWidget;
import com.example.magicmusic.adapters.SongPlayerWidget;
import com.example.magicmusic.adapters.TrackAdapter;
import com.example.magicmusic.controllers.MusicController;
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
    private ProgressBar progressBar;
    private List<Track> tracks = new ArrayList<>();

    private TextView songTitle;
//    private ImageButton playButton, pauseButton, stopButton;
    private int currentTrackIndex = -1;
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
    private MusicController musicController;


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
                currentTrackIndex = index;
                musicController.playTrack(track);

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

        musicController = new MusicController(this);
        songPlayerWidget = new SongPlayerWidget(ListMusicActivity.this);
        listContent = findViewById(R.id.song_player_widget_container);
        listContent.addView(songPlayerWidget);
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


        progressBar = findViewById(R.id.progressBar);

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
                finish();
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
            AlbumTrackList previousTrack = getPreviousTrack();
            if (previousTrack != null) {
                playFunction = 2;
                currentTrackIndex = getPreviousTrackIndex();
                musicController.playTrack(tracks.get(currentTrackIndex));
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
                currentTrackIndex = getNextTrackIndex();
                musicController.playTrack(tracks.get(currentTrackIndex));
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
        int previousIndex = getPreviousTrackIndex();
        return albumTrackLists.get(previousIndex);
    }

    private AlbumTrackList getNextTrack() {
        int nextIndex = getNextTrackIndex();
        return albumTrackLists.get(nextIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public int getNextTrackIndex() {
        if(currentTrackIndex < albumTrackLists.size()) {
            return currentTrackIndex + 1;
        } else {
            return 0;
        }
    };

    public int getPreviousTrackIndex() {
        if (currentTrackIndex > 0) {
            return currentTrackIndex - 1;
        } else {
            return albumTrackLists.size() - 1;
        }
    }
}
