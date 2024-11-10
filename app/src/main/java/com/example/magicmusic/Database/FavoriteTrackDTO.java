package com.example.magicmusic.Database;

import android.media.Image;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FavoriteTrackDatabase")
public class FavoriteTrackDTO {
    @PrimaryKey
    private int songId;

    @ColumnInfo(name = "song_url")
    private String songUrl;

    @ColumnInfo(name = "song_name")
    private String songName;

    @ColumnInfo(name = "song_artist")
    private String songArtist;

    @ColumnInfo(name = "song_image")
    private Image songImage;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    public FavoriteTrackDTO() {
        this.songId = 0;
        this.songUrl = "";
        this.songName = "";
        this.songArtist = "";
        this.songImage = null;
        this.isFavorite = false;
    }

    public FavoriteTrackDTO(int songId, String songUrl, String songName, String songArtist, Image songImage, boolean isFavorite) {
        this.songId = songId;
        this.songUrl = songUrl;
        this.songName = songName;
        this.songArtist = songArtist;
        this.songImage = songImage;
        this.isFavorite = isFavorite;
    }

    public int getSongId() {
        return this.songId;
    }

    public String getSongUrl() {
        return this.songUrl;
    }

    public String getSongName() {
        return this.songName;
    }

    public String getSongArtist() {
        return this.songArtist;
    }

    public Image getSongImage() {
        return this.songImage;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public void setSongImage(Image songImage) {
        this.songImage = songImage;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
