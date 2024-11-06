package com.example.magicmusic.GUI;

import static com.example.magicmusic.GUI.ListMusicActivity.CLIENT_ID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.magicmusic.API.AlbumApi;
import com.example.magicmusic.API.ApiClient;
import com.example.magicmusic.API.JamendoApi;
import com.example.magicmusic.R;
import com.example.magicmusic.adapters.AlbumAdapter;
import com.example.magicmusic.adapters.ImageSliderAdapter;
import com.example.magicmusic.models.AlbumResponse;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    List<Integer> imageList;
    RecyclerView recyclerView;
    List<AlbumResponse.Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //slider
        viewPager = findViewById(R.id.slider);
        imageList = Arrays.asList(
                R.drawable.slider1,
                R.drawable.slider2,
                R.drawable.slider3,
                R.drawable.slider4
        );
        ImageSliderAdapter adapter = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapter);
        CircleIndicator3 indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        //albums
        recyclerView = findViewById(R.id.albums_list);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        fetchAndInflateAlbums();
    }

    public void fetchAndInflateAlbums() {
        AlbumApi apiService = ApiClient.getClient().create(AlbumApi.class);
        Call<AlbumResponse> call = apiService.getAlbums(
                CLIENT_ID,
                "json",
                10,
                ""
        );

        call.enqueue(new Callback<AlbumResponse>() {
            @Override
            public void onResponse(Call<AlbumResponse> call, Response<AlbumResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    albumList = response.body().getResults();
                    // Cập nhật giao diện người dùng
                    AlbumAdapter albumAdapter = new AlbumAdapter(albumList, MainActivity.this, new AlbumAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(AlbumResponse.Album album) {
                            // Xử lý khi item được chọn
                            Toast.makeText(MainActivity.this, "Item clicked: " + album.getName(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ListMusicActivity.class);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(albumAdapter);

                    for (AlbumResponse.Album album : albumList) {
                        Log.d("Jamendo", "Track ID: " + album.getId());
                        Log.d("Jamendo", "Track Name: " + album.getName());
                        Log.d("Jamendo", "Artist: " + album.getArtist_name());
                        Log.d("Jamendo", "Image: " + album.getImage());
                    }
                    Log.d("Jamendo", response.body().toString());
                } else {
                    Log.e("Jamendo", "No tracks found or response failed.");
                }
            }

            @Override
            public void onFailure(Call<AlbumResponse> call, Throwable t) {
                Log.e("Jamendo", "Error: " + t.getMessage());
            }
        });

    }

}