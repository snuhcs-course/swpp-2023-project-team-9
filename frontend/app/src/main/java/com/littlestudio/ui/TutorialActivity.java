package com.littlestudio.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.littlestudio.R;

import pl.droidsonroids.gif.GifImageView;

public class TutorialActivity extends AppCompatActivity {
    private int pageCounter;
    private int selectedImageViewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        initializeUI();
    }

    private void initializeUI() {
        pageCounter = 1;
        updateUIForPage(pageCounter);
        TextView backBtn = findViewById(R.id.back_btn);
        backBtn.setVisibility(View.GONE);
    }

    private void updateUIForPage(int page) {
        AppCompatImageView tutorialImage = findViewById(R.id.imageView);
        tutorialImage.setAlpha(1f);
        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        AppCompatButton nextBtn = findViewById(R.id.next_btn);
        TextView backBtn = findViewById(R.id.back_btn);
        TextView skipBtn = findViewById(R.id.skip_btn);
        ProgressBar loading = findViewById(R.id.loading);

        loading.setVisibility(View.GONE);
        skipBtn.setOnClickListener(v -> finish());
        nextBtn.setOnClickListener(v -> handleNextButtonClick());
        backBtn.setOnClickListener(v -> handleBackButtonClick());

        switch (page) {
            case 1:
                tutorialImage.setImageResource(R.drawable.start);
                title.setText("Welcome to Little Studio!");
                description.setText("Let's bring the character you drew come to life!");
                nextBtn.setText("Let's go!");
                break;
            case 2:
                tutorialImage.setImageResource(R.drawable.tutorial_2);
                title.setText("Step 1");
                description.setText("Click on the '+' button to create a drawing");
                nextBtn.setText("Next");
                break;
            case 3:
                tutorialImage.setImageResource(R.drawable.tutorial_3);
                title.setText("Step 2");
                description.setText("Click on 'Create a drawing'");
                nextBtn.setText("Next");
                break;
            case 4:
                tutorialImage.setImageResource(R.drawable.tutorial_4);
                title.setText("Step 3");
                description.setText("Share the invitation code to draw together with your family!");
                nextBtn.setText("Next");
                break;
            case 5:
                tutorialImage.setImageResource(R.drawable.tutorial_5);
                title.setText("Step 4");
                description.setText("Draw your character!");
                nextBtn.setText("Next");
                break;
            case 6:
                tutorialImage.setImageResource(R.drawable.tutorial_6);
                title.setText("");
                String outlineDescription = "There should be clear outlines, with no gaps in between";
                String[] outlineBoldWords = {"clear outlines", "no gaps"};
                setStyledText(description, outlineDescription, outlineBoldWords, R.color.orange);
                nextBtn.setText("Next");
                break;
            case 7:
                tutorialImage.setImageResource(R.drawable.tutorial_7);
                title.setText("");
                String armsAndLegsDescription = "Your character must have two arms and two legs";
                String[] armsAndLegsBoldWords = {"two arms", "two legs"};
                setStyledText(description, armsAndLegsDescription, armsAndLegsBoldWords, R.color.orange);
                nextBtn.setText("Next");
                break;
            case 8:
                tutorialImage.setImageResource(R.drawable.tutorial_8);
                title.setText("Submit your drawing!");
                description.setText("Give your character a name and describe it!");
                nextBtn.setText("Next");
                break;
            case 9:
                tutorialImage.setImageResource(R.drawable.tutorial_7);
                tutorialImage.setAlpha(0.3f);
                ProgressBar loadingIndicator = findViewById(R.id.loading);
                loadingIndicator.setVisibility(View.VISIBLE);
                title.setText("Wait for a few minutes");
                description.setText("Wait until your character is learning some moves!");
                nextBtn.setText("Next");
                break;
            case 10:
                initializeLastPage();
                break;
            default:
                break;
        }
    }

    private void initializeLastPage() {
        setContentView(R.layout.activity_tutorial_last_page);
        AppCompatButton startBtn = findViewById(R.id.start_btn);
        TextView backBtnLastPage = findViewById(R.id.back_btn);
        ImageView imageView = findViewById(R.id.image_view);

        ImageView originalImageView = findViewById(R.id.original_image_view);
        GifImageView dabImageView = findViewById(R.id.dab_image_view);
        GifImageView jumpingImageView = findViewById(R.id.jumping_image_view);
        GifImageView zombieImageView = findViewById(R.id.zombie_image_view);

        selectedImageViewId = dabImageView.getId();
        setForeground(selectedImageViewId, R.drawable.orange_border_thin);

        String originalDrawing = "tutorial_7";
        String dabGif = "dab";
        String jumpingGif = "jumping";
        String zombieGif = "zombie";

        int original = getResources().getIdentifier(originalDrawing, "drawable", getPackageName());
        int dab = getResources().getIdentifier(dabGif, "drawable", getPackageName());
        int jumping = getResources().getIdentifier(jumpingGif, "drawable", getPackageName());
        int zombie = getResources().getIdentifier(zombieGif, "drawable", getPackageName());

        originalImageView.setOnClickListener(v -> {
            onImageViewClicked(originalImageView.getId());
            Glide.with(this).load(original).into(imageView);
        });

        dabImageView.setOnClickListener(v -> {
            onImageViewClicked(dabImageView.getId());
            Glide.with(getApplication()).load(dab).into(imageView);
        });

        jumpingImageView.setOnClickListener(v -> {
            onImageViewClicked(jumpingImageView.getId());
            Glide.with(getApplication()).load(jumping).into(imageView);
        });

        zombieImageView.setOnClickListener(v -> {
            onImageViewClicked(zombieImageView.getId());
            Glide.with(getApplication()).load(zombie).into(imageView);
        });

        backBtnLastPage.setOnClickListener(v -> lastPageBackButtonClick());
        startBtn.setOnClickListener(v -> finish());
    }


    private void lastPageBackButtonClick() {
        setContentView(R.layout.activity_tutorial);
        pageCounter = 9;
        updateUIForPage(9);
    }

    private void handleNextButtonClick() {
        if (pageCounter < 10) {
            pageCounter += 1;
            updateUIForPage(pageCounter);
            if (pageCounter > 1) {
                findViewById(R.id.back_btn).setVisibility(View.VISIBLE);
            }
        } else {
            finish();
        }
    }

    private void handleBackButtonClick() {
        if (pageCounter > 1) {
            pageCounter -= 1;
            updateUIForPage(pageCounter);
            if (pageCounter == 1) {
                findViewById(R.id.back_btn).setVisibility(View.GONE);
            }
        }
    }

    private void setStyledText(TextView textView, String text, String[] boldWords, int color) {
        SpannableString spannableString = new SpannableString(text);

        for (String boldWord : boldWords) {
            int startIndex = text.indexOf(boldWord);
            int endIndex = startIndex + boldWord.length();
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(TutorialActivity.this, color)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(spannableString);
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