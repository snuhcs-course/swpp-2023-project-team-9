package com.littlestudio.data.datasource;

import android.util.Log;

import com.littlestudio.data.api.ServiceApi;
import com.littlestudio.data.api.ServiceApiClient;
import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingStartRequestDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;

import okhttp3.ResponseBody;
import retrofit2.Callback;

public class DrawingRemoteDataSource implements DrawingDataSource {
    private static volatile DrawingRemoteDataSource instance;
    private final ServiceApi serviceApi;

    public static DrawingRemoteDataSource getInstance() {
        if (instance == null) {
            synchronized (DrawingRemoteDataSource.class) {
                if (instance == null) {
                    instance = new DrawingRemoteDataSource();
                }
            }
        }
        return instance;
    }

    private DrawingRemoteDataSource() {
        this.serviceApi = ServiceApiClient.getInstance();
    }

    @Override
    public void getDrawings(int userId, Callback<DrawingListResponseDto> callback) {
        try {
            serviceApi.getDrawings(userId).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    @Override
    public void getDrawing(int id, Callback<DrawingViewResponseDto> callback) {
        try {
            serviceApi.getDrawing(id).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    @Override
    public void createDrawing(DrawingCreateRequestDto request, Callback<DrawingCreateResponseDto> callback) {
        try {
            serviceApi.createDrawing(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    @Override
    public void joinDrawing(DrawingJoinRequestDto request, Callback<ResponseBody> callback) {
        try {
            serviceApi.joinDrawing(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("drawingJoin", callback.toString());
            callback.onFailure(null, e);
        }
    }

    @Override
    public void finishDrawing(int id, Callback callback) {
        try {
            serviceApi.finishDrawing(id).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    @Override
    public void abortDrawing(int id, Callback callback) {
        try {
            serviceApi.abortDrawing(id).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    @Override
    public void submitDrawing(int id, DrawingSubmitRequestDto request, Callback callback) {
        try {
            serviceApi.submitDrawing(id, request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    public void realTimeDrawing(DrawingRealTimeRequestDto request, Callback callback) {
        try {
            serviceApi.uploadRealTimeDrawing(1, request).enqueue(callback);

        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

    @Override
    public void startDrawing(DrawingStartRequestDto request, Callback callback) {

        try {
            serviceApi.startDrawing(request).enqueue(callback);

        } catch (Exception e) {
            callback.onFailure(null, e);
        }
    }

}