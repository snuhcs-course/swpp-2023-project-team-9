package com.littlestudio.ui.drawing;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.ui.ImageActivity;
import com.littlestudio.ui.constant.ErrorMessage;
import com.littlestudio.ui.constant.IntentExtraKey;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubmitActivity extends AppCompatActivity {
    DrawingRepository drawingRepository;
    AppCompatButton finishButton;
    LinearLayout loadingIndicator;
    EditText titleEditText;
    EditText descriptionEditText;
    private MediaPlayer mediaPlayer;
    int drawingId;
    String title;
    String description;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        mediaPlayer = MediaPlayer.create(this, R.raw.bgm);

        drawingRepository = DrawingRepository.getInstance(
                DrawingRemoteDataSource.getInstance(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );

        byte[] imageByteArray = getIntent().getByteArrayExtra(IntentExtraKey.DRAWING_IMAGE_BYTE_ARRAY);
        drawingId = getIntent().getIntExtra(IntentExtraKey.DRAWING_ID, 0);
        bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

        ImageView imageView = (ImageView) findViewById(R.id.drawing);
        imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imageView.getWidth(), imageView.getHeight(), false));
            }
        });

        finishButton = findViewById(R.id.submit_btn);
        loadingIndicator = findViewById(R.id.loading_indicator);
        titleEditText = (EditText) findViewById(R.id.editText_title);
        descriptionEditText = (EditText) findViewById(R.id.editText_description);

        finishButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Submit Drawing")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        title = titleEditText.getText().toString();
                        description = descriptionEditText.getText().toString();

                        if (title.isEmpty() || description.isEmpty()) {
                            Toast.makeText(getApplicationContext(), ErrorMessage.EMPTY_TITLE_AND_DESCRIPTION, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        submitDrawing();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        // Do nothing
                    })
                    .show();
        });
    }

    private void submitDrawing() {
        setLoading(true);
        drawingRepository.submitDrawing(
                drawingId,
                new DrawingSubmitRequestDto(
                        bitmapToString(bitmap),
                        title,
                        description
                ), new Callback<Drawing>() {
                    @Override
                    public void onResponse(Call<Drawing> call, Response<Drawing> response) {
                        setLoading(false);
                        String drawingImageUrl = response.body().image_url;
                        String gifDabUrl = response.body().gif_dab_url;
                        String gifJumpingUrl = response.body().gif_jumping_url;
                        String gifZombieUrl = response.body().gif_zombie_url;
                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                        intent.putExtra(IntentExtraKey.DRAWING_ID, drawingId);
                        intent.putExtra(IntentExtraKey.DRAWING_IMAGE_URL, drawingImageUrl);
                        intent.putExtra(IntentExtraKey.DRAWING_DAB_URL, gifDabUrl);
                        intent.putExtra(IntentExtraKey.DRAWING_JUMPING_URL, gifJumpingUrl);
                        intent.putExtra(IntentExtraKey.DRAWING_ZOMBIE_URL, gifZombieUrl);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        setLoading(false);
                        new AlertDialog.Builder(SubmitActivity.this)
                                .setTitle("Error occurred")
                                .setMessage("There was a problem in detecting the pose from your character. Please draw again with a clear outline.")
                                .setPositiveButton("Retry", (dialogInterface, i) -> {
                                    title = titleEditText.getText().toString();
                                    description = descriptionEditText.getText().toString();
                                    if (title.isEmpty() || description.isEmpty()) {
                                        Toast.makeText(getApplicationContext(), ErrorMessage.EMPTY_TITLE_AND_DESCRIPTION, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    submitDrawing();
                                })
                                .setNegativeButton("Return to home", (dialogInterface, i) -> {
                                    finish();
                                })
                                .show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        abortDrawing();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 1, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            imageString = Base64.getEncoder().encodeToString(imageBytes);
        }
        return imageString;
    }

    private void abortDrawing() {
        drawingRepository.abortDrawing(drawingId, new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingIndicator.setVisibility(View.VISIBLE);
            playAudio();
        } else {
            loadingIndicator.setVisibility(View.GONE);
            pauseAudio();
        }
        finishButton.setEnabled(!isLoading);
        titleEditText.setEnabled(!isLoading);
        descriptionEditText.setEnabled(!isLoading);
    }

    private void playAudio() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void pauseAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
}