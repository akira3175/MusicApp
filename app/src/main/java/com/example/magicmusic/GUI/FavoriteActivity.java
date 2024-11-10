package com.example.magicmusic.GUI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.magicmusic.Database.FavoriteTrackDAO;
import com.example.magicmusic.Database.FavoriteTrackDTO;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.SongContentWidget;
import com.example.magicmusic.adapters.SongPlayerWidget;
import com.example.magicmusic.adapters.DownloadAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    //Database Objects
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private FavoriteTrackDAO favoriteTrackDAO;
    private List<FavoriteTrackDTO> favoriteTrackLists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        // lấy danh sách bài hát yêu thích
        selectAll();

        scrollViewContainer = findViewById(R.id.scroll_view_container);
        songPlayerWidget = new SongPlayerWidget(FavoriteActivity.this);
        listContent = findViewById(R.id.song_player_widget_container);
        listContent.addView(songPlayerWidget);

        Header();
        Navigation();
        SongContentView();
        Logic();
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
            }
        }
    }

    // Override để xử lý kết quả khi người dùng cấp quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Quyền đã được cấp", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cấp quyền không thành công", Toast.LENGTH_SHORT).show();        }
    }

    private void Header() {
        //Nút Back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Navigation() {
        //Nút Play chung
        MaterialButton playMaterialButton = findViewById(R.id.play_material_button);
        playMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chọn bài đầu để phát
                playFunction = 2;
                playMusic(favoriteTrackLists.get(0).getSongUrl());
                songPlayerWidget.setSongPlayerView(
                        favoriteTrackLists.get(0).getSongName(),
                        favoriteTrackLists.get(0).getSongArtist(),
                        playFunction,
                        loopFunction
                );
            }
        });
    }

    private void SongContentView() {
        TextView svNoContentNotify = findViewById(R.id.scroll_view_notify);
        scrollViewContainer.removeAllViews(); // Xóa các view cũ nếu có
        if (favoriteTrackLists == null || favoriteTrackLists.isEmpty()) {
            svNoContentNotify.setText(R.string.favorite_empty_list);
            svNoContentNotify.setVisibility(View.VISIBLE);
        } else {
            svNoContentNotify.setVisibility(View.GONE);
            for (FavoriteTrackDTO track : favoriteTrackLists) {
                if (!track.getIsFavorite()) {
                    continue;
                }
                SongContentWidget songContentWidget = new SongContentWidget(this);
                songContentWidget.setSongName(track.getSongName());
                songContentWidget.setSongArtist(track.getSongArtist());
                songContentWidget.setSongUrl(track.getSongUrl());
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(track.getSongUrl());
                    mediaPlayer.prepareAsync();
                    songContentWidget.setSongDuration(mediaPlayer.getDuration());
                } catch (Exception e) {
                    Log.e("Jamendo", "Thiếu URL bài hát");
                }
                // Khi nhấn vào 1 bài hát trên list
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
                // Khi nhấn vào nút Favorite
                songContentWidget.getSongFavoriteButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (favoriteFunction == 0) {
                            favoriteFunction = 1;
                            songContentWidget.setSongFavorite(favoriteFunction);
                        } else {
                            favoriteFunction = 0;
                            for (FavoriteTrackDTO track : favoriteTrackLists) {
                                if (track.getSongUrl().equals(songContentWidget.getSongUrl())) {
                                    track.setIsFavorite(false);
                                    update(track);
                                    break;
                                }
                            }
                            songContentWidget.setSongFavorite(favoriteFunction);
                        }
                    }
                });
                // Khi nhấn vào nút Download
                songContentWidget.getSongDownloadedButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String songName = songContentWidget.getSongName() + '_' + songContentWidget.getSongArtist() + ".mp3";
                        if (!downloadAdapter.checkFileInDownloads(FavoriteActivity.this, songName)) {
                            songContentWidget.setSongDownloaded(false);
                        } else {
                            songContentWidget.setSongDownloaded(true);
                            downloadAdapter.downloadAndRenameSong(FavoriteActivity.this, songContentWidget.getSongUrl(), songName);
                        }
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
            FavoriteTrackDTO previousTrack = getPreviousTrack();
            if (previousTrack != null) {
                playFunction = 2;
                currentSongUrl = previousTrack.getSongUrl();
                playMusic(currentSongUrl);
                songPlayerWidget.setSongPlayerView(
                        previousTrack.getSongName(),
                        previousTrack.getSongArtist(),
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
        for (int i = 0; i < favoriteTrackLists.size(); i++) {
            if (favoriteTrackLists.get(i).getSongUrl().equals(currentSongUrl) && i > 0) {
                Log.d("FavoriteActivity", "Previous Track: " + favoriteTrackLists.get(i - 1).getSongName());
                return favoriteTrackLists.get(i - 1);
            }
        }
        Log.d("FavoriteActivity", "No previous track found for: " + currentSongUrl);
        return null;
    }

    private FavoriteTrackDTO getNextTrack() {
        for (int i = 0; i < favoriteTrackLists.size(); i++) {
            if (favoriteTrackLists.get(i).getSongUrl().equals(currentSongUrl) && i < favoriteTrackLists.size() - 1) {
                Log.d("FavoriteActivity", "Next Track: " + favoriteTrackLists.get(i + 1).getSongName());
                return favoriteTrackLists.get(i + 1);
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
                            FavoriteTrackDTO randomTrack = getRandomItem(favoriteTrackLists);
                            if (randomTrack != null) {
                                playMusic(randomTrack.getSongUrl());
                                songPlayerWidget.setSongPlayerView(
                                        randomTrack.getSongName(),
                                        randomTrack.getSongArtist(),
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

    private void downloadMusic(String songUrl, String fileName) {
        DownloadAdapter downloadAdapter = new DownloadAdapter();
        downloadAdapter.downloadAndRenameSong(this, songUrl, fileName + ".mp3");
    }

    // Thao tác với database
    public void selectAll() {
        try {
            executorService.execute(() -> {
                favoriteTrackLists = favoriteTrackDAO.getAllFavoriteTracks();
            });
        } catch (Exception e) {
            Log.e("Database", "Có lỗi thực thi truy vấn");
        }
    }

    public void insertAll(List<FavoriteTrackDTO> favoriteTracks) {
        executorService.execute(() -> {
            favoriteTrackDAO.insertAllFavoriteTrack(favoriteTracks);
        });
    }

    public void update(FavoriteTrackDTO favoriteTrack) {
        try {
            executorService.execute(() -> {
                favoriteTrackDAO.updateFavoriteTrack(favoriteTrack);
            });
        } catch (SQLiteConstraintException e) {
            Log.e("Database", "Có lỗi trong ràng buộc dữ liệu");
        } catch (IllegalArgumentException e) {
            Log.e("Database", "Các tham số truyền không hợp lệ");
        }
    }

    public void delete(FavoriteTrackDTO favoriteTrack) {
        executorService.execute(() -> {
            favoriteTrackDAO.deleteFavoriteTrack(favoriteTrack);
        });
    }
}