package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import com.example.magicmusic.models.JamendoResponse.Track;

public class PlaylistResponse {

    @SerializedName("results")
    private List<Playlist> results;

    public List<Playlist> getResults() {
        return results;
    }

    @SerializedName("tracks")
    private List<JamendoResponse.Track> tracks;

    public static class Playlist {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("track_count")
        private int trackCount;

        @SerializedName("tracks")
        private List<Track> tracks;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getTrackCount() {
            return trackCount;
        }

        public List<Track> getTracks() {
            return tracks;
        }
    }
}
