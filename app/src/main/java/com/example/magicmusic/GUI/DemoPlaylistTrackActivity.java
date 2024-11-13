package com.example.magicmusic.GUI;

import static com.example.magicmusic.GUI.ListMusicActivity.CLIENT_ID;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.TrackAdapter;
import com.example.magicmusic.models.PlaylistResponse;
import com.example.magicmusic.models.Playlist;
import com.example.magicmusic.models.PlaylistResponse;
import com.example.magicmusic.models.PlaylistResponse;
import com.example.magicmusic.models.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//delay test
import android.os.Handler;
import android.os.Looper;


public class DemoPlaylistTrackActivity extends AppCompatActivity {

  private RecyclerView trackList;
  private TrackAdapter trackAdapter;
  private List<Track> tracks = new ArrayList<>();
  private int currentPage = 1;  // Trang hiện tại
  private final int PAGE_SIZE = 10; // Số lượng bản nhạc mỗi trang
  private boolean isLoading = false; // Đánh dấu khi đang tải dữ liệu
  private MediaPlayer mediaPlayer;
  private ProgressBar progressBar;
  private ImageButton prevButton, playPauseButton, nextButton;
  private TextView currentSongTitle;
  private boolean isPlaying = false; // Trạng thái phát nhạc ban đầu
  private int currentSongIndex = 0; // Chỉ số bài hát hiện tại

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_track_list);

    trackList = findViewById(R.id.song_list);
    trackList.setLayoutManager(new LinearLayoutManager(this));

    progressBar = findViewById(R.id.progressBar);
    showLoadingScreen();
    fetchAndInflateTracksDelay();

    trackAdapter = new TrackAdapter(tracks, DemoPlaylistTrackActivity.this, new TrackAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(Track track, int index) {
        Toast.makeText(DemoPlaylistTrackActivity.this, track.getName(), Toast.LENGTH_SHORT).show();
        playSong(track.getAudio());
        currentSongTitle.setText(track.getName());
        currentSongIndex = index;
      }
    });
    trackList.setAdapter(trackAdapter);

    prevButton = findViewById(R.id.prev_button);
    playPauseButton = findViewById(R.id.play_pause_button);
    nextButton = findViewById(R.id.next_button);
    currentSongTitle = findViewById(R.id.current_song_title);
    mediaPlayer = new MediaPlayer();
    prevButton.setOnClickListener(view -> {
      playPrevSong();
    });
    nextButton.setOnClickListener(view -> {
      playNextSong();
    });
    playPauseButton.setOnClickListener(view -> {
      if (isPlaying) {
        // Tạm dừng nhạc
        playPauseButton.setImageResource(android.R.drawable.ic_media_play); // Đổi thành biểu tượng Play
        mediaPlayer.pause();
      } else {
        // Phát nhạc
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause); // Đổi thành biểu tượng Pause
        mediaPlayer.start();
      }
      isPlaying = !isPlaying; // Đổi trạng thái
    });
    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {
        playNextSong();
      }
    });

    // Thiết lập sự kiện cuộn để tải thêm dữ liệu khi đến cuối danh sách
    setupScrollListener();
  }

  public void fetchAndInflateTracks() {
    if (isLoading) return; // Kiểm tra xem có đang tải không để tránh gọi nhiều lần
    isLoading = true; // Đánh dấu trạng thái là đang tải

    Intent intent = getIntent();
    int playlistId = intent.getIntExtra("playlistId", 500089797);

    int offset = (currentPage - 1) * PAGE_SIZE;
    JamendoApi apiService = ApiClient.getClient().create(JamendoApi.class);
    Call<PlaylistResponse> call = apiService.getPlaylistTracks("json", playlistId + "", PAGE_SIZE-1, offset);

    call.enqueue(new Callback<PlaylistResponse>() {
      @Override
      public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
        isLoading = false; // Hoàn thành tải
        if (response.isSuccessful() && response.body() != null) {
          List<Playlist> playlists = response.body().getResults();
          List<Track> newTracks = new ArrayList<>();
          if (!playlists.isEmpty()) {
            newTracks.addAll(playlists.get(0).getTracks());
          }

          // Thêm dữ liệu mới vào danh sách và cập nhật adapter
          tracks.addAll(newTracks);
          trackAdapter.notifyDataSetChanged();
        } else {
          Log.e("Jamendo", "No tracks found or response failed.");
        }
        hideLoadingScreen();
      }

      @Override
      public void onFailure(Call<PlaylistResponse> call, Throwable t) {
        isLoading = false; // Kết thúc tải trong trường hợp có lỗi
        hideLoadingScreen();
        Log.e("Jamendo", "Error: " + t.getMessage());
        t.printStackTrace();
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
        // Tạo độ trễ 3 giây trước khi xử lý kết quả
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
          isLoading = false; // Hoàn thành tải

          if (response.isSuccessful() && response.body() != null) {
            List<Playlist> playlists = response.body().getResults();
            List<Track> newTracks = new ArrayList<>();
            if (!playlists.isEmpty()) {
              newTracks.addAll(playlists.get(0).getTracks());
            }

            // Thêm dữ liệu mới vào danh sách và cập nhật adapter
            tracks.addAll(newTracks);
            trackAdapter.notifyDataSetChanged();
          } else {
            Log.e("Jamendo", "No tracks found or response failed.");
          }

          hideLoadingScreen(); // Ẩn loading screen sau khi hết delay
        }, 1000); // Độ trễ 3 giây
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

  private void playSong(String url) {
    try {
      showLoadingScreen();
      mediaPlayer.release();
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setDataSource(url);
      mediaPlayer.prepareAsync(); // Chuẩn bị phát nhạc không đồng bộ
      mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
          mp.start(); // Bắt đầu phát nhạc khi sẵn sàng
          playPauseButton.setImageResource(android.R.drawable.ic_media_pause); // Đổi thành biểu tượng Pause
          isPlaying = true; // Đặt trạng thái phát nhạc thành true
          hideLoadingScreen();
        }
      });
    } catch (IOException e) {
      Toast.makeText(this, "Error setting up MediaPlayer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
      Log.e("MainActivity", "Error setting up MediaPlayer: " + e.getMessage());
    }
  }


  private void playPrevSong() {
    if(currentSongIndex > 0) {
      currentSongIndex--;
    } else {
      currentSongIndex = tracks.size() - 1;
    }
    Track prevSong = tracks.get(currentSongIndex);
    playSong(prevSong.getAudio());
    currentSongTitle.setText(prevSong.getName());
  }

  private void playNextSong() {
    if (currentSongIndex < tracks.size() - 1) {
      currentSongIndex++;
    } else {
      currentSongIndex = 0; // Quay lại bài đầu tiên nếu hết danh sách
    }
    Track nextSong = tracks.get(currentSongIndex);
    playSong(nextSong.getAudio());
    currentSongTitle.setText(nextSong.getName());
  }

  private void showLoadingScreen() {
    progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar
  }

  private void hideLoadingScreen() {
    progressBar.setVisibility(View.GONE); // Ẩn ProgressBar
  }

}
