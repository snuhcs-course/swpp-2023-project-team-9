package com.littlestudio.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.littlestudio.R;

public class TutorialActivity extends AppCompatActivity {
    int pageCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pageCounter = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        AppCompatButton hiddenBtn = (AppCompatButton) findViewById(R.id.hiddenBtn);
        AppCompatImageView tutorialImage = (AppCompatImageView) findViewById(R.id.imageView);
        hiddenBtn.setText(String.valueOf(pageCounter));

        hiddenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageCounter += 1;
                //String fileName = "Tutorial" + pagecounter + ".png";
                //int id = getResources().getIdentifier(fileName, "drawable", getPackageName());
                //tutorialImage.setImageResource(id);
                //is not efficient
                switch (pageCounter){
                    case 1: tutorialImage.setImageResource(R.drawable.tutorial1);
                    break;
                    case 2: tutorialImage.setImageResource(R.drawable.tutorial2);
                    break;
                    case 3: tutorialImage.setImageResource(R.drawable.tutorial3);
                    break;
                    case 4: tutorialImage.setImageResource(R.drawable.tutorial4);
                    break;
                    case 5: tutorialImage.setImageResource(R.drawable.tutorial5);
                    break;
                    case 6: tutorialImage.setImageResource(R.drawable.tutorial6);
                    break;
                    case 7: tutorialImage.setImageResource(R.drawable.tutorial7);
                    break;
                    case 8:
                        Intent intent = new Intent (TutorialActivity.this, MainActivity.class);
                        startActivity(intent);
                        pageCounter = 1;
                        break;
                    default:
                        break;
                }
                hiddenBtn.setText(String.valueOf(pageCounter));
            }
        });
        TextView backBtn = (TextView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageCounter > 1) pageCounter -= 1;
                switch (pageCounter){
                    case 1: tutorialImage.setImageResource(R.drawable.tutorial1);
                        break;
                    case 2: tutorialImage.setImageResource(R.drawable.tutorial2);
                        break;
                    case 3: tutorialImage.setImageResource(R.drawable.tutorial3);
                        break;
                    case 4: tutorialImage.setImageResource(R.drawable.tutorial4);
                        break;
                    case 5: tutorialImage.setImageResource(R.drawable.tutorial5);
                        break;
                    case 6: tutorialImage.setImageResource(R.drawable.tutorial6);
                        break;
                    case 7: tutorialImage.setImageResource(R.drawable.tutorial7);
                        break;
            }
            }

        });
        TextView skipBtn = (TextView) findViewById(R.id.skipBtn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (TutorialActivity.this, MainActivity.class);
                startActivity(intent);
                pageCounter = 1;
            }
        });

    }
// Set this to the number of files you want to process



}
