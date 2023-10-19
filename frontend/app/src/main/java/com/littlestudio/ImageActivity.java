package com.littlestudio;

import static com.littlestudio.DrawAdapter.IMAGE_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.littlestudio.ui.gallery.GalleryFragment;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        String path = getIntent().getStringExtra(IMAGE_PATH);
        ImageView imageView = findViewById(R.id.image_view);
        Glide.with(this).load(path).into(imageView);

        findViewById(com.littlestudio.R.id.image_close_drawing).setOnClickListener(v -> {
            Intent intent = new Intent(this, GalleryFragment.class);
            startActivity(intent);
            finish();
        });
    }
}