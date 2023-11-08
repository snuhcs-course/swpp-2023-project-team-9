package com.littlestudio.ui.drawing.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;

import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.repository.DrawingRepository;


import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrawView extends View {


    private DrawingRepository drawingRepository;

    private LinkedHashMap<MyPath, PaintOptions> mPaths = new LinkedHashMap<>();
    private LinkedHashMap<MyPath, PaintOptions> mLastPaths = new LinkedHashMap<>();
    private LinkedHashMap<MyPath, PaintOptions> mUndonePaths = new LinkedHashMap<>();

    private Paint mPaint = new Paint();
    private MyPath mPath = new MyPath();
    private PaintOptions mPaintOptions = new PaintOptions();

    private String invitationCode;

    private float mCurX = 0f;
    private float mCurY = 0f;
    private float mStartX = 0f;
    private float mStartY = 0f;
    private boolean mIsSaving = false;
    private boolean mIsStrokeWidthBarEnabled = false;

    private boolean isEraserOn = false;

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mPaint.setColor(mPaintOptions.getColor());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mPaintOptions.getStrokeWidth());
        mPaint.setAntiAlias(true);
    }

    public void undo() {
        if (!mPaths.isEmpty()) {
            MyPath lastKey = (MyPath) mPaths.keySet().toArray()[mPaths.size() - 1];
            PaintOptions lastValue = mPaths.get(lastKey);

            mPaths.remove(lastKey);

            if (lastKey != null && lastValue != null) {
                mUndonePaths.put(lastKey, lastValue);
            }
            invalidate();
        }
    }


    /*
    public void undo() {
        if (!mPaths.isEmpty() && mLastPaths.isEmpty()) {
            mPaths = (LinkedHashMap<MyPath, PaintOptions>) mLastPaths.clone();
            mLastPaths.clear();
            invalidate();
            return;
        }

        if (!mPaths.isEmpty()) {
            MyPath lastKey = (MyPath) mPaths.keySet().toArray()[mPaths.size() - 1];
            PaintOptions lastValue = mPaths.get(lastKey);

            mPaths.remove(lastKey);

            if (lastKey != null && lastValue != null) {
                mUndonePaths.put(lastKey, lastValue);
            }
            invalidate();
        }
    }
     */
    public void addRealTimeStroke(Stroke strokeData){
        PaintOptions paintOptions = new PaintOptions();
        paintOptions.setColor(strokeData.paint.color);
        paintOptions.setStrokeWidth(strokeData.paint.strokeWidth);
        paintOptions.setAlpha(strokeData.paint.alpha);

        MyPath myPath = new MyPath();
        // Iterate over the actions and perform them on the path
        for (Action action : strokeData.path.actions) {
            if (action instanceof Move) {
                Move move = (Move) action;
                myPath.moveTo(move.x, move.y);
            } else if (action instanceof Line) {
                Line line = (Line) action;
                myPath.lineTo(line.x, line.y);
            } else if (action instanceof Quad) {
                Quad quad = (Quad) action;
                myPath.quadTo(quad.x1, quad.y1, quad.x2, quad.y2);
            }
            // Add more types as needed
        }

        mPaths.put(myPath, paintOptions);
//        Log.d(myPath.toString(), paintOptions.toString());
//        Log.d("TETE", "path: " + myPath.toString());
//        Log.d("TETE", "path: " + myPath.actions.toString());
//        Log.d("TETE", "path: " + myPath.actions.get(0).toString());
//        Log.d("TETE", "path: " + myPath.getFillType());
//        Log.d("TETE", "path: " + myPath.isEmpty());
//        Log.d("TETE", "path: " + myPath.isConvex());
//        Log.d("TETE", "path: " + myPath.isInverseFillType());
//        Log.d("TETE", "mPaths: " + mPaths.toString());
        invalidate();
    }
    public void redo() {
        if (!mUndonePaths.isEmpty()) {
            MyPath lastKey = (MyPath) mUndonePaths.keySet().toArray()[mUndonePaths.size() - 1];
            PaintOptions lastValue = mUndonePaths.get(lastKey);

            mPaths.put(lastKey, lastValue);
            mUndonePaths.remove(lastKey);
            invalidate();
        }
    }

    public void setColor(@ColorInt int newColor) {
        int alphaColor = ColorUtils.setAlphaComponent(newColor, mPaintOptions.getAlpha());
        mPaintOptions.setColor(alphaColor);
        if (mIsStrokeWidthBarEnabled) {
            invalidate();
        }
    }


    public void setAlpha(int newAlpha) {
        int alpha = (newAlpha * 255) / 100;
        mPaintOptions.setAlpha(alpha);
        setColor(mPaintOptions.getColor());
    }

    public void setStrokeWidth(float newStrokeWidth) {
        mPaintOptions.setStrokeWidth(newStrokeWidth);
        if (mIsStrokeWidthBarEnabled) {
            invalidate();
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        mIsSaving = true;
        draw(canvas);
        mIsSaving = false;
        return bitmap;
    }

    public void addPath(MyPath path, PaintOptions options) {
        mPaths.put(path, options);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (MyPath key : mPaths.keySet()) {
            changePaint(mPaths.get(key));
            canvas.drawPath(key, mPaint);
        }

        changePaint(mPaintOptions);
        canvas.drawPath(mPath, mPaint);
    }



    public void setDrawingRepository(DrawingRepository drawingRepository) {
        this.drawingRepository = drawingRepository;
    }

    private void changePaint(PaintOptions paintOptions) {
        mPaint.setColor(paintOptions.isEraserOn() ? Color.WHITE : paintOptions.getColor());
        mPaint.setStrokeWidth(paintOptions.getStrokeWidth());
    }

    public void clearCanvas() {
        mLastPaths = (LinkedHashMap<MyPath, PaintOptions>) mPaths.clone();
        mPath.reset();
        mPaths.clear();
        invalidate();
    }

    private void actionDown(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mCurX = x;
        mCurY = y;
    }

    private void actionMove(float x, float y) {
        mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2);
        mCurX = x;
        mCurY = y;
    }

    private void actionUp() {
        mPath.lineTo(mCurX, mCurY);

        if (mStartX == mCurX && mStartY == mCurY) {
            mPath.lineTo(mCurX, mCurY + 2);
            mPath.lineTo(mCurX + 1, mCurY + 2);
            mPath.lineTo(mCurX + 1, mCurY);
        }

        mPaths.put(mPath, mPaintOptions);
        Stroke stroke = new Stroke(mPath, mPaintOptions);
        drawingRepository.realTimeDrawing(
                new DrawingRealTimeRequestDto(stroke, invitationCode),
                new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("TETE success", response.body().toString());
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Log.e("TETE error", t.toString());
                    }
                });
        mPath = new MyPath();
        mPaintOptions = new PaintOptions(mPaintOptions.getColor(), mPaintOptions.getStrokeWidth(), mPaintOptions.getAlpha(), mPaintOptions.isEraserOn());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                actionDown(x, y);
                mUndonePaths.clear();
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                actionUp();
                break;
        }

        invalidate();
        return true;
    }
    public void setInvitationCode(String invitationCode){
        this.invitationCode = invitationCode;
    }

    public void toggleEraser() {
        isEraserOn = !isEraserOn;
        mPaintOptions.setEraserOn(isEraserOn);
        invalidate();
    }

    public boolean isEraserOn() {
        return isEraserOn;
    }

}