package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlbumResponse {
  @SerializedName("results")
  private List<Album> results;

  public List<Album> getResults() {
    return results;
  }

}
