package com.example.magicmusic.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.magicmusic.R;

public class SongContentWidget extends LinearLayout {
    private String songUrl;

    public SongContentWidget(Context context) {
        super(context);
        init(context);
    }

    public SongContentWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SongContentWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setSongName(String title) {
        TextView songName = findViewById(R.id.song_title);
        songName.setText(title);
        songName.setSelected(true); // Bắt buộc cần để marquee hoạt động
        songName.requestFocus();
    }

    public void setSongArtist(String artist) {
        TextView songArtist = findViewById(R.id.song_artist);
        songArtist.setText(artist);
        songArtist.setSelected(true); // Bắt buộc cần để marquee hoạt động
        songArtist.requestFocus();
    }

    public void setSongDuration(String duration) {
        TextView songDuration = findViewById(R.id.song_duration);
        songDuration.setText(duration);
    }

    public void setSongFavorite(boolean isFavorite) {
        ImageButton favButton = findViewById(R.id.fav_button);
        if (isFavorite) {
            favButton.setImageResource(R.drawable.song_hearted);
        } else {
            favButton.setImageResource(R.drawable.song_unhearted);
        }
    }

    public void setSongUrl(String url) {
        this.songUrl = url;
    }

    public String getSongUrl() {
        return this.songUrl;
    }

    public String getSongName() {
        TextView songName = findViewById(R.id.song_title);
        return songName.getText().toString();
    }

    public String getSongArtist() {
        TextView songArtist = findViewById(R.id.song_artist);
        return songArtist.getText().toString();
    }

    public String getSongDuration() {
        TextView songDuration = findViewById(R.id.song_duration);
        return songDuration.getText().toString();
    }

    public boolean getSongFavorite() {
        ImageButton favButton = findViewById(R.id.fav_button);
        return favButton.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.song_hearted).getConstantState();
    }

    public void init(Context context) {
        LayoutInflater lf = LayoutInflater.from(context);
        lf.inflate(R.layout.song_content_widget, this, true);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        this.setLayoutParams(layoutParams);
    }
}
