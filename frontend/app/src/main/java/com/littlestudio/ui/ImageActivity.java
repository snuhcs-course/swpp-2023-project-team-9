package com.littlestudio.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.model.User;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.data.repository.UserRepository;
import com.littlestudio.ui.constant.ErrorMessage;
import com.littlestudio.ui.constant.IntentExtraKey;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageActivity extends AppCompatActivity {
    DrawingRepository drawingRepository;
    UserRepository userRepository;
    private int selectedImageViewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        drawingRepository = DrawingRepository.getInstance(
                DrawingRemoteDataSource.getInstance(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );

        userRepository = UserRepository.getInstance(
                UserRemoteDataSource.getInstance(),
                UserLocalDataSource.getInstance(getApplicationContext())
        );

        int drawingId = getIntent().getIntExtra(IntentExtraKey.DRAWING_ID, 0);
        String imageUrl = getIntent().getStringExtra(IntentExtraKey.DRAWING_IMAGE_URL);
        String dabUrl = getIntent().getStringExtra(IntentExtraKey.DRAWING_DAB_URL);
        String jumpingUrl = getIntent().getStringExtra(IntentExtraKey.DRAWING_JUMPING_URL);
        String zombieUrl = getIntent().getStringExtra(IntentExtraKey.DRAWING_ZOMBIE_URL);

        ImageView imageView = findViewById(R.id.image_view);
        Glide.with(this).load(imageUrl).into(imageView);

        ImageView originalImageView = findViewById(R.id.original_image_view);
        Glide.with(this).load(imageUrl).into(originalImageView);
        originalImageView.setOnClickListener(v -> {
            onImageViewClicked(originalImageView.getId());
            Glide.with(this).load(imageUrl).into(imageView);
        });

        ImageView dabImageView = findViewById(R.id.dab_image_view);
        Glide.with(this).load(dabUrl).into(dabImageView);
        dabImageView.setOnClickListener(v -> {
            onImageViewClicked(dabImageView.getId());
            Glide.with(this).load(dabUrl).into(imageView);
        });

        ImageView jumpingImageView = findViewById(R.id.jumping_image_view);
        Glide.with(this).load(jumpingUrl).into(jumpingImageView);
        jumpingImageView.setOnClickListener(v -> {
            onImageViewClicked(jumpingImageView.getId());
            Glide.with(this).load(jumpingUrl).into(imageView);
        });

        ImageView zombieImageView = findViewById(R.id.zombie_image_view);
        Glide.with(this).load(zombieUrl).into(zombieImageView);
        zombieImageView.setOnClickListener(v -> {
            onImageViewClicked(zombieImageView.getId());
            Glide.with(this).load(zombieUrl).into(imageView);
        });

        selectedImageViewId = originalImageView.getId(); // default to original image
        setForeground(selectedImageViewId, R.drawable.orange_border_thin);

        drawingRepository.getDrawing(drawingId, new Callback<Drawing>() {
            @Override
            public void onResponse(Call<Drawing> call, Response<Drawing> response) {
                Drawing drawing = response.body();
                TextView title = findViewById(R.id.gallery_detail_title);
                TextView description = findViewById(R.id.gallery_detail_desc);
                TextView createdOn = findViewById(R.id.gallery_detail_created_on);
                TextView participants = findViewById(R.id.gallery_detail_participants);
                title.setText(drawing.title);
                description.setText(drawing.description);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String createOn = dateFormat.format(drawing.created_at);

                String combinedString = drawing.participants.stream()
                        .map(participant -> participant.getFamilyDisplayName(userRepository.getUser()))
                        .collect(Collectors.joining(", "));

                createdOn.setText("Created on " + createOn);
                participants.setText("Drawn by " + combinedString);
            }

            @Override
            public void onFailure(Call<Drawing> call, Throwable t) {
                Toast.makeText(getApplicationContext(), ErrorMessage.DEFAULT, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(com.littlestudio.R.id.image_close_drawing).setOnClickListener(v -> {
            finish();
        });
    }

    private void onImageViewClicked(int imageViewId) {
        setForeground(selectedImageViewId, R.drawable.black_border_thin);
        setForeground(imageViewId, R.drawable.orange_border_thin);
        selectedImageViewId = imageViewId;
    }

    private void setForeground(int imageViewId, int foregroundDrawableResource) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setForeground(ContextCompat.getDrawable(this, foregroundDrawableResource));
    }
}