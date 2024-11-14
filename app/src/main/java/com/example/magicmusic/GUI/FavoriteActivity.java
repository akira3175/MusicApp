package com.example.magicmusic.GUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.magicmusic.Database.DatabaseInstance;
import com.example.magicmusic.Database.FavoriteTrackDTO;
import com.example.magicmusic.Database.FavoriteTrackDatabase;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongContentWidget;
import com.example.magicmusic.adapters.SongPlayerWidget;
import com.example.magicmusic.adapters.DownloadAdapter;
import com.example.magicmusic.adapters.TrackAdapter;
import com.example.magicmusic.controllers.MusicController;
import com.example.magicmusic.models.Track;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class FavoriteActivity extends AppCompatActivity {
    //XML Components
    RelativeLayout listContent;
    ImageButton playButton;
    ImageButton playPreviousButton;
    ImageButton playNextButton;
    ImageButton loopButton;
    private SongPlayerWidget songPlayerWidget;
    private LinearLayout scrollViewContainer;
    //Class Objects
    private final DownloadAdapter downloadAdapter = new DownloadAdapter();
    private MediaPlayer mediaPlayer;
    private int playFunction = 0;
    private int loopFunction = 1;
    private int favoriteFunction = 0;
    private String currentSongUrl;
    List<FavoriteTrackDTO> downloadedSongs;
    //Database Objects
    TrackAdapter lma = new TrackAdapter();
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<FavoriteTrackDTO> favoriteTrackLists;
    private MusicController musicController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        scrollViewContainer = findViewById(R.id.scroll_view_container);
        songPlayerWidget = new SongPlayerWidget(FavoriteActivity.this);
        listContent = findViewById(R.id.song_player_widget_container);
        listContent.addView(songPlayerWidget);

        musicController = new MusicController(this);
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

        FavoriteTrackDatabase db = DatabaseInstance.getDatabase(FavoriteActivity.this);
        selectAll(db);
        requestAudioPermission();
        Header();
        Navigation();
        Logic();
        songPlayerWidget.setVisibility(View.GONE);
    }

    private void requestAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 trở lên
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    // Override để xử lý kết quả khi người dùng cấp quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Quyền đã được cấp", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cấp quyền không thành công", Toast.LENGTH_SHORT).show();        }
    }

    private void Header() {
        // Nút Back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                stopMusic();
                startActivity(intent);
                finish(); // Kết thúc FavoriteActivity
            }
        });
    }

    private void Navigation() {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch downloadSwitch = findViewById(R.id.favorite_switch);
        //Nút Play chung
        MaterialButton playMaterialButton = findViewById(R.id.play_material_button);
        playMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadSwitch.isChecked()) {
                    // Khi Switch ở chế độ Download
                    if (downloadedSongs != null && !downloadedSongs.isEmpty()) {
                        playFunction = 2;
                        currentSongUrl = downloadedSongs.get(0).getSongUrl(); // Cập nhật bài đầu tiên của downloadedSongs
                        playMusic(currentSongUrl);
                        songPlayerWidget.setSongPlayerView(
                                downloadedSongs.get(0).getSongName(),
                                downloadedSongs.get(0).getSongArtist(),
                                null,
                                playFunction,
                                loopFunction
                        );
                        songPlayerWidget.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("FavoriteActivity", "No downloaded songs available");
                    }
                } else {
                    // Khi Switch ở chế độ yêu thích
                    FavoriteTrackDatabase db = DatabaseInstance.getDatabase(FavoriteActivity.this);
                    selectAll(db);
                    if (favoriteTrackLists != null && !favoriteTrackLists.isEmpty()) {
                        playFunction = 2;
                        currentSongUrl = favoriteTrackLists.get(0).getSongUrl(); // Cập nhật bài đầu tiên của favoriteTrackLists
                        playMusic(currentSongUrl);
                        songPlayerWidget.setSongPlayerView(
                                favoriteTrackLists.get(0).getSongName(),
                                favoriteTrackLists.get(0).getSongArtist(),
                                favoriteTrackLists.get(0).getSongImageUrl(),
                                playFunction,
                                loopFunction
                        );
                        songPlayerWidget.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("FavoriteActivity", "No favorite tracks available");
                    }
                }
            }
        });
        //Nút Switch
        downloadSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Cập nhật lại danh sách bài hát tải xuống
                List<FavoriteTrackDTO> downloadedSongsList = downloadAdapter.findSongsInDownloads(FavoriteActivity.this);
                Log.d("FavoriteActivity", "Downloaded songs found in Downloads folder: " + (downloadedSongsList != null ? downloadedSongsList.size() : "null"));
                downloadedSongs = downloadAdapter.downloadedSongsStringSpliterator(downloadedSongsList);
                Log.d("FavoriteActivity", "Splitted downloaded songs: " + (downloadedSongs != null ? downloadedSongs.size() : "null"));

                TextView svNoContentNotify = findViewById(R.id.scroll_view_notify);
                scrollViewContainer.removeAllViews(); // Xóa các view cũ nếu có
                if (downloadedSongs == null || downloadedSongs.isEmpty()) {
                    svNoContentNotify.setText(R.string.favorite_empty_list);
                    svNoContentNotify.setVisibility(View.VISIBLE);
                } else {
                    svNoContentNotify.setVisibility(View.GONE);
                    for (FavoriteTrackDTO track : downloadedSongs) {
                        SongContentWidget songContentWidget = new SongContentWidget(FavoriteActivity.this);
                        songContentWidget.setSongName(track.getSongName());
                        songContentWidget.setSongArtist(track.getSongArtist());
                        songContentWidget.setSongUrl(track.getSongUrl());
                        songContentWidget.setTextFavorite("Downloaded /");
                        songContentWidget.setSongImageUrl(null);
                        songContentWidget.getSongFavoriteButton().setVisibility(View.GONE);
                        songContentWidget.getSongDownloadedButton().setVisibility(View.GONE);
                        MediaPlayer media = new MediaPlayer();
                        try {
                            Uri songUri = Uri.parse(track.getSongUrl());
                            media.setDataSource(FavoriteActivity.this, songUri);
                            // Đặt OnPreparedListener để gọi getDuration() sau khi MediaPlayer đã chuẩn bị xong
                            media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    // Khi MediaPlayer đã sẵn sàng, lấy thời gian của bài hát
                                    int duration = media.getDuration();
                                    songContentWidget.setSongDuration(duration);
                                }
                            });
                            // Sử dụng prepareAsync() để chuẩn bị MediaPlayer bất đồng bộ
                            media.prepareAsync();
                        } catch (Exception e) {
                            Log.e("FavoriteActivity", "Thiếu đường dẫn bài hát");
                        }
                        // Xử lý sự kiện nhấn widget -> chạy nhạc
                        songContentWidget.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stopMusic();
                                playFunction = 2;
                                songPlayerWidget.setSongPlayerView(
                                        songContentWidget.getSongName(),
                                        songContentWidget.getSongArtist(),
                                        playFunction,
                                        loopFunction
                                );
                                playMusic(track.getSongUrl());
                                // Cập nhật bài hiện tại
                                currentSongUrl = track.getSongUrl();
                                songPlayerWidget.setVisibility(View.VISIBLE);
                            }
                        });
                        scrollViewContainer.addView(songContentWidget);
                    }
                }
            } else {
                SongContentView();
            }
        });
    }

    private void SongContentView() {
        FavoriteTrackDatabase db = DatabaseInstance.getDatabase(FavoriteActivity.this);
        TextView svNoContentNotify = findViewById(R.id.scroll_view_notify);

        executorService.execute(() -> {
            try {
                // Lấy dữ liệu từ database
                List<FavoriteTrackDTO> tracks = db.favoriteTrackDao().getAllFavoriteTracks();
                favoriteTrackLists = tracks;

                // Cập nhật UI trên main thread
                runOnUiThread(() -> {
                    scrollViewContainer.removeAllViews();

                    if (tracks == null || tracks.isEmpty()) {
                        svNoContentNotify.setText(R.string.favorite_empty_list);
                        svNoContentNotify.setVisibility(View.VISIBLE);
                    } else {
                        svNoContentNotify.setVisibility(View.GONE);
                        for (FavoriteTrackDTO track : tracks) {
                            if (!track.getIsFavorite()) continue;

                            SongContentWidget songContentWidget = new SongContentWidget(FavoriteActivity.this);
                            setupSongContentWidget(songContentWidget, track, db);
                            scrollViewContainer.addView(songContentWidget);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("Database", "Error loading favorite tracks: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(FavoriteActivity.this,
                            "Error loading favorites", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupSongContentWidget(SongContentWidget songContentWidget, FavoriteTrackDTO track, FavoriteTrackDatabase db) {
        songContentWidget.setSongName(track.getSongName());
        songContentWidget.setSongArtist(track.getSongArtist());
        songContentWidget.setSongImageUrl(track.getSongImageUrl());
        songContentWidget.setSongUrl(track.getSongUrl());

        // Tạo MediaPlayer và lấy thời gian bài hát
        setupMediaPlayerForSong(songContentWidget, track);

        // Xử lý sự kiện nhấn widget -> chạy nhạc
        songContentWidget.setOnClickListener(v -> playSong(songContentWidget));

        // Xử lý sự kiện nhấn nút Favorite
        songContentWidget.getSongFavoriteButton().setOnClickListener(v -> toggleFavorite(track, songContentWidget, db));

        // Xử lý sự kiện nhấn nút Download
        songContentWidget.getSongDownloadedButton().setOnClickListener(v -> handleDownload(songContentWidget));
    }

    private void setupMediaPlayerForSong(SongContentWidget songContentWidget, FavoriteTrackDTO track) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            Uri songUri = Uri.parse(track.getSongUrl());
            mediaPlayer.setDataSource(FavoriteActivity.this, songUri);
            mediaPlayer.setOnPreparedListener(mp -> {
                int duration = mp.getDuration();
                songContentWidget.setSongDuration(duration); // Cập nhật vào widget giao diện
            });

            // Chuẩn bị MediaPlayer bất đồng bộ
            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            Log.e("FavoriteActivity", "Error setting up MediaPlayer for song: " + track.getSongName(), e);
        }

        // Listener khi hoàn thành để giải phóng tài nguyên
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            Log.d("FavoriteActivity", "MediaPlayer resources released after song completion");
        });
    }

    private void playSong(SongContentWidget songContentWidget) {
        playFunction = 2;
        songPlayerWidget.setSongPlayerView(
                songContentWidget.getSongName(),
                songContentWidget.getSongArtist(),
                songContentWidget.getSongImageUrl(),
                playFunction,
                loopFunction
        );
        playMusic(songContentWidget.getSongUrl());
        currentSongUrl = songContentWidget.getSongUrl();
        songContentWidget.setVisibility(View.VISIBLE);
        songPlayerWidget.setVisibility(View.VISIBLE);
    }

    private void toggleFavorite(FavoriteTrackDTO track, SongContentWidget songContentWidget, FavoriteTrackDatabase db) {
        if (favoriteFunction == 0) {
            favoriteFunction = 1;
            songContentWidget.setSongFavorite(favoriteFunction);
        } else {
            favoriteFunction = 0;
            track.setIsFavorite(false);
            update(db, track);
            songContentWidget.setSongFavorite(favoriteFunction);
        }
    }

    private void handleDownload(SongContentWidget songContentWidget) {
        String songName = songContentWidget.getSongName() + " - " + songContentWidget.getSongArtist() + ".mp3";
        Toast.makeText(FavoriteActivity.this, "Đang tải bài hát: " + songName + " ...", Toast.LENGTH_SHORT).show();
        if (!downloadAdapter.checkFileInDownloads(FavoriteActivity.this, songName)) {
            songContentWidget.setSongDownloaded(true);
            Log.d("Download", "Download song: " + songContentWidget.getSongUrl());
            executorService.execute(() -> {
                downloadAdapter.downloadAndRenameSong(FavoriteActivity.this, songContentWidget.getSongUrl(), songName);
            });
        } else {
            songContentWidget.setSongDownloaded(false);
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
            FavoriteTrackDTO previousTrack = getPreviousTrack();
            if (previousTrack != null) {
                playFunction = 2;
                currentSongUrl = previousTrack.getSongUrl();
                playMusic(currentSongUrl);
                songPlayerWidget.setSongPlayerView(
                        previousTrack.getSongName(),
                        previousTrack.getSongArtist(),
                        previousTrack.getSongImageUrl(),
                        playFunction,
                        loopFunction
                );
            } else {
                Log.d("FavoriteActivity", "No previous track available");
            }
        });

        playNextButton = songPlayerWidget.getRootView().findViewById(R.id.play_next_button);
        playNextButton.setOnClickListener(v -> {
            FavoriteTrackDTO nextTrack = getNextTrack();
            if (nextTrack != null) {
                playFunction = 2;
                currentSongUrl = nextTrack.getSongUrl();
                playMusic(currentSongUrl);
                songPlayerWidget.setSongPlayerView(
                        nextTrack.getSongName(),
                        nextTrack.getSongArtist(),
                        nextTrack.getSongImageUrl(),
                        playFunction,
                        loopFunction
                );
            } else {
                Log.d("FavoriteActivity", "No next track available");
            }
        });

        loopButton = songPlayerWidget.getRootView().findViewById(R.id.play_mode_button);
        loopButton.setOnClickListener(v -> {
            loopFunction = (loopFunction % 3) + 1;  // Chuyển vòng từ NoRepeat (1) -> Repeat (2) -> Shuffle (3)
            songPlayerWidget.setLoopButtonState(loopFunction);
            Log.d("FavoriteActivity", "Loop mode changed to: " + loopFunction);
        });
    }

    private static <T> T getRandomItem(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    private FavoriteTrackDTO getPreviousTrack() {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch downloadMaterialButton = findViewById(R.id.favorite_switch);
        if (!downloadMaterialButton.isChecked()) {
            for (int i = 0; i < favoriteTrackLists.size(); i++) {
                if (favoriteTrackLists.get(i).getSongUrl().equals(currentSongUrl) && i > 0) {
                    Log.d("FavoriteActivity", "Previous Track: " + favoriteTrackLists.get(i - 1).getSongName());
                    return favoriteTrackLists.get(i - 1);
                }
            }
        } else {
            for (int i = 0; i < downloadedSongs.size(); i++) {
                if (downloadedSongs.get(i).getSongUrl().equals(currentSongUrl) && i > 0) {
                    Log.d("FavoriteActivity", "Previous Track: " + downloadedSongs.get(i - 1).getSongName());
                    return downloadedSongs.get(i - 1);
                }
            }
        }
        Log.d("FavoriteActivity", "No previous track found for: " + currentSongUrl);
        return null;
    }

    private FavoriteTrackDTO getNextTrack() {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch downloadMaterialButton = findViewById(R.id.favorite_switch);

        if (!downloadMaterialButton.isChecked()) {
            for (int i = 0; i < favoriteTrackLists.size(); i++) {
                if (favoriteTrackLists.get(i).getSongUrl().equals(currentSongUrl) && i < favoriteTrackLists.size() - 1) {
                    Log.d("FavoriteActivity", "Next Track: " + favoriteTrackLists.get(i + 1).getSongName());
                    return favoriteTrackLists.get(i + 1);
                }
            }
        } else {
            for (int i = 0; i < downloadedSongs.size(); i++) {
                if (downloadedSongs.get(i).getSongUrl().equals(currentSongUrl) && i < downloadedSongs.size() - 1) {
                    Log.d("FavoriteActivity", "Next Track: " + downloadedSongs.get(i + 1).getSongName());
                    return downloadedSongs.get(i + 1);
                }
            }
        }
        Log.d("FavoriteActivity", "No next track found for: " + currentSongUrl);
        return null;
    }

    private void playMusic(String currentSongUrl) {
        if (currentSongUrl != null) {
            if (mediaPlayer != null) {
                mediaPlayer.release(); // Giải phóng nếu đang phát
            }
            mediaPlayer = new MediaPlayer();
            try {
                @SuppressLint("UseSwitchCompatOrMaterialCode")
                Switch downloadMaterialButton = findViewById(R.id.favorite_switch);
                if (!downloadMaterialButton.isChecked()) {
                    mediaPlayer.setDataSource(currentSongUrl);
                } else {
                    Uri songUri = Uri.parse(currentSongUrl);
                    mediaPlayer.setDataSource(this, songUri);
                }
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());

                // Cài đặt lặp lại khi bài hát kết thúc theo chế độ lặp hiện tại
                mediaPlayer.setOnCompletionListener(mp -> {
                    switch (loopFunction) {
                        case 1: // NoRepeat
                            if (getNextTrack().getSongUrl() == null)
                                stopMusic();
                            else
                                playMusic(getNextTrack().getSongUrl());
                            break;
                        case 2: // Repeat
                            playMusic(currentSongUrl);
                            break;
                        case 3: // Shuffle
                            FavoriteTrackDTO randomTrack;
                            if (!downloadMaterialButton.isChecked())
                                randomTrack = getRandomItem(favoriteTrackLists);
                            else
                                randomTrack = getRandomItem(downloadedSongs);

                            if (randomTrack != null) {
                                playMusic(randomTrack.getSongUrl());
                                songPlayerWidget.setSongPlayerView(
                                        randomTrack.getSongName(),
                                        randomTrack.getSongArtist(),
                                        randomTrack.getSongImageUrl(),
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Giải phóng tài nguyên khi không còn cần thiết
            mediaPlayer = null;
        }
    }

    // Thao tác với Database
    public void selectAll(FavoriteTrackDatabase db) {
        Log.d("Database", "selectAllFavorite called");
        if (db != null) {
            executorService.execute(() -> {
                try {
                    List<FavoriteTrackDTO> tracks = db.favoriteTrackDao().getAllFavoriteTracks();
                    favoriteTrackLists = tracks;
                    for (FavoriteTrackDTO track : tracks) {
                        Log.d("Database", "Track ID: " + track.getSongId() +
                                ", Track URL: " + track.getSongUrl() +
                                ", Track Name: " + track.getSongName() +
                                ", Artist: " + track.getSongArtist() +
                                track.getIsFavorite() +
                                track.getSongImageUrl());
                    }
                } catch (Exception e) {
                    Log.e("Database", "Có lỗi thực thi truy vấn: " + e.getMessage());
                }
            });
        } else {
            Log.e("Database", "Database chưa được khởi tạo");
        }
    }

    public void update(FavoriteTrackDatabase db, FavoriteTrackDTO trackNeedUpdating) {
        try {
            Log.d("Database", "updateFavorite called");
            executorService.execute(() -> {
                db.favoriteTrackDao().updateFavoriteTrack(trackNeedUpdating);
                runOnUiThread(this::SongContentView);
            });
        } catch (SQLiteConstraintException e) {
            Log.e("Database", "Có lỗi trong ràng buộc dữ liệu");
        } catch (IllegalArgumentException e) {
            Log.e("Database", "Các tham số truyền không hợp lệ");
        } catch (Exception e) {
            Log.e("Database", "An unexpected error occurred during update", e);
        }
    }
}