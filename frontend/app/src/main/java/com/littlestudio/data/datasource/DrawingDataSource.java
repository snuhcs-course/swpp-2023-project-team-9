package com.littlestudio.data.datasource;

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

public interface DrawingDataSource {

    void getDrawings(int userId, Callback<DrawingListResponseDto> callback);

    void getDrawing(int id, Callback<DrawingViewResponseDto> callback);

    void createDrawing(DrawingCreateRequestDto request, Callback<DrawingCreateResponseDto> callback);

    void joinDrawing(DrawingJoinRequestDto request, Callback<ResponseBody> callback);

    void finishDrawing(int id, Callback callback);

    void abortDrawing(int id, Callback callback);

    void submitDrawing(int id, DrawingSubmitRequestDto request, Callback callback);

    void realTimeDrawing(DrawingRealTimeRequestDto request, Callback callback);

    void startDrawing(DrawingStartRequestDto request, Callback callback);
}