package com.example.magicmusic.GUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.magicmusic.R;

/*
* author: truong ngo
* launch screen test, phai cai trong manifest de chay
* */

public class SplashActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    // Bắt đầu animation và chuyển sang MainActivity sau khi hoàn tất
    startIntroAnimation();
  }

  private void startIntroAnimation() {
    // Áp dụng hoạt ảnh (ví dụ dùng AlphaAnimation cho hiệu ứng mờ dần)
    View logo = findViewById(R.id.logo);
    logo.setAlpha(0f);
    logo.animate()
            .alpha(1f)
            .setDuration(2000)  // Thời gian chạy animation là 2 giây
            .withEndAction(() -> {
              // Chuyển sang MainActivity sau khi animation kết thúc
              startActivity(new Intent(SplashActivity.this, MainActivity.class));
              finish();
            })
            .start();
  }
}

