package com.example.magicmusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.magicmusic.services.MediaPlayerService;

public class NotificationReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    Intent serviceIntent = new Intent(context, MediaPlayerService.class);

    if ("PLAY".equals(action)) {
      serviceIntent.setAction("PLAY");
      context.startService(serviceIntent);
    } else if ("PAUSE".equals(action)) {
      serviceIntent.setAction("PAUSE");
      context.startService(serviceIntent);
    }
  }
}
