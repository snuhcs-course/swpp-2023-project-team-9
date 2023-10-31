package com.littlestudio.ui.drawing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.littlestudio.R;
import com.littlestudio.ui.constant.IntentExtraKey;

public class WaitingRoomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_DRAW = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        String invitationCode = getIntent().getStringExtra("invitationCode");
        TextView invitationCodeTextView = findViewById(R.id.invitation_code);
        invitationCodeTextView.setText(invitationCode);

        findViewById(R.id.copy_invitation_code).setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Invitation Code", invitationCode);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getApplicationContext(), "Invitation code copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        Button button = findViewById(R.id.start_drawing);
        button.setOnClickListener((v) -> {
            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
            startActivityForResult(intent, REQUEST_CODE_DRAW);
        });
    }
}