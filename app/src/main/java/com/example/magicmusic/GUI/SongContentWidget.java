package com.example.magicmusic.GUI;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;

import com.example.magicmusic.R;

public class SongContentWidget extends LinearLayout {
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

    public void init(Context context) {
        LayoutInflater lf = LayoutInflater.from(context);
        lf.inflate(R.layout.song_content_widget, this, true);
    }
}
