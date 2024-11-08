package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JamendoResponse {

    @SerializedName("results")
    private List<Track> results;

    public List<Track> getResults() {
        return results;
    }

}
