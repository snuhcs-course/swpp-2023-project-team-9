package com.littlestudio.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.DrawingCreateRequest;
import com.littlestudio.data.model.DrawingCreateResponse;
import com.littlestudio.data.model.DrawingJoinRequest;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.ui.constant.IntentExtraKey;
import com.littlestudio.ui.drawing.WaitingRoomActivity;
import com.littlestudio.ui.gallery.GalleryFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    GalleryFragment galleryFragment = new GalleryFragment();
    MypageFragment mypageFragment = new MypageFragment();
    private final int REQUEST_CODE = 111;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    DrawingRepository drawingRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.gallery);

        FloatingActionButton buttonView = findViewById(R.id.fab_add_draw);
        buttonView.setOnClickListener((view) -> {
            showStartDrawingModal();
        });

        drawingRepository = new DrawingRepository(
                new DrawingRemoteDataSource(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );
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
        final List<String> startDrawingOptions = new ArrayList<String>() {{
            add(StartDrawingOptions.CREATE);
            add(StartDrawingOptions.JOIN);
        }};

        final CharSequence[] optionItems = startDrawingOptions.toArray(new String[startDrawingOptions.size()]);

        new AlertDialog.Builder(this)
                .setTitle("What would you like to do?")
                .setItems(optionItems, (DialogInterface dialog, int pos) -> {
                    String selectedOption = optionItems[pos].toString();
                    switch (selectedOption) {
                        case StartDrawingOptions.CREATE:
                            Intent intent = new Intent(this, WaitingRoomActivity.class);
                            drawingRepository.createDrawing(new DrawingCreateRequest(1), new Callback<DrawingCreateResponse>() {
                                @Override
                                public void onResponse(Call<DrawingCreateResponse> call, Response<DrawingCreateResponse> response) {
                                    String invitationCode = response.body().invitation_code;
                                    int id = response.body().id;
                                    intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                                    intent.putExtra(IntentExtraKey.DRAWING_CODE, id);
                                    intent.putExtra(IntentExtraKey.HOST_CODE, true);
                                    startActivityForResult(intent, REQUEST_CODE);
                                }

                                @Override
                                public void onFailure(Call<DrawingCreateResponse> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case StartDrawingOptions.JOIN:
                            showInvitationCodeInputDialog();
                            break;
                    }
                })
                .setNegativeButton("Cancel", (dialog, _which) -> dialog.dismiss())
                .show();
    }

    private void showInvitationCodeInputDialog() {
        final EditText invitationCodeInput = new EditText(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter Invitation Code")
                .setView(invitationCodeInput)
                .create();

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Join", (_dialog, _which) -> {});
        dialog.show();

        Button joinButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        joinButton.setOnClickListener(view -> {
            String invitationCode = invitationCodeInput.getText().toString();
            if (invitationCode.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter invitation code.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, WaitingRoomActivity.class);
            // TODO : change userId to real id.
            drawingRepository.joinDrawing(new DrawingJoinRequest(1, invitationCode), new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                    intent.putExtra(IntentExtraKey.HOST_CODE, false);
                    startActivityForResult(intent, REQUEST_CODE);
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Invalid Invitation Code", Toast.LENGTH_SHORT).show();
                    Log.e("why error?", t.toString());


                }
            });
        });
    }
}

class StartDrawingOptions {
    public final static String CREATE = "Create a drawing";
    public final static String JOIN = "Join a drawing";
}
