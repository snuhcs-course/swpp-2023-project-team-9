package com.littlestudio.data.datasource;

import com.littlestudio.data.api.ServiceApi;
import com.littlestudio.data.api.ServiceApiClient;
import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;

import retrofit2.Callback;

public class DrawingRemoteDataSource implements DrawingDataSource {
    private final ServiceApi serviceApi;

    public DrawingRemoteDataSource() {
        this.serviceApi = ServiceApiClient.getServiceApiInstance();
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
    public void createDrawing(DrawingCreateRequestDto request, Callback<DrawingCreateResponseDto> callback) {
        try {
            serviceApi.createDrawing(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    @Override
    public void joinDrawing(DrawingJoinRequestDto request, Callback callback) {
        try {
            serviceApi.joinDrawing(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    // TODO define more drawing methods
    @Override
    public void submitDrawing(DrawingSubmitRequestDto request, Callback callback){
        try{
            serviceApi.submitDrawing(request).enqueue(callback);
        }catch (Exception e){
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

}