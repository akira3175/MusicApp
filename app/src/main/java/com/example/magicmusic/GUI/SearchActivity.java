package com.example.magicmusic.GUI;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.R;
import com.example.magicmusic.models.JamendoResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private TextView resultsText;
    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchInput);
        resultsText = findViewById(R.id.resultsText);
        Button searchButton = findViewById(R.id.searchButton);

        // Xử lý khi người dùng nhấn nút tìm kiếm
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchInput.getText().toString();
                if (!keyword.isEmpty()) {
                    searchTracks(keyword, 10);
                } else {
                    resultsText.setText("Vui lòng nhập từ khóa.");
                }
            }
        });
    }

    // Hàm tìm kiếm bài hát
    private void searchTracks(String keyword, int limit) {
        ApiClient.getJamendoApi().searchTracks("json", limit, keyword)
                .enqueue(new Callback<JamendoResponse>() {
                    @Override
                    public void onResponse(Call<JamendoResponse> call, Response<JamendoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StringBuilder results = new StringBuilder();
                            for (JamendoResponse.Track track : response.body().getResults()) {
                                results.append("Name: ").append(track.getName()).append("\n")
                                        .append("Artist: ").append(track.getArtist_name()).append("\n\n");
                            }
                            resultsText.setText(results.toString());
                        } else {
                            resultsText.setText("Không tìm thấy bài hát.");
                        }
                    }

                    @Override
                    public void onFailure(Call<JamendoResponse> call, Throwable t) {
                        Log.e(TAG, "API Error: " + t.getMessage());
                        resultsText.setText("Đã xảy ra lỗi khi tìm kiếm.");
                    }
                });
    }
}