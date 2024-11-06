package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrackResponse {
  @SerializedName("results")
  private List<Playlist> results;

  public List<Playlist> getResults() {
    return results;
  }

}
