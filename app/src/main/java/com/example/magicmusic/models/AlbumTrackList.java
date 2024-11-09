package com.example.magicmusic.models;

import android.media.Image;

public class AlbumTrackList {
    private String currentSongUrl;
    private String currentSongName;
    private String currentSongArtist;
    private String currentSongImage;
    private boolean isFavorite;
    private boolean isDownloaded;

    public AlbumTrackList(String currentSongUrl,
                          String currentSongName,
                          String currentSongArtist,
                          String currentSongImage,
                          boolean isFavorite,
                          boolean isDownloaded) {
        this.currentSongUrl = currentSongUrl;
        this.currentSongName = currentSongName;
        this.currentSongArtist = currentSongArtist;
        this.currentSongImage = currentSongImage;
        this.isFavorite = isFavorite;
        this.isDownloaded = isDownloaded;
    }

    public String getCurrentSongUrl() {
        return this.currentSongUrl;
    }

    public String getCurrentSongName() {
        return this.currentSongName;
    }

    public String getCurrentSongArtist() {
        return this.currentSongArtist;
    }

    public boolean isFavorite() {
        return this.isFavorite;
    }

    public boolean isDownloaded() {
        return this.isDownloaded;
    }

    public String getCurrentSongImage() {
        return this.currentSongImage;
    }

    public void setCurrentSongUrl(String currentSongUrl) {
        this.currentSongUrl = currentSongUrl;
    }

    public void setCurrentSongName(String currentSongName) {
        this.currentSongName = currentSongName;
    }

    public void setCurrentSongArtist(String currentSongArtist) {
        this.currentSongArtist = currentSongArtist;
    }

    public void setCurrentSongImage(String currentSongImage) {
        this.currentSongImage = currentSongImage;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }
}
