package com.littlestudio.data.api;

import com.littlestudio.data.dto.DrawingCanvasRequestDto;
import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;
import com.littlestudio.data.dto.FamilyListResponseDto;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceApi {

    @GET("/drawing")
    Call<DrawingListResponseDto> getDrawings(@Query("user_id") int userId);

    @POST("/drawing/")
    Call<DrawingCreateResponseDto> createDrawing(@Body DrawingCreateRequestDto request);

    // TODO consider changing to dtos
    @GET("/drawing/{id}")
    Call<DrawingViewResponseDto> getDrawing(@Path("id") int id);

    @POST("/drawing/join")
    Call<Void> joinDrawing(@Body DrawingJoinRequestDto request);

    @PUT("/drawing/submit")
    Call<Drawing> submitDrawing(@Body DrawingSubmitRequestDto request);

    @POST("/drawing/{id}/canvas")
    Call<Void> uploadRealTimeDrawing(@Path("id") int id, @Body DrawingCanvasRequestDto request);

    @POST("/user")
    Call<User> registerUser(@Body UserCreateRequestDto request);

    @POST("/user/login")
    Call<User> loginUser(@Body UserLoginRequestDto request);

    @POST("/user/logout")
    Call<Void> logoutUser();

    @GET("/family")
    Call<FamilyListResponseDto> getFamily();

}
