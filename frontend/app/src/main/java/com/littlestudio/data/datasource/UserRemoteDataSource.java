package com.littlestudio.data.datasource;

import com.littlestudio.data.api.ServiceApi;
import com.littlestudio.data.api.ServiceApiClient;
import com.littlestudio.data.dto.FamilyListResponseDto;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.model.User;

import retrofit2.Callback;

public class UserRemoteDataSource {
    private static volatile UserRemoteDataSource instance;
    private final ServiceApi serviceApi;

    public static UserRemoteDataSource getInstance(){
        if (instance == null){
            synchronized (UserRemoteDataSource.class) {
                if (instance == null) {
                    instance = new UserRemoteDataSource();
                }
            }
        }
        return instance;
    }

    private UserRemoteDataSource() {
        this.serviceApi = ServiceApiClient.getInstance();
    }

    public void login(UserLoginRequestDto request, Callback<User> callback) {
        try {
            serviceApi.login(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    public void signup(UserCreateRequestDto request, Callback<User> callback) {
        try {
            serviceApi.signup(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    public void getFamily(int userId, Callback<FamilyListResponseDto> callback) {
        try {
            serviceApi.getFamily(userId).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }
}
