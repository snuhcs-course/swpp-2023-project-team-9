package com.littlestudio;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.ui.constant.IntentExtraKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageActivity extends AppCompatActivity {

    DrawingRepository drawingRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        drawingRepository = new DrawingRepository(new DrawingRemoteDataSource(), new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper())));

        int drawingId = getIntent().getIntExtra(IntentExtraKey.DRAWING_ID, 0);
        String imageUrl = getIntent().getStringExtra(IntentExtraKey.DRAWING_IMAGE_URL);

        ImageView imageView = findViewById(R.id.image_view);
        Glide.with(this).load(imageUrl).into(imageView);

        drawingRepository.getDrawing(drawingId, new Callback<Drawing>() {
            @Override
            public void onResponse(Call<Drawing> call, Response<Drawing> response) {
                Drawing drawing = response.body();
                TextView title = findViewById(R.id.gallery_detail_title_value);
                TextView description = findViewById(R.id.gallery_detail_desc_value);
                title.setText(drawing.title);
                description.setText(drawing.description);
            }

            @Override
            public void onFailure(Call<Drawing> call, Throwable t) {
                //
            }
        });

        findViewById(com.littlestudio.R.id.image_close_drawing).setOnClickListener(v -> {
            finish();
        });
    }
}