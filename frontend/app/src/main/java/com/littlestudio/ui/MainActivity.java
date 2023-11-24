package com.littlestudio.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.dto.DrawingJoinResponseDto;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.User;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.data.repository.UserRepository;
import com.littlestudio.ui.constant.IntentExtraKey;
import com.littlestudio.ui.constant.StartDrawingOptions;
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
    UserRepository userRepository;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.gallery);

//        FloatingActionButton buttonView = findViewById(R.id.fab_add_draw);
//        buttonView.setOnClickListener((view) -> {
//            showStartDrawingModal();
//        });

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        Menu menu = navigation.getMenu();
        MenuItem drawItem = menu.findItem(R.id.draw);
        drawItem.setOnMenuItemClickListener((item) -> {
            showStartDrawingModal();
            return true;
        });

        drawingRepository = DrawingRepository.getInstance(
                DrawingRemoteDataSource.getInstance(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );

        userRepository = UserRepository.getInstance(
                UserRemoteDataSource.getInstance(),
                UserLocalDataSource.getInstance(getApplicationContext())
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userRepository.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            user = userRepository.getUser();
        }
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
        View customView = getLayoutInflater().inflate(R.layout.modal_start_drawing, null);

        AppCompatButton createDrawingButton = customView.findViewById(R.id.create_drawing_btn);
        AppCompatButton joinDrawingButton = customView.findViewById(R.id.join_drawing_btn);
        AppCompatButton cancelButton = customView.findViewById(R.id.cancel_start_drawing_button);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(customView)
                //.setNegativeButton("Cancel", (dialog, _which) -> dialog.dismiss())
                .show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        createDrawingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WaitingRoomActivity.class);
            drawingRepository.createDrawing(new DrawingCreateRequestDto(user.id), new Callback<DrawingCreateResponseDto>() {
                @Override
                public void onResponse(Call<DrawingCreateResponseDto> call, Response<DrawingCreateResponseDto> response) {
                    String invitationCode = response.body().invitation_code;
                    int id = response.body().id;
                    intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                    intent.putExtra(IntentExtraKey.DRAWING_ID, id);
                    intent.putExtra(IntentExtraKey.HOST_CODE, true);
                    startActivityForResult(intent, REQUEST_CODE);
                }

                @Override
                public void onFailure(Call<DrawingCreateResponseDto> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        joinDrawingButton.setOnClickListener(view -> {
            showInvitationCodeInputDialog();
        });

        cancelButton.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
    }

    private void showInvitationCodeInputDialog() {
        View customView = getLayoutInflater().inflate(R.layout.modal_input_invitation_code, null);

        EditText invitationCodeInput = customView.findViewById(R.id.editText_invitation_code);
        AppCompatButton cancelButton = customView.findViewById(R.id.cancel_join_drawing_button);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(customView)
                .create();

        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button joinButton = customView.findViewById(R.id.join_btn);
        joinButton.setOnClickListener(view -> {
            String invitationCode = invitationCodeInput.getText().toString();
            if (invitationCode.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter an invitation code.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, WaitingRoomActivity.class);
            drawingRepository.joinDrawing(new DrawingJoinRequestDto(user.id, invitationCode), new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                    intent.putExtra(IntentExtraKey.HOST_CODE, false);
                    startActivityForResult(intent, REQUEST_CODE);
                    alertDialog.dismiss();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Invalid Invitation Code", Toast.LENGTH_SHORT).show();
                    Log.e("why error?", t.toString());
                }
            });
        });

        cancelButton.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
    }
    /*
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
                            drawingRepository.createDrawing(new DrawingCreateRequestDto(user.id), new Callback<DrawingCreateResponseDto>() {
                                @Override
                                public void onResponse(Call<DrawingCreateResponseDto> call, Response<DrawingCreateResponseDto> response) {
                                    String invitationCode = response.body().invitation_code;
                                    int id = response.body().id;
                                    ArrayList<String> participants = new ArrayList<>();
                                    // TODO: change username to localDataSource's username
                                    participants.add("username");
                                    intent.putStringArrayListExtra(IntentExtraKey.PARTICIPANTS, participants);
                                    intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                                    intent.putExtra(IntentExtraKey.DRAWING_ID, id);
                                    intent.putExtra(IntentExtraKey.HOST_CODE, true);
                                    startActivityForResult(intent, REQUEST_CODE);
                                }

                                @Override
                                public void onFailure(Call<DrawingCreateResponseDto> call, Throwable t) {
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
            drawingRepository.joinDrawing(new DrawingJoinRequestDto(user.id, invitationCode), new Callback<DrawingJoinResponseDto>() {
                @Override
                public void onResponse(Call<DrawingJoinResponseDto> call, Response<DrawingJoinResponseDto> response) {
                    ArrayList<String> participants = response.body().participants;
                    Log.d("participant in MainActivity", participants.toString());
                    intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                    intent.putExtra(IntentExtraKey.HOST_CODE, false);
                    intent.putStringArrayListExtra(IntentExtraKey.PARTICIPANTS, participants);
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
     */
}