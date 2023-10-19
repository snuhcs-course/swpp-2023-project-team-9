package com.littlestudio;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    GalleryFragment galleryFragment = new GalleryFragment();
    MypageFragment mypageFragment = new MypageFragment();

    private final int REQUEST_CODE = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == R.id.gallery) {
            switchFragment(galleryFragment);
            return true;
        }
        if (itemId == R.id.draw) {
            showStartDrawingModal();
            return false;
        }
        if (itemId == R.id.mypage) {
            switchFragment(mypageFragment);
            return true;
        }
        return false;
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }

    private void showStartDrawingModal() {
        final List<String> ListItems = new ArrayList();
        ListItems.add("Create a drawing");
        ListItems.add("Join a drawing");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What would you like to do?");
        builder.setCancelable(true);
        builder.setItems(items, (DialogInterface dialog, int pos) -> {
            String selectedText = items[pos].toString();
            Toast.makeText(MainActivity.this, selectedText, Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                String uri = data.getStringExtra("uri");
                updateRecyclerView(uri);
            }
        }
    }

    private ArrayList<String> getFilesPath() {
        ArrayList<String> resultList = new ArrayList<>();
        String imageDir = Environment.DIRECTORY_PICTURES + "/Android Draw/";
        File path = Environment.getExternalStoragePublicDirectory(imageDir);
        path.mkdirs();
        File[] imageList = path.listFiles();
        if (imageList != null) {
            for (File imagePath : imageList) {
                resultList.add(imagePath.getAbsolutePath());
            }
        }
        return resultList;
    }

    private void updateRecyclerView(String uri) {

        adapter.addItem(uri);
    }

}