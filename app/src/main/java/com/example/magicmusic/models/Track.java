package com.example.magicmusic.models;

import com.google.gson.annotations.SerializedName;

/*
 *   author: truong
 * */

public class Track {
  @SerializedName("id")
  private int id;

  @SerializedName("name")
  private String name;

  @SerializedName("artist_name")
  private String artist_name;

  @SerializedName("audio")
  private String audio; // Thêm thuộc tính preview

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
