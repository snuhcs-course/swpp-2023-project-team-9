package com.littlestudio.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
    private int drawingId;
    private MediaPlayer currentMediaPlayer;


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
        findViewById(com.littlestudio.R.id.image_close_drawing).setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        drawingId = getIntent().getIntExtra(IntentExtraKey.DRAWING_ID, 0);
        String imageUrl = getIntent().getStringExtra(IntentExtraKey.DRAWING_IMAGE_URL);

        ImageView imageView = findViewById(R.id.image_view);
        Glide.with(this).load(imageUrl).into(imageView);

        ImageView originalImageView = findViewById(R.id.original_image_view);
        Glide.with(this).load(imageUrl).into(originalImageView);
        originalImageView.setOnClickListener(v -> {
            onImageViewClicked(originalImageView.getId());
            Glide.with(this).load(imageUrl).into(imageView);
        });

        ImageView dabImageView = findViewById(R.id.dab_image_view);
        ImageView jumpingImageView = findViewById(R.id.jumping_image_view);
        ImageView zombieImageView = findViewById(R.id.zombie_image_view);

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

                String dabUrl = drawing.gif_dab_url;
                String jumpingUrl = drawing.gif_jumping_url;
                String zombieUrl = drawing.gif_zombie_url;

                Glide.with(getApplication()).load(dabUrl).into(dabImageView);
                dabImageView.setOnClickListener(v -> {
                    onImageViewClicked(dabImageView.getId());
                    Glide.with(getApplication()).load(dabUrl).into(imageView);
                });

                Glide.with(getApplication()).load(jumpingUrl).into(jumpingImageView);
                jumpingImageView.setOnClickListener(v -> {
                    onImageViewClicked(jumpingImageView.getId());
                    Glide.with(getApplication()).load(jumpingUrl).into(imageView);
                });

                Glide.with(getApplication()).load(zombieUrl).into(zombieImageView);
                zombieImageView.setOnClickListener(v -> {
                    onImageViewClicked(zombieImageView.getId());
                    Glide.with(getApplication()).load(zombieUrl).into(imageView);
                });

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayer dabSFX = MediaPlayer.create(this, R.raw.dab_sfx);
        MediaPlayer jumpingSFX = MediaPlayer.create(this, R.raw.jump_sfx);
        MediaPlayer zombieSFX = MediaPlayer.create(this, R.raw.zombie_sfx);

        if (currentMediaPlayer != null) {
            currentMediaPlayer.release();
            currentMediaPlayer = null;
        }

        if (dabSFX != null) {
            dabSFX.release();
        }
        if (jumpingSFX != null) {
            jumpingSFX.release();
            jumpingSFX.release();
        }
        if (zombieSFX != null) {
            zombieSFX.release();
        }
    }

    private void onImageViewClicked(int imageViewId) {
        setForeground(selectedImageViewId, R.drawable.black_border_thin);
        setForeground(imageViewId, R.drawable.orange_border_thin);
        selectedImageViewId = imageViewId;

        if (currentMediaPlayer != null && currentMediaPlayer.isPlaying()) {
            currentMediaPlayer.release();
            currentMediaPlayer = null;
        }

        ImageView dabImageView = findViewById(R.id.dab_image_view);
        ImageView jumpingImageView = findViewById(R.id.jumping_image_view);
        ImageView zombieImageView = findViewById(R.id.zombie_image_view);

        if (imageViewId == dabImageView.getId()) {
            currentMediaPlayer = MediaPlayer.create(this, R.raw.dab_sfx);
            playSound(currentMediaPlayer);
        } else if (imageViewId == jumpingImageView.getId()) {
            currentMediaPlayer = MediaPlayer.create(this, R.raw.jump_sfx);
            playSoundWithDelay(currentMediaPlayer, 0);
            playSoundWithDelay(currentMediaPlayer, 2000);
        } else if (imageViewId == zombieImageView.getId()) {
            currentMediaPlayer = MediaPlayer.create(this, R.raw.zombie_sfx);
            playSound(currentMediaPlayer);
        }
    }

    private void playSound(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                currentMediaPlayer = null;
            });
            mediaPlayer.start();
        }
    }

    private void playSoundWithDelay(MediaPlayer mediaPlayer, long delayMillis) {
        new Handler().postDelayed(() -> {
            if (mediaPlayer != null) {
                try {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                } catch (IllegalStateException e) {
                    mediaPlayer.release();
                    currentMediaPlayer = null;
                }
            }
        }, delayMillis);
    }

    private void setForeground(int imageViewId, int foregroundDrawableResource) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setForeground(ContextCompat.getDrawable(this, foregroundDrawableResource));
    }
}