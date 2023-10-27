package com.littlestudio.data.repository;

import android.util.Log;

import com.littlestudio.data.datasource.DrawingDataSource;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.model.Drawing;

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
    public void submitDrawing(DrawingSubmitRequestDto request, final Callback callback){
        remoteDataSource.submitDrawing(request, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(null, Response.success(response));
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }
    public void realTimeDrawing(DrawingRealTimeRequestDto request, final Callback callback){
        remoteDataSource.realTimeDrawing(request, new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onResponse(null, Response.success(response));
                            } else {
                                Log.e("error", response.message());
                                Log.e("error", response.toString());
                                callback.onFailure(null, new Throwable("Unsuccessful response"));
                            }
                        }
                        @Override
                        public void onFailure(Call call, Throwable t) {
                            callback.onFailure(null, t);
                        }
                    });
                }
    // TODO implement more drawing methods
    // TODO create similar UserRepository and FamilyRepository
}
