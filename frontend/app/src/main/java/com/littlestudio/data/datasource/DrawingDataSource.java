package com.littlestudio.data.datasource;

import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;

import retrofit2.Callback;

public interface DrawingDataSource {

    void getDrawings(int userId, Callback<DrawingListResponseDto> callback);

    void createDrawing(DrawingCreateRequestDto request, Callback<DrawingCreateResponseDto> callback);

    void joinDrawing(DrawingJoinRequestDto request, Callback callback);

    void submitDrawing(DrawingSubmitRequestDto request, Callback callback);
    // TODO define more drawing methods
    // TODO create similar interface and remote data source for family and user
}