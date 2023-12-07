package com.littlestudio.ui.drawing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.dto.DrawingStartRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.ui.JoinAdapter;
import com.littlestudio.ui.constant.ErrorMessage;
import com.littlestudio.ui.constant.IntentExtraKey;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitingRoomActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_DRAW = 101;
    private DrawingRepository drawingRepository;
    private boolean isHost;
    private String invitationCode;
    private Pusher pusher;
    private Channel channel;
    private int drawingId;
    private RecyclerView waitRecycleView;
    private JoinAdapter joinAdapter;
    private ArrayList<String> participants;

    // TODO : make finish button and out.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectToPusher();
        setContentView(R.layout.activity_waiting_room);

        drawingRepository = DrawingRepository.getInstance(
                DrawingRemoteDataSource.getInstance(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );

        String invitationCode = getIntent().getStringExtra(IntentExtraKey.INVITATION_CODE);
        drawingId = getIntent().getIntExtra(IntentExtraKey.DRAWING_ID, 0);
        TextView invitationCodeTextView = findViewById(R.id.invitation_code);
        invitationCodeTextView.setText(invitationCode);

        findViewById(R.id.copy_invitation_code).setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Invitation Code", invitationCode);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getApplicationContext(), "Invitation code copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        Button button = findViewById(R.id.start_drawing_btn);

        button.setOnClickListener((v) -> {

            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
            intent.putExtra(IntentExtraKey.DRAWING_ID, drawingId);
            intent.putExtra(IntentExtraKey.HOST_CODE, isHost);

            drawingRepository.startDrawing(new DrawingStartRequestDto(invitationCode), new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    startActivityForResult(intent, REQUEST_CODE_DRAW);
                    finish();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(getApplicationContext(), ErrorMessage.DEFAULT, Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.waitRecycleView = findViewById(R.id.wait_recycler_view);
        this.invitationCode = getIntent().getStringExtra(IntentExtraKey.INVITATION_CODE);
        this.isHost = getIntent().getBooleanExtra(IntentExtraKey.HOST_CODE, false);
        this.participants = getIntent().getStringArrayListExtra(IntentExtraKey.PARTICIPANTS);

        this.joinAdapter = new JoinAdapter(getApplicationContext(), participants);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        this.waitRecycleView.setLayoutManager(linearLayoutManager);
        this.waitRecycleView.setAdapter(joinAdapter);
        connectToChannel(invitationCode);
        if (!isHost) {
            Button button = findViewById(R.id.start_drawing_btn);
            Toast.makeText(WaitingRoomActivity.this, "Only host can start drawing.", Toast.LENGTH_SHORT).show();
            button.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pusher.unsubscribe(invitationCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isHost) {
            drawingRepository.abortDrawing(drawingId, new Callback() {
                @Override
                public void onResponse(Call call, Response response) {}

                @Override
                public void onFailure(Call call, Throwable t) {}
            });
        }
    }

    private void connectToPusher() {
        PusherOptions options = new PusherOptions();
        options.setCluster("ap3");
        Pusher pusherInstance = new Pusher("48e0ed2d6758286a8441", options);
        this.pusher = pusherInstance;
        this.pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.i("Pusher", "There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);
    }

    private void connectToChannel(String invitationCode) {
        this.channel = this.pusher.subscribe(invitationCode);
        this.channel.bind("participant", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                try {
                    JSONObject data = new JSONObject(event.getData());
                    if (data.has("full_name") && data.has("type")) {
                        String full_name = data.getString("full_name");
                        String type = data.getString("type");
                        if (type.equals("IN")) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            participants.add(full_name);
                                            joinAdapter.setParticipants(participants);
                                            waitRecycleView.setAdapter(joinAdapter);
                                            joinAdapter.notifyDataSetChanged();
                                        }
                                    }
                            );
                            //부모 뷰에 추가

                        } else if (type.equals("OUT")) {

                        }
                    }
                    if (data.has("start") && !isHost) {
                        Intent intent = new Intent(WaitingRoomActivity.this, DrawingActivity.class);
                        intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                        intent.putExtra(IntentExtraKey.DRAWING_ID, drawingId);
                        intent.putExtra(IntentExtraKey.HOST_CODE, isHost);
                        Log.d("drawingCOde", String.valueOf(drawingId));
                        startActivityForResult(intent, REQUEST_CODE_DRAW);
                        finish();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        if (!isHost) {
            this.channel.bind("abort", new SubscriptionEventListener() {
                @Override
                public void onEvent(PusherEvent event) {
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplication(), ErrorMessage.DRAWING_ABORTED, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                    );
                }
            });
        }
    }
}