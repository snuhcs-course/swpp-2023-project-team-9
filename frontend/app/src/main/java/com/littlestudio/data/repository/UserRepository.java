package com.littlestudio.data.repository;

import android.util.Log;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.littlestudio.data.datasource.UserDataSource;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.mapper.UserMapper;
import com.littlestudio.data.model.User;

public class UserRepository {
    private final UserDataSource remoteDataSource;
    private final UserMapper userMapper;

    // Following principle of dependency injection
    public UserRepository(UserDataSource remoteDataSource, UserMapper userMapper) {
        this.remoteDataSource = remoteDataSource;
        this.userMapper = userMapper;
    }
/*
    public void userLoginRequest(User request, final Callback callback){
        remoteDataSource.userLoginRequest(userMapper.toUserLoginRequestDto(request), new Callback<ResponseBody>() {
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
*/
    public void userLoginRequest(UserLoginRequestDto request, final Callback callback){
        remoteDataSource.userLoginRequest(request, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d(call.toString(), response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResponse(null, Response.success(response));
                } else {
                    Log.e("Login error", response.message());
                    callback.onFailure(null, new Throwable("Unsuccessful login response"));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("Login failure", t.getMessage());
                callback.onFailure(null, t);
            }
        });
    }

    public void userCreateRequest(UserCreateRequestDto request, final Callback callback){
        remoteDataSource.userCreateRequest(request, new Callback<ResponseBody>() {
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
                Log.e("Signup failure", t.getMessage());
                callback.onFailure(null, t);
            }
        });
    }
}