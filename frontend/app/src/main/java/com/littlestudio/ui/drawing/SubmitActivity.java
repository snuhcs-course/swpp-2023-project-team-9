package com.littlestudio.ui.drawing;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.ui.ImageActivity;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.ui.constant.IntentExtraKey;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SubmitActivity extends AppCompatActivity {
    DrawingRepository drawingRepository;
    AppCompatButton finishButton;
    ProgressBar loadingIndicator;
    EditText titleEditText;
    EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        drawingRepository = DrawingRepository.getInstance(
                DrawingRemoteDataSource.getInstance(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );

        byte[] imageByteArray = getIntent().getByteArrayExtra(IntentExtraKey.DRAWING_IMAGE_BYTE_ARRAY);
        int drawingId = getIntent().getIntExtra(IntentExtraKey.DRAWING_ID, 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

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
                        String title = titleEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();

                        if (title.isEmpty() || description.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please input title and description.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        setLoading(true);
                        drawingRepository.submitDrawing(new DrawingSubmitRequestDto(
                                bitmapToString(bitmap),
                                title,
                                description,
                                drawingId
                        ), new Callback<Drawing>() {
                            @Override
                            public void onResponse(Call<Drawing> call, Response<Drawing> response) {
                                setLoading(false);
                                String drawingImageUrl = response.body().image_url;
                                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                                intent.putExtra(IntentExtraKey.DRAWING_ID, drawingId);
                                intent.putExtra(IntentExtraKey.DRAWING_IMAGE_URL, drawingImageUrl);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(Call call, Throwable t) {
                                setLoading(false);
                                Log.d("test", t.toString());
                                Toast.makeText(getApplicationContext(), "Failed to submit drawing. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        // Do nothing
                    })
                    .show();
        });
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

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingIndicator.setVisibility(View.VISIBLE);
        } else {
            loadingIndicator.setVisibility(View.GONE);
        }
        finishButton.setEnabled(!isLoading);
        titleEditText.setEnabled(!isLoading);
        descriptionEditText.setEnabled(!isLoading);
    }
}