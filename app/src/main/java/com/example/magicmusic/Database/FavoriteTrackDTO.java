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
    private String songImageUrl;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    public String text;

    public FavoriteTrackDTO() {
        this.songId = 0;
        this.songUrl = "";
        this.songName = "";
        this.songArtist = "";
        this.songImageUrl = null;
        this.isFavorite = false;
    }

    public FavoriteTrackDTO(int songId, String songUrl, String songName, String songArtist, String songImageUrl, boolean isFavorite) {
        this.songId = songId;
        this.songUrl = songUrl;
        this.songName = songName;
        this.songArtist = songArtist;
        this.songImageUrl = songImageUrl;
        this.isFavorite = isFavorite;
    }

    public FavoriteTrackDTO(String songUrl, String songName, String songArtist) {
        this.songUrl = songUrl;
        this.songName = songName;
        this.songArtist = songArtist;
    }

    public FavoriteTrackDTO(String songUrl, String text) {
        this.songUrl = songUrl;
        this.text = text;
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

    public String getSongImageUrl() {
        return this.songImageUrl;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public String getText() {
        return this.text;
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

    public void setSongImageUrl(String songImageUrl) {
        this.songImageUrl = songImageUrl;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setText(String text) {
        this.text = text;
    }
}
