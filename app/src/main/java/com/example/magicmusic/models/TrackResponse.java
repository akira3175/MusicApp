package com.example.magicmusic.models;

import java.util.List;

public class TrackResponse {
    private List<Track> results;

    public List<Track> getResults() {
        return results;
    }

    public void setResults(List<Track> results) {
        this.results = results;
    }

    // Tạo class Track nếu chưa có để ánh xạ các thông tin của từng bài hát
    public static class Track {
        private String name;
        private String artist_name;
        private String album_name;

        // Thêm các field và getter/setter phù hợp với cấu trúc trả về từ API
    }
}
