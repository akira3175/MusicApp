//package com.example.magicmusic.adapters;
//
//import android.annotation.SuppressLint;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import com.example.magicmusic.Database.FavoriteTrackDTO;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class DownloadAdapter {
//    public void downloadAndRenameSong(Context context, String songUrl, String newFileName) {
//        // Thư mục Music riêng của ứng dụng
//        File musicDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        if (musicDir != null && !musicDir.exists()) {
//            musicDir.mkdirs();
//        }
//
//        File tempFile = new File(musicDir, "temp_song.mp3");
//
//        try {
//            URL url = new URL(songUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.connect();
//
//            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                Log.d("DownloadAdapter", "Failed to connect: " + connection.getResponseCode());
//                return;
//            }
//
//            // Tải tệp về
//            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
//            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
//            byte[] dataBuffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
//                fileOutputStream.write(dataBuffer, 0, bytesRead);
//            }
//            fileOutputStream.close();
//            inputStream.close();
//            Toast.makeText(context, "Bài hát" + newFileName + " được tải xuống thành công ở thư mục Downloads", Toast.LENGTH_SHORT).show();
//
//            // Đổi tên tệp
//            File newFile = new File(musicDir, newFileName);
//            if (tempFile.renameTo(newFile)) {
//                Log.d("DownloadAdapter", "File renamed successfully to: " + newFileName);
//            } else {
//                Log.d("DownloadAdapter", "Failed to rename file.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean checkFileInDownloads(Context context, String fileNameToFind) {
//        Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
//        String[] projection = {MediaStore.Downloads.DISPLAY_NAME, MediaStore.Downloads._ID};
//
//        ContentResolver contentResolver = context.getContentResolver();
//        String selection = MediaStore.Downloads.DISPLAY_NAME + " = ?";
//        String[] selectionArgs = new String[]{fileNameToFind};
//
//        try (Cursor cursor = contentResolver.query(collection, projection, selection, selectionArgs, null)) {
//            if (cursor != null && cursor.moveToFirst()) {
//                int displayNameColumn = cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME);
//                String displayName = cursor.getString(displayNameColumn);
//                Log.d("DownloadAdapter", "Found file: " + displayName);
//                return true;
//            } else {
//                Log.d("DownloadAdapter", "File not found in Downloads.");
//            }
//        }
//        return false;
//    }
//
//    public List<FavoriteTrackDTO> findSongsInDownloads(Context context) {
//        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  // Dùng MediaStore.Audio để tìm kiếm các file âm thanh
//        String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID}; // Lấy cả tên và ID bài hát
//
//        ContentResolver contentResolver = context.getContentResolver();
//        String selection = MediaStore.Audio.Media.RELATIVE_PATH + " LIKE ?"; // Lọc các bài hát trong thư mục Downloads
//        String[] selectionArgs = new String[]{"%Download%"};
//
//        List<FavoriteTrackDTO> songNames = new ArrayList<>(); // Danh sách để lưu tên bài hát
//
//        try (Cursor cursor = contentResolver.query(collection, projection, selection, selectionArgs, null)) {
//            if (cursor != null && cursor.moveToFirst()) {
//                int displayNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
//                int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
//                do {
//                    if (displayNameColumn != -1 && idColumn != -1) {  // Kiểm tra nếu cột tồn tại
//                        long songId = cursor.getLong(idColumn);
//                        Uri songUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(songId));
//                        String fileName = cursor.getString(displayNameColumn);
//
//                        songNames.add(new FavoriteTrackDTO(songUri.toString(), fileName));  // Thêm bài hát vào danh sách
//                        Log.d("DownloadAdapter", "Found song in Downloads: " + fileName);
//                    }
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return songNames;
//    }
//
//    public List<FavoriteTrackDTO> downloadedSongsStringSpliterator(List<FavoriteTrackDTO> downloadedSongs) {
//        List<FavoriteTrackDTO> result = new ArrayList<>();
//
//        for (FavoriteTrackDTO i : downloadedSongs) {
//            String[] parts = i.getText().split("-");
//            if (parts.length >= 2) {
//                String songName = parts[0];
//                String songArtist = parts[1].replace(".mp3", "");
//                result.add(new FavoriteTrackDTO(i.getSongUrl(), songName, songArtist));
//            } else {
//                result.add(new FavoriteTrackDTO(i.getSongUrl(), parts[0], null));
//            }
//        }
//        return result;
//    }
//}
