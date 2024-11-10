package com.example.magicmusic.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.magicmusic.R;

public class SongPlayerWidget extends LinearLayout {
    public SongPlayerWidget(Context context) {
        super(context);
        init(context);
    }

    public SongPlayerWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SongPlayerWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setSongName(String title) {
        TextView songTitle = findViewById(R.id.song_title);
        songTitle.setText(title);
        songTitle.setSelected(true); // Bắt buộc cần để marquee hoạt động
        songTitle.requestFocus();
    }

    public void setSongArtist(String artist) {
        TextView songArtist = findViewById(R.id.song_artist);
        songArtist.setText(artist);
        songArtist.setSelected(true); // Bắt buộc cần để marquee hoạt động
        songArtist.requestFocus();
    }

    public void setPlayButtonState(int state) {
        ImageButton playButton = findViewById(R.id.play_button);
        switch(state) {
            case 1: // Nút Play
                playButton.setImageResource(R.drawable.play_song_button);
                break;
            case 2: // Nút Pause
                playButton.setImageResource(R.drawable.pause_button);
                break;
        }
    }

    public void setLoopButtonState(int state) {
        ImageButton loopButton = findViewById(R.id.play_mode_button);
        switch(state) {
            case 1: // Nút NoRepeat
                loopButton.setImageResource(R.drawable.song_no_repeat);
                break;
            case 2: // Nút Repeat
                loopButton.setImageResource(R.drawable.song_repeat);
                break;
            case 3: // Nút Shuffle
                loopButton.setImageResource(R.drawable.song_shuffle);
                break;
        }
    }

    public void setSongPlayerView(String title, String artist, int playButtonState, int loopButtonState) {
        setSongName(title);
        setSongArtist(artist);
        setPlayButtonState(playButtonState);
        setLoopButtonState(loopButtonState);
    }

    public String getSongName() {
        TextView str = findViewById(R.id.song_title);
        return str.getText().toString();
    }

    public String getSongArtist() {
        TextView str = findViewById(R.id.song_artist);
        return str.getText().toString();
    }

    public void init(Context context) {
        LayoutInflater lf = LayoutInflater.from(context);
        lf.inflate(R.layout.song_player_widget, this, true);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        this.setLayoutParams(layoutParams);
    }
}