package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Playlist {
  @SerializedName("id")
  private int id;

  @SerializedName("name")
  private String name;

  @SerializedName("tracks")
  private List<Track> tracks;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<Track> getTracks() {
    return tracks;
  }
}
