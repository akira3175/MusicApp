package com.example.magicmusic.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

import java.io.IOException;

public class MediaPlayerService extends Service {
  private final IBinder binder = new MediaPlayerBinder();
  private MediaPlayer mediaPlayer;
  private boolean isPlaying = false;

  public class MediaPlayerBinder extends Binder {
    public MediaPlayerService getService() {
      return MediaPlayerService.this;
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mediaPlayer = new MediaPlayer();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  public void playMusic(String url) {
    if (isPlaying) {
      mediaPlayer.stop();
      mediaPlayer.reset();
    }
    try {
      mediaPlayer.setDataSource(url);
      mediaPlayer.prepareAsync();
      mediaPlayer.setOnPreparedListener(mp -> {
        mp.start();
        isPlaying = true;
      });
    } catch (IOException e) {
      Log.e("MediaPlayerService", "Error setting data source", e);
    }
  }

  public void pauseMusic() {
    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
      mediaPlayer.pause();
      isPlaying = false;
    }
  }

  public void resumeMusic() {
    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
      mediaPlayer.start();
      isPlaying = true;
    }
  }

  public void stopMusic() {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
      mediaPlayer.reset();
      isPlaying = false;
    }
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  @Override
  public void onDestroy() {
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
    super.onDestroy();
  }
}

