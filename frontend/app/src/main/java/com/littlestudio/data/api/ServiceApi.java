package com.littlestudio.data.api;

import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingJoinResponseDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingStartRequestDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;
import com.littlestudio.data.dto.FamilyListResponseDto;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceApi {

    @GET("/drawing")
    Call<DrawingListResponseDto> getDrawings(@Query("user_id") int userId);

    @POST("/drawing/")
    Call<DrawingCreateResponseDto> createDrawing(@Body DrawingCreateRequestDto request);

    @GET("/drawing/{id}")
    Call<DrawingViewResponseDto> getDrawing(@Path("id") int id);

    @GET("/drawing/join")
    Call<DrawingJoinResponseDto> getParticipants(@Query("invitation_code") String invitation_code);

    @POST("/drawing/join")
    Call<ResponseBody> joinDrawing(@Body DrawingJoinRequestDto request);

    @POST("/drawing/{id}/wait")
    Call<Void> finishDrawing(@Path("id") int id);

    @POST("/drawing/{id}/submit")
    Call<DrawingViewResponseDto> submitDrawing(@Path("id") int id, @Body DrawingSubmitRequestDto request);

    @POST("/drawing/{id}/canvas")
    Call<Void> uploadRealTimeDrawing(@Path("id") int id, @Body DrawingRealTimeRequestDto request);

    @POST("/user/")
    Call<User> signup(@Body UserCreateRequestDto request);

    @POST("/user/login")
    Call<User> login(@Body UserLoginRequestDto request);

    @POST("/user/logout")
    Call<Void> logout();

    @GET("/family")
    Call<FamilyListResponseDto> getFamily(@Query("user_id") int user_id);

    @POST("/drawing/1/start")
    Call<Void> startDrawing(@Body DrawingStartRequestDto request);

}
