package com.example.magicmusic.GUI;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import com.example.magicmusic.R;

public class Favorite extends MainActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.favorite);
    }
}
