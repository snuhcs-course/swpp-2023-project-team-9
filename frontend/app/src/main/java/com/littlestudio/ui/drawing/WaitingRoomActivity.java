package com.littlestudio.ui.drawing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.dto.DrawingStartRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.repository.DrawingRepository;
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

    private int topMargin = 30;
    private LinearLayout container;
    private int drawingId;

    // TODO : make finish button and out.
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        connectToPusher();
        setContentView(R.layout.activity_waiting_room);
        container = (LinearLayout) findViewById(R.id.participants);

        drawingRepository = new DrawingRepository(
                new DrawingRemoteDataSource(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );

        String invitationCode = getIntent().getStringExtra("invitationCode");
        this.drawingId = getIntent().getIntExtra("drawingCode", 1);

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
            intent.putExtra(IntentExtraKey.DRAWING_CODE, drawingId);

            Log.d("help", "help");
            drawingRepository.startDrawing(new DrawingStartRequestDto(invitationCode), new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("good", "good");
                    pusher.unsubscribe(invitationCode);
                    startActivityForResult(intent, REQUEST_CODE_DRAW);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.e("error!!!!!!!!!!!!!", t.toString());
                }
            });
        });

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

    private void connectToChannel(String invitationCode){
        this.channel = this.pusher.subscribe(invitationCode);
        this.channel.bind("participant", new SubscriptionEventListener() {


            @Override
            public void onEvent(PusherEvent event) {
                try {
                    JSONObject data = new JSONObject(event.getData());
                    if (data.has("username") && data.has("type")){
                        String username = data.getString("username");
                        String type = data.getString("type");
                        if(type.equals("IN")){
                            TextView textView = new TextView(WaitingRoomActivity.this);
                            textView.setText(username);
                            textView.setTextSize(20);
                            textView.setTextColor(Color.BLACK);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.gravity = Gravity.CENTER;
                            lp.topMargin = topMargin += 30;


                            textView.setLayoutParams(lp);
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            container.addView(textView);
                                        }
                                    }
                            );
                            //부모 뷰에 추가

                        }else if(type.equals("OUT")){
                            Log.d("OUT", username + " OUT!");
                        }
                    }
                    if (data.has("start")){
                        Intent intent = new Intent(WaitingRoomActivity.this, DrawingActivity.class);
                        intent.putExtra(IntentExtraKey.INVITATION_CODE, invitationCode);
                        intent.putExtra(IntentExtraKey.DRAWING_CODE, drawingId);
                        Log.d("drawingCOde", String.valueOf(drawingId));
                        startActivityForResult(intent, REQUEST_CODE_DRAW);
                        pusher.unsubscribe(invitationCode);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.invitationCode = getIntent().getStringExtra(IntentExtraKey.INVITATION_CODE);
        this.isHost = getIntent().getBooleanExtra(IntentExtraKey.HOST_CODE, false);
        connectToChannel(invitationCode);
        Button button = findViewById(R.id.start_drawing);
        if(!isHost){
            Toast.makeText(WaitingRoomActivity.this, "Only Host can start drawing", Toast.LENGTH_SHORT).show();
            button.setVisibility(View.INVISIBLE);
        }
    }
}