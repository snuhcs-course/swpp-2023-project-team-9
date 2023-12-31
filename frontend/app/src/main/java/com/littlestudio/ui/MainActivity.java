package com.littlestudio.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingJoinResponseDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.User;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.data.repository.UserRepository;
import com.littlestudio.ui.constant.ErrorMessage;
import com.littlestudio.ui.constant.IntentExtraKey;
import com.littlestudio.ui.constant.RequestCode;
import com.littlestudio.ui.drawing.WaitingRoomActivity;
import com.littlestudio.ui.gallery.GalleryFragment;

import java.util.ArrayList;

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
            startActivityForResult(intent, RequestCode.LOGIN);
        } else {
            user = userRepository.getUser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode.LOGIN && resultCode == Activity.RESULT_OK) {
            switchFragment(galleryFragment);
            startActivity(new Intent(this, TutorialActivity.class));
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
                .show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        createDrawingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WaitingRoomActivity.class);
            drawingRepository.createDrawing(new DrawingCreateRequestDto(user.id), new Callback<DrawingCreateResponseDto>() {
                @Override
                public void onResponse(Call<DrawingCreateResponseDto> call, Response<DrawingCreateResponseDto> response) {
                    String invitationCode = response.body().invitation_code;
                    int id = response.body().id;
                    ArrayList<String> participants = new ArrayList<>();
                    participants.add(user.full_name);
                    intent.putStringArrayListExtra(IntentExtraKey.PARTICIPANTS, participants);
                    intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                    intent.putExtra(IntentExtraKey.DRAWING_ID, id);
                    intent.putExtra(IntentExtraKey.HOST_CODE, true);
                    startActivityForResult(intent, REQUEST_CODE);
                    alertDialog.dismiss();
                }

                @Override
                public void onFailure(Call<DrawingCreateResponseDto> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), ErrorMessage.DEFAULT, Toast.LENGTH_SHORT).show();
                }
            });
        });

        joinDrawingButton.setOnClickListener(view -> {
            showInvitationCodeInputDialog();
            alertDialog.dismiss();
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
                Toast.makeText(MainActivity.this, ErrorMessage.EMPTY_INVITATION_CODE, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, WaitingRoomActivity.class);
            drawingRepository.joinDrawing(new DrawingJoinRequestDto(user.id, invitationCode), new Callback<DrawingJoinResponseDto>() {
                @Override
                public void onResponse(Call<DrawingJoinResponseDto> call, Response<DrawingJoinResponseDto> response) {
                    ArrayList<String> participants = response.body().participants;
                    int drawingId = response.body().drawing_id;
                    intent.putExtra(IntentExtraKey.PARTICIPANTS, participants);
                    intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                    intent.putExtra(IntentExtraKey.HOST_CODE, false);
                    intent.putExtra(IntentExtraKey.DRAWING_ID, drawingId);
                    startActivityForResult(intent, REQUEST_CODE);
                    alertDialog.dismiss();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(MainActivity.this, ErrorMessage.WRONG_INVITATION_CODE, Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelButton.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
    }
}