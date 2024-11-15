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

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavoriteTrack(FavoriteTrackDTO favoriteTrack);

    @Update
    void updateAllFavoriteTrack(List<FavoriteTrackDTO> favoriteTracks);

    @Delete
    void deleteFavoriteTrack(FavoriteTrackDTO favoriteTrack);

    @Delete
    void deleteAllFavoriteTrack(List<FavoriteTrackDTO> favoriteTracks);

    @Query("SELECT * FROM FavoriteTrackDatabase")
    List<FavoriteTrackDTO> getAllFavoriteTracks();

    @Query("SELECT * FROM FavoriteTrackDatabase WHERE songId = :trackId")
    FavoriteTrackDTO getFavoriteTrack(long trackId);

    @Query("SELECT * FROM FavoriteTrackDatabase WHERE song_name = :songName LIMIT 1")
    FavoriteTrackDTO getTrackByName(String songName);

    // Cập nhật trạng thái tải xuống của bài hát bằng songUrl
    @Query("UPDATE FavoriteTrackDatabase SET is_downloaded = :status WHERE song_url = :songUrl")
    void updateDownloadedStatus(String songUrl, boolean status);

    // Lấy thông tin bài hát từ songUrl
    @Query("SELECT * FROM FavoriteTrackDatabase WHERE song_url = :songUrl LIMIT 1")
    FavoriteTrackDTO getTrackByUrl(String songUrl);
}
