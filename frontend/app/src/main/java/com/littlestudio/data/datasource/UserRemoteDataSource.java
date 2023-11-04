package com.littlestudio.data.datasource;

import com.littlestudio.data.api.ServiceApi;
import com.littlestudio.data.api.ServiceApiClient;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;

import retrofit2.Callback;

public class UserRemoteDataSource implements UserDataSource {
    private final ServiceApi serviceApi;
    public UserRemoteDataSource() {
        this.serviceApi = ServiceApiClient.getServiceApiInstance();
    }

    public void userLoginRequest(UserLoginRequestDto request, Callback callback) {
        try {
            serviceApi.loginUser(request).enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }

    public void userCreateRequest(UserCreateRequestDto request, Callback callback) {
        try {
            serviceApi.registerUser(request).enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(null, e);
        }
    }
}
