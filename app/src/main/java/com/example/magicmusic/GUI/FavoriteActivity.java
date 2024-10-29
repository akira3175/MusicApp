package com.example.magicmusic.GUI;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.magicmusic.R;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.favorite);

        ScrollView sv = findViewById(R.id.scroll_view);
        TextView svNoContentNotify = findViewById(R.id.scroll_view_notify);
        LinearLayout contentLayout = findViewById(R.id.scroll_view_container);

        if (contentLayout.getChildCount() == 0) {
            svNoContentNotify.setText(R.string.favorite_empty_list);
            svNoContentNotify.setVisibility(View.VISIBLE);
        } else {
            svNoContentNotify.setVisibility(View.GONE);
        }
    }
}
