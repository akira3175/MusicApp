package com.example.magicmusic.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.magicmusic.services.MediaPlayerService;

public class MusicController {
  private MediaPlayerService mediaPlayerService;
  private boolean isBound = false;
  private final Context context;

  public MusicController(Context context) {
    this.context = context;
    Intent intent = new Intent(context, MediaPlayerService.class);
    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
  }

  private final ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      MediaPlayerService.MediaPlayerBinder binder = (MediaPlayerService.MediaPlayerBinder) service;
      mediaPlayerService = binder.getService();
      isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      isBound = false;
    }
  };

  public void playTrack(String url) {
    if (mediaPlayerService != null) {
      mediaPlayerService.playMusic(url);
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
}

