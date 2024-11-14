package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import com.example.magicmusic.models.Track;

/*
 *   author: truong
 * */

public class PlaylistResponse {

    @SerializedName("results")
    private List<Playlist> results;

    public List<Playlist> getResults() {
        return results;
    }

}
