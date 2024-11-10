package com.example.magicmusic.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadAdapter {
    public void downloadAndRenameSong(Context context, String songUrl, String newFileName) {
        // Thư mục Music riêng của ứng dụng
        File musicDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (musicDir != null && !musicDir.exists()) {
            musicDir.mkdirs();
        }

        File tempFile = new File(musicDir, "temp_song.mp3");

        try {
            URL url = new URL(songUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("DownloadAdapter", "Failed to connect: " + connection.getResponseCode());
                return;
            }

            // Tải tệp về
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            fileOutputStream.close();
            inputStream.close();
            Toast.makeText(context, "Bài hát" + newFileName + " được tải xuống thành công ở thư mục Downloads", Toast.LENGTH_SHORT).show();

            // Đổi tên tệp
            File newFile = new File(musicDir, newFileName);
            if (tempFile.renameTo(newFile)) {
                Log.d("DownloadAdapter", "File renamed successfully to: " + newFileName);
            } else {
                Log.d("DownloadAdapter", "Failed to rename file.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkFileInDownloads(Context context, String fileNameToFind) {
        Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Downloads.DISPLAY_NAME, MediaStore.Downloads._ID};

        ContentResolver contentResolver = context.getContentResolver();
        String selection = MediaStore.Downloads.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{fileNameToFind};

        try (Cursor cursor = contentResolver.query(collection, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameColumn = cursor.getColumnIndex(MediaStore.Downloads.DISPLAY_NAME);
                String displayName = cursor.getString(displayNameColumn);
                Log.d("DownloadAdapter", "Found file: " + displayName);
                return true;
            } else {
                Log.d("DownloadAdapter", "File not found in Downloads.");
            }
        }
        return false;
    }
}
