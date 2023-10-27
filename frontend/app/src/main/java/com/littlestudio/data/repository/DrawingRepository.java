package com.littlestudio.data.repository;

import com.littlestudio.data.datasource.DrawingDataSource;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.model.DrawingCreateRequest;
import com.littlestudio.data.model.DrawingCreateResponse;
import com.littlestudio.data.model.DrawingJoinRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrawingRepository {
    private final DrawingDataSource remoteDataSource;
    private final DrawingMapper drawingMapper;

    // Following principle of dependency injection
    public DrawingRepository(DrawingDataSource remoteDataSource, DrawingMapper drawingMapper) {
        this.remoteDataSource = remoteDataSource;
        this.drawingMapper = drawingMapper;
    }

    public void getDrawings(int userId, final Callback<List<Drawing>> callback) {
        remoteDataSource.getDrawings(userId, new Callback<DrawingListResponseDto>() {
            @Override
            public void onResponse(Call<DrawingListResponseDto> call, Response<DrawingListResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Drawing> drawings = drawingMapper.mapListToModel(response.body());
                    callback.onResponse(null, Response.success(drawings));
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<DrawingListResponseDto> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    public void createDrawing(DrawingCreateRequest request, final Callback<DrawingCreateResponse> callback) {
        remoteDataSource.createDrawing(drawingMapper.toDrawingCreateRequestDto(request), new Callback<DrawingCreateResponseDto>() {
            @Override
            public void onResponse(Call<DrawingCreateResponseDto> call, Response<DrawingCreateResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DrawingCreateResponse createResponse = drawingMapper.fromDrawingCreateResponseDto(response.body());
                    callback.onResponse(null, Response.success(createResponse));
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<DrawingCreateResponseDto> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    public void joinDrawing(DrawingJoinRequest request, final Callback callback) {
        remoteDataSource.joinDrawing(drawingMapper.toDrawingJoinRequestDto(request), new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(null, Response.success(response));
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

// TODO implement more drawing methods
// TODO create similar UserRepository and FamilyRepository
}
