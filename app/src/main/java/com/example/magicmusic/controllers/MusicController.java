package com.example.magicmusic.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.magicmusic.Database.FavoriteTrackDTO;
import com.example.magicmusic.models.Track;
import com.example.magicmusic.services.MediaPlayerService;

public class MusicController {
  private MediaPlayerService mediaPlayerService;
  private boolean isBound = false;
  private final Context context;
  private OnServiceConnectedListener onServiceConnectedListener;
  private MediaPlayer.OnPreparedListener onPreparedListener = (mediaPlayer) -> {};
  private MediaPlayer.OnCompletionListener onCompletionListener = (mediaPlayer) -> {};

  public interface OnServiceConnectedListener {
    void onServiceConnected();
  }

  public void setOnServiceConnectedListener(OnServiceConnectedListener listener) {
    this.onServiceConnectedListener = listener;
  }

  public MusicController(Context context) {
    this.context = context;
    Intent intent = new Intent(context, MediaPlayerService.class);
    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
  }

  public MusicController(Context context, MediaPlayer.OnPreparedListener onPreparedListener) {
    this.context = context;
    Intent intent = new Intent(context, MediaPlayerService.class);
    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    this.onPreparedListener = onPreparedListener;
  }

  public MusicController(Context context, MediaPlayer.OnPreparedListener onPreparedListener, MediaPlayer.OnCompletionListener onCompletionListener) {
    this.context = context;
    Intent intent = new Intent(context, MediaPlayerService.class);
    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    this.onPreparedListener = onPreparedListener;
    this.onCompletionListener = onCompletionListener;
  }

  private final ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
      mediaPlayerService = binder.getService();
      isBound = true;
      if (onServiceConnectedListener != null) {
        onServiceConnectedListener.onServiceConnected();
      }
      mediaPlayerService.setOnPreparedListener(onPreparedListener);
      mediaPlayerService.setOnCompletionListener(onCompletionListener);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      isBound = false;
    }
  };

  public void playTrack(Track track) {
    if (mediaPlayerService != null) {
      mediaPlayerService.playMusic(track);
    }
  }

  public void playTrack(FavoriteTrackDTO ftrack) {
    if (mediaPlayerService != null) {
      mediaPlayerService.playMusic(ftrack);
    }
  }

  public void pauseTrack() {
    if (mediaPlayerService != null) {
      mediaPlayerService.pauseMusic();
    }
  }

  public void resumeTrack() {
    if (mediaPlayerService != null) {
      mediaPlayerService.resumeMusic();
    }
  }

  public boolean isPlaying() {
    return mediaPlayerService != null && mediaPlayerService.isPlaying();
  }

  public void release() {
    if (isBound) {
      context.unbindService(serviceConnection);
      isBound = false;
    }
  }

  public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
    if (mediaPlayerService != null) {
      mediaPlayerService.setOnCompletionListener(listener);
    }
  }

  public Track getCurrentTrack() {
    return mediaPlayerService != null ? mediaPlayerService.getCurrentTrack() : null;
  }

  public FavoriteTrackDTO getCurrentfTrack() {
    return mediaPlayerService != null ? mediaPlayerService.getCurrentfTrack() : null;
  }

  public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
    if (onPreparedListener != null) {
      onPreparedListener = listener;
    }
  }

  public MediaPlayer getMediaPlayer() {
    return mediaPlayerService != null ? mediaPlayerService.getMediaPlayer() : null;
  }

}

