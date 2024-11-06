package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Album {

  @SerializedName("id")
  private int id;

  @SerializedName("name")
  private String name;

  @SerializedName("artist_name")
  private String artist_name;

  @SerializedName("image")
  private String image;

  @SerializedName("tracks")
  private List<Track> tracks;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getArtist_name() {
    return artist_name;
  }

  public String getImage() {
    return image;
  }

  public List<Track> getTracks() {
    return tracks;
  }
}
