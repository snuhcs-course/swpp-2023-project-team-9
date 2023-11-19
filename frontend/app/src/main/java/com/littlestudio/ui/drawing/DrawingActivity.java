package com.littlestudio.ui.drawing;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.ui.ImageActivity;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.ui.constant.IntentExtraKey;
import com.littlestudio.ui.drawing.widget.CircleView;
import com.littlestudio.ui.drawing.widget.DrawView;
import com.littlestudio.ui.drawing.widget.PaintOptions;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class DrawingActivity extends AppCompatActivity {
    DrawingRepository drawingRepository;

    private Pusher pusher;

    private Channel channel;

    private String invitationCode;

    private boolean isHost;

    private int drawingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.littlestudio.R.layout.activity_drawing);

        ObjectMapper mapper = new ObjectMapper();
        FamilyMapper familyMapper = new FamilyMapper(mapper);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        drawingRepository = new DrawingRepository(new DrawingRemoteDataSource(), new DrawingMapper(mapper, new FamilyMapper(mapper)));
        ((DrawView) findViewById(R.id.draw_view)).setDrawingRepository(drawingRepository);

        findViewById(R.id.finish_btn).setOnClickListener(v -> {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            Bitmap bitmap = ((DrawView) findViewById(R.id.draw_view)).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] byteArray = bStream.toByteArray();
            Intent intent = new Intent(this, SubmitActivity.class);
            intent.putExtra(IntentExtraKey.DRAWING_IMAGE_BYTE_ARRAY, byteArray);
            intent.putExtra(IntentExtraKey.DRAWING_ID, getIntent().getIntExtra(IntentExtraKey.DRAWING_ID, 0));
            startActivity(intent);
            pusher.unsubscribe(invitationCode);
            finish();
        });


        findViewById(R.id.image_close_drawing).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Close Drawing")
                    .setMessage("Are you sure you want to close this drawing? Any unsaved changes will be lost.")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        finish();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        // Do nothing
                    })
                    .show();
        });

        setUpDrawTools();
        colorSelector();
        setPaintAlpha();
        setPaintWidth();
        connectToPusher();
    }

    @Override
    protected void onStart(){
        super.onStart();
        this.invitationCode = getIntent().getStringExtra("invitationCode");
        this.isHost = getIntent().getBooleanExtra(IntentExtraKey.HOST_CODE, false);
        this.drawingId = getIntent().getIntExtra(IntentExtraKey.DRAWING_ID, 0);
        connectToChannel(invitationCode);
        if(!isHost){
            Button button = findViewById(R.id.finish_btn);
            button.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        pusher.unsubscribe(invitationCode);
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

        final HashMap<String, HashMap<String, HashMap<Integer, String>>> events = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        DrawingMapper drawingMapper = new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()));
        ((DrawView) findViewById(R.id.draw_view)).setInvitationCode(invitationCode);
        this.channel.bind("new-stroke", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.i("Pusher", "Received event with data: " + event.toString());
                DrawingRealTimeRequestDto stroke = null;
                try {
                    stroke = drawingMapper.mapToStroke(event.getData());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                PaintOptions paint = stroke.stroke_data.paint;
                ((DrawView) findViewById(R.id.draw_view)).addRealTimeStroke(stroke.stroke_data);
            }
        });

        this.channel.bind("chunked-new-stroke", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.i("Pusher", "Received chunked event with data: " + event.toString());
                try {
                    JSONObject data = new JSONObject(event.getData());
                    String id = data.getString("id");
                    String stroke_id = data.getString("stroke_id");
                    int index = data.getInt("index");
                    String chunk = data.getString("chunk");
                    boolean isFinal = data.getBoolean("final");

                    if (!events.containsKey(id)) {
                        events.put(id, new HashMap<>());
                    }

                    if (!events.get(id).containsKey(stroke_id)) {
                        events.get(id).put(stroke_id, new HashMap<>());
                    }

                    HashMap<Integer, String> eventChunks = events.get(id).get(stroke_id);
                    eventChunks.put(index, chunk);

                    if (isFinal) {
                        StringBuilder fullData = new StringBuilder();
                        for (int i = 0; i < eventChunks.size(); i++) {
                            fullData.append(eventChunks.get(i));
                        }

                        String fullDataString = fullData.toString();
                        DrawingRealTimeRequestDto stroke = drawingMapper.mapToStroke(fullDataString);
                        PaintOptions paint = stroke.stroke_data.paint;
                        ((DrawView) findViewById(R.id.draw_view)).addRealTimeStroke(stroke.stroke_data);

                        events.get(id).remove(stroke_id);
                    }
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        if(!isHost){
            this.channel.bind("finish", new SubscriptionEventListener() {
                @Override
                public void onEvent(PusherEvent event) {
                    Log.i("Pusher", "finished! " + event.toString());
                    try {
                        JSONObject data = new JSONObject(event.getData());
                        String drawingImageUrl = data.getString("image_url");
                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                        intent.putExtra(IntentExtraKey.DRAWING_ID, drawingId);
                        intent.putExtra(IntentExtraKey.DRAWING_IMAGE_URL, drawingImageUrl);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }
    }

    private void setUpDrawTools() {
        ((CircleView) findViewById(R.id.circle_view_opacity)).setCircleRadius(100f);
        View imageDrawEraser = findViewById(R.id.image_draw_eraser);
        View imageDrawColor = findViewById(R.id.image_draw_color);
        View drawTools = findViewById(R.id.draw_tools);
        imageDrawEraser.setOnClickListener(v -> {
            ((DrawView) findViewById(R.id.draw_view)).setEraserOn();
            imageDrawEraser.setSelected(true);
            imageDrawColor.setSelected(false);
            toggleDrawTools(drawTools, false);
        });
        findViewById(R.id.image_draw_width).setOnClickListener(v -> {
            if (drawTools.getTranslationY() == toPx(56)) {
                toggleDrawTools(drawTools, true);
            } else if (drawTools.getTranslationY() == toPx(0) && findViewById(R.id.seekBar_width).getVisibility() == View.VISIBLE) {
                toggleDrawTools(drawTools, false);
            }
            findViewById(R.id.circle_view_width).setVisibility(View.VISIBLE);
            findViewById(R.id.circle_view_opacity).setVisibility(View.GONE);
            findViewById(R.id.seekBar_width).setVisibility(View.VISIBLE);
            findViewById(R.id.seekBar_opacity).setVisibility(View.GONE);
            findViewById(R.id.draw_color_palette).setVisibility(View.GONE);
        });
        imageDrawColor.setOnClickListener(v -> {
            ((DrawView) findViewById(R.id.draw_view)).setEraserOff();
            imageDrawEraser.setSelected(false);
            imageDrawColor.setSelected(true);
            if (drawTools.getTranslationY() == toPx(56)) {
                toggleDrawTools(drawTools, true);
            } else if (drawTools.getTranslationY() == toPx(0) && findViewById(R.id.draw_color_palette).getVisibility() == View.VISIBLE) {
                toggleDrawTools(drawTools, false);
            }
            findViewById(R.id.circle_view_width).setVisibility(View.GONE);
            findViewById(R.id.circle_view_opacity).setVisibility(View.GONE);
            findViewById(R.id.seekBar_width).setVisibility(View.GONE);
            findViewById(R.id.seekBar_opacity).setVisibility(View.GONE);
            findViewById(R.id.draw_color_palette).setVisibility(View.VISIBLE);
        });
    }

    private void toggleDrawTools(View view, boolean showView) {
        view.animate().translationY(showView ? toPx(0) : toPx(56));
    }

    private void colorSelector() {
        imageColorSelector(R.id.image_color_black, R.color.color_black);
        imageColorSelector(R.id.image_color_red, R.color.color_red);
        imageColorSelector(R.id.image_color_yellow, R.color.color_yellow);
        imageColorSelector(R.id.image_color_green, R.color.color_green);
        imageColorSelector(R.id.image_color_blue, R.color.color_blue);
        imageColorSelector(R.id.image_color_pink, R.color.color_pink);
        imageColorSelector(R.id.image_color_brown, R.color.color_brown);
    }

    private void imageColorSelector(int imageViewId, int colorResourceId) {
        findViewById(imageViewId).setOnClickListener(v -> {
            int color = ResourcesCompat.getColor(getResources(), colorResourceId, null);
            ((DrawView) findViewById(R.id.draw_view)).setColor(color);
            ((CircleView) findViewById(R.id.circle_view_opacity)).setColor(color);
            ((CircleView) findViewById(R.id.circle_view_width)).setColor(color);
            scaleColorView(findViewById(imageViewId));
        });
    }

    private void scaleColorView(View view) {
        // Reset scale of all views
        findViewById(R.id.image_color_black).setScaleX(1f);
        findViewById(R.id.image_color_black).setScaleY(1f);

        findViewById(R.id.image_color_red).setScaleX(1f);
        findViewById(R.id.image_color_red).setScaleY(1f);

        findViewById(R.id.image_color_yellow).setScaleX(1f);
        findViewById(R.id.image_color_yellow).setScaleY(1f);

        findViewById(R.id.image_color_green).setScaleX(1f);
        findViewById(R.id.image_color_green).setScaleY(1f);

        findViewById(R.id.image_color_blue).setScaleX(1f);
        findViewById(R.id.image_color_blue).setScaleY(1f);

        findViewById(R.id.image_color_pink).setScaleX(1f);
        findViewById(R.id.image_color_pink).setScaleY(1f);

        findViewById(R.id.image_color_brown).setScaleX(1f);
        findViewById(R.id.image_color_brown).setScaleY(1f);

        // Set scale of the selected view
        view.setScaleX(1.5f);
        view.setScaleY(1.5f);
    }

    private void setPaintWidth() {
        ((SeekBar) findViewById(R.id.seekBar_width)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((DrawView) findViewById(R.id.draw_view)).setStrokeWidth((float) progress);
                ((CircleView) findViewById(R.id.circle_view_width)).setCircleRadius((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setPaintAlpha() {
        ((SeekBar) findViewById(R.id.seekBar_opacity)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((DrawView) findViewById(R.id.draw_view)).setAlpha(progress);
                ((CircleView) findViewById(R.id.circle_view_opacity)).setAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private float toPx(int value) {
        return value * Resources.getSystem().getDisplayMetrics().density;
    }
}