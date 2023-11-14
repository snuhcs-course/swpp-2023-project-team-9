package com.littlestudio.data.datasource;

import com.littlestudio.data.api.ServiceApi;
import com.littlestudio.data.api.ServiceApiClient;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.model.User;

import retrofit2.Callback;

public class UserRemoteDataSource {
    private final ServiceApi serviceApi;

    public UserRemoteDataSource() {
        this.serviceApi = ServiceApiClient.getServiceApiInstance();
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
}
