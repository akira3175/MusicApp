package com.example.magicmusic.Database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseInstance {
    private static FavoriteTrackDatabase INSTANCE;

    public static FavoriteTrackDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseInstance.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FavoriteTrackDatabase.class, "FavoriteTrackDatabase")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
