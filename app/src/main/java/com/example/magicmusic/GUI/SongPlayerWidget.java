package com.example.magicmusic.GUI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

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

    public void init(Context context) {
        LayoutInflater lf = LayoutInflater.from(context);
        lf.inflate(R.layout.song_player_widget, this, true);
    }
}