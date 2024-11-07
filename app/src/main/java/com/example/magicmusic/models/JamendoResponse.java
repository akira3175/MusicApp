package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JamendoResponse {

    @SerializedName("results")
    private List<Track> results;

    public List<Track> getResults() {
        return results;
    }

    public static class Track {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("artist_name")
        private String artist_name;

        @SerializedName("audio")
        private String audio;

        @SerializedName("image")
        private String image;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getArtist_name() {
            return artist_name;
        }

        public String getAudio() { // Getter cho audio
            return audio;
        }

        public String getImage() {
            return image;
        }
    }

}
