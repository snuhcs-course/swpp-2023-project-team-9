package com.littlestudio.ui.drawing;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.DrawAdapter;
import com.littlestudio.R;
import com.littlestudio.ui.MainActivity;
import com.littlestudio.ui.constant.IntentExtraKey;
import com.littlestudio.ui.drawing.widget.CircleView;
import com.littlestudio.ui.drawing.widget.DrawView;
import com.littlestudio.ui.drawing.widget.PaintOptions;
import com.littlestudio.ui.gallery.GalleryFragment;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrawingActivity extends AppCompatActivity {
    DrawingRepository drawingRepository;
    private static final int REQUEST_CODE_DRAW = 101;
    GalleryFragment galleryFragment;

    private Pusher pusher;

    private Channel channel;

    private String invitationCode;

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
            showSaveDialog(bitmap, byteArray);
        });


        findViewById(R.id.image_close_drawing).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Close Drawing")
                    .setMessage("Are you sure you want to close this drawing? Any unsaved changes will be lost.")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        finish();
                        Intent intent = new Intent(this, GalleryFragment.class);
                        startActivityForResult(intent, RESULT_CANCELED);
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        // Do nothing
                    })
                    .show();
        });


        findViewById(R.id.finish_btn).setOnClickListener(v -> {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            Bitmap bitmap = ((DrawView) findViewById(R.id.draw_view)).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] byteArray = bStream.toByteArray();
            showSaveDialog(bitmap, byteArray);

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
        this.invitationCode = getIntent().getStringExtra(IntentExtraKey.INVITATION_CODE);
        this.drawingId = Integer.parseInt(getIntent().getStringExtra(IntentExtraKey.DRAWING_CODE));

        connectToChannel(invitationCode);

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
    }

    private void showSaveDialog(Bitmap bitmap, byte[] byteArray) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save, null);
        alertDialog.setView(dialogView);
        EditText fileNameEditText = dialogView.findViewById(R.id.editText_file_name);
        EditText descriptionEditText = dialogView.findViewById(R.id.editText_description);
        String filename = UUID.randomUUID().toString();
        fileNameEditText.setSelectAllOnFocus(true);
        fileNameEditText.setText(filename);

        alertDialog.setTitle("Save Drawing")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    submitDrawing(bitmap, fileNameEditText.getText().toString(), descriptionEditText.getText().toString());
//                    Intent intent = new Intent(this, LoadingActivity.class);
//                    startActivityForResult(intent, RESULT_OK);
                    saveImage(bitmap, fileNameEditText.getText().toString(), byteArray);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivityForResult(intent, RESULT_OK);
                    finish();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // Do nothing
                });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void submitDrawing(Bitmap bitmap, String fileName, String description){
        String bitmapString = bitmapToString(bitmap);
        drawingRepository.submitDrawing(
                new DrawingSubmitRequestDto(bitmapString, fileName, description, drawingId),
                new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("TETE success", response.body().toString());
                        pusher.unsubscribe(invitationCode);
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.e("TETE error", t.toString());
                    }
                }
        );

    }
    private String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            imageString = Base64.getEncoder().encodeToString(imageBytes);
        }
        return imageString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_DRAW) {
                byte[] result = data.getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                showSaveDialog(bitmap, result);
            }
        }
    }

    private void saveImage(Bitmap bitmap, String fileName, byte[] byteArray) {
        String imageDir = Environment.DIRECTORY_PICTURES + "/Android Draw/";
        File path = Environment.getExternalStoragePublicDirectory(imageDir);
        path.mkdirs();
        File file = new File(path, fileName + ".png");
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            updateRecyclerView(Uri.fromFile(file));

            Intent intent = new Intent();
            intent.putExtra("uri", Uri.fromFile(file));
            setResult(RESULT_OK, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRecyclerView(Uri uri) {
        DrawAdapter adapter = new DrawAdapter(galleryFragment.getActivity(), galleryFragment.getFilesPath());
        adapter.addItem(uri.toString());
    }

    private void setUpDrawTools() {
        ((CircleView) findViewById(R.id.circle_view_opacity)).setCircleRadius(100f);
        View imageDrawEraser = findViewById(R.id.image_draw_eraser);
        View drawTools = findViewById(R.id.draw_tools);
        imageDrawEraser.setOnClickListener(v -> {
            ((DrawView) findViewById(R.id.draw_view)).toggleEraser();
            imageDrawEraser.setSelected(((DrawView) findViewById(R.id.draw_view)).isEraserOn());
            toggleDrawTools(drawTools, false);
        });
        imageDrawEraser.setOnLongClickListener(v -> {
            ((DrawView) findViewById(R.id.draw_view)).clearCanvas();
            toggleDrawTools(drawTools, false);
            return true;
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
        findViewById(R.id.image_draw_color).setOnClickListener(v -> {
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
        findViewById(R.id.image_draw_undo).setOnClickListener(v -> {
            ((DrawView) findViewById(R.id.draw_view)).undo();
            toggleDrawTools(drawTools, false);
        });
        findViewById(R.id.image_draw_redo).setOnClickListener(v -> {
            ((DrawView) findViewById(R.id.draw_view)).redo();
            toggleDrawTools(drawTools, false);
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