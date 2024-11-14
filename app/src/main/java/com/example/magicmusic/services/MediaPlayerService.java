package com.example.magicmusic.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import com.example.magicmusic.R;
import com.example.magicmusic.models.Track;
import com.example.magicmusic.receivers.NotificationReceiver;

import java.io.IOException;

public class MediaPlayerService extends Service {
  private final IBinder binder = new MediaPlayerBinder();
  private MediaPlayer mediaPlayer;
  private boolean isPlaying = false;
  private Track currentTrack;
  private static final String CHANNEL_ID = "MusicPlayerChannel";
  private static final int NOTIFICATION_ID = 1;

  public class MediaPlayerBinder extends Binder {
    public MediaPlayerService getService() {
      return MediaPlayerService.this;
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mediaPlayer = new MediaPlayer();
    createNotificationChannel();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      String action = intent.getAction();
      if ("PLAY".equals(action)) {
        resumeMusic();
      } else if ("PAUSE".equals(action)) {
        pauseMusic();
      }
    }
    return START_STICKY;
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

  public void playMusic(Track track) {
    currentTrack = track;
    showNotification();
    if (isPlaying) {
      mediaPlayer.stop();
      mediaPlayer.reset();
    }
    try {
      mediaPlayer.setDataSource(track.getAudio());
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
    currentTrack = null;
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

  public void setLooping(boolean isLooping) {
    if (mediaPlayer != null) {
      mediaPlayer.setLooping(isLooping);
    }
  }

  public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
    if (mediaPlayer != null) {
      mediaPlayer.setOnCompletionListener(listener);
    }
  }

  public Track getCurrentTrack() {
    return currentTrack;
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
              CHANNEL_ID,
              "Music Player",
              NotificationManager.IMPORTANCE_LOW
      );
      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(channel);
    }
  }

  private void showNotification() {
    Intent playIntent = new Intent(this, NotificationReceiver.class).setAction("PLAY");
    PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_MUTABLE);

    Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction("PAUSE");
    PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_MUTABLE);

    // Xây dựng Notification với MediaStyle
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle("Music Player")
            .setContentText(isPlaying ? "Playing" : "Paused")
            .addAction(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play, isPlaying ? "Pause" : "Play",
                    isPlaying ? pausePendingIntent : playPendingIntent)
            .setStyle(new MediaStyle().setShowActionsInCompactView(0)) // Sử dụng androidx.media.app.NotificationCompat.MediaStyle
            .setOngoing(isPlaying)
            .setAutoCancel(false);

    startForeground(NOTIFICATION_ID, builder.build());

    startForeground(NOTIFICATION_ID, builder.build());
  }

}

