package com.example.magicmusic.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteTrackDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavoriteTrack(FavoriteTrackDTO favoriteTrack);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllFavoriteTrack(List<FavoriteTrackDTO> favoriteTracks);

    @Update
    void updateFavoriteTrack(FavoriteTrackDTO favoriteTrack);

    @Update
    void updateAllFavoriteTrack(List<FavoriteTrackDTO> favoriteTracks);

    @Delete
    void deleteFavoriteTrack(FavoriteTrackDTO favoriteTrack);

    @Delete
    void deleteAllFavoriteTrack(List<FavoriteTrackDTO> favoriteTracks);

    @Query("SELECT * FROM FavoriteTrackDatabase")
    List<FavoriteTrackDTO> getAllFavoriteTracks();

    @Query("SELECT * FROM FavoriteTrackDatabase WHERE song_url = :songUrl")
    FavoriteTrackDTO getFavoriteTrackBySongUrl(String songUrl);
}
