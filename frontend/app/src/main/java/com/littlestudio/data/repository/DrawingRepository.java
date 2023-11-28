package com.littlestudio.data.repository;

import android.util.Log;

import com.littlestudio.data.datasource.DrawingDataSource;
import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingJoinResponseDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingStartRequestDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.model.Drawing;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrawingRepository {
    private static volatile DrawingRepository instance;
    private final DrawingDataSource remoteDataSource;
    private final DrawingMapper drawingMapper;

    public static DrawingRepository getInstance(DrawingDataSource remoteDataSource, DrawingMapper drawingMapper) {
        if (instance == null) {
            synchronized (DrawingRepository.class) {
                if (instance == null) {
                    instance = new DrawingRepository(remoteDataSource, drawingMapper);
                }
            }
        }
        return instance;
    }

    private DrawingRepository(DrawingDataSource remoteDataSource, DrawingMapper drawingMapper) {
        this.remoteDataSource = remoteDataSource;
        this.drawingMapper = drawingMapper;
    }

    public void getDrawings(int userId, final Callback<List<Drawing>> callback) {
        remoteDataSource.getDrawings(userId, new Callback<DrawingListResponseDto>() {
            @Override
            public void onResponse(Call<DrawingListResponseDto> call, Response<DrawingListResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Drawing> drawings = drawingMapper.fromDrawingListResponseDto(response.body());
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

    public void getDrawing(int id, final Callback<Drawing> callback) {
        remoteDataSource.getDrawing(id, new Callback<DrawingViewResponseDto>() {
            @Override
            public void onResponse(Call<DrawingViewResponseDto> call, Response<DrawingViewResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Drawing drawing = drawingMapper.fromDrawingViewResponseDto(response.body());
                    callback.onResponse(null, Response.success(drawing));
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<DrawingViewResponseDto> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    public void finishDrawing(int id, final Callback callback) {
        remoteDataSource.finishDrawing(id, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(null, null);
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

    public void submitDrawing(int id, DrawingSubmitRequestDto request, final Callback callback) {
        remoteDataSource.submitDrawing(id, request, new Callback<DrawingViewResponseDto>() {
            @Override
            public void onResponse(Call<DrawingViewResponseDto> call, Response<DrawingViewResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Drawing drawing = drawingMapper.fromDrawingViewResponseDto(response.body());
                    callback.onResponse(null, Response.success(drawing));
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<DrawingViewResponseDto> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    public void createDrawing(DrawingCreateRequestDto request, final Callback<DrawingCreateResponseDto> callback) {
        remoteDataSource.createDrawing(request, new Callback<DrawingCreateResponseDto>() {
            @Override
            public void onResponse(Call<DrawingCreateResponseDto> call, Response<DrawingCreateResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(null, Response.success(response.body()));
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

    public void joinDrawing(DrawingJoinRequestDto request, final Callback<DrawingJoinResponseDto> callback) {
        remoteDataSource.joinDrawing(drawingMapper.toDrawingJoinRequestDto(request), new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {
                    DrawingJoinResponseDto drawingJoinResponseDto = null;
                    try {
                        drawingJoinResponseDto = drawingMapper.fromJoinResponseDto(response.body().string());
                    } catch (IOException e) {
                        callback.onFailure(null, new Throwable("Unsuccessful response"));
                    }
                    callback.onResponse(null, Response.success(drawingJoinResponseDto));
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

    public void realTimeDrawing(DrawingRealTimeRequestDto request, final Callback callback) {
        remoteDataSource.realTimeDrawing(request, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
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

    public void startDrawing(DrawingStartRequestDto request, final Callback callback) {

        remoteDataSource.startDrawing(request, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Log.d("start success", "sucess");
                    callback.onResponse(null, Response.success(response));
                } else {
                    Log.e("error", response.message());
                    Log.e("error", response.toString());
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("hei", "jfke");
                callback.onFailure(null, t);
            }
        });

    }
}