package com.littlestudio.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.littlestudio.DrawAdapter;
import com.littlestudio.R;
import com.littlestudio.ui.drawing.DrawingActivity;
import com.littlestudio.ui.gallery.GalleryFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    GalleryFragment galleryFragment = new GalleryFragment();
    MypageFragment mypageFragment = new MypageFragment();
    private final int REQUEST_CODE = 111;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    DrawAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.gallery);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            adapter = new DrawAdapter(this, galleryFragment.getFilesPath());
            RecyclerView rcv = (RecyclerView) galleryFragment.getView().findViewById(R.id.recycler_view);

            rcv.setAdapter(adapter);
            rcv.setLayoutManager(new LinearLayoutManager(this));
        }

        FloatingActionButton buttonView = findViewById(R.id.fab_add_draw);
        buttonView.setOnClickListener((view) -> {
            showStartDrawingModal();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == R.id.gallery) {
            switchFragment(galleryFragment);
            return true;
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

            if (selectedText.equals("Create a drawing")) {
                Intent intent = new Intent(this, DrawingActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            } else if (selectedText.equals("Join a drawing")) {
                Intent intent = new Intent(this, DrawingActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        builder.show();
    }
}