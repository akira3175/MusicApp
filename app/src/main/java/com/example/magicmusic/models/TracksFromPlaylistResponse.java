package com.example.magicmusic.models;

import java.util.List;

public class TracksFromPlaylistResponse {
    private List<Track> results;

    public List<Track> getResults() {
        return results;
    }

    public void setResults(List<Track> results) {
        this.results = results;
    }
}
