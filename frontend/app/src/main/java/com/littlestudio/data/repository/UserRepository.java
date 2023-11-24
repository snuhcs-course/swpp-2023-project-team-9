package com.littlestudio.data.repository;

import androidx.annotation.Nullable;

import com.littlestudio.data.datasource.DrawingDataSource;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.UserMapper;
import com.littlestudio.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static UserRepository instance;
    private final UserRemoteDataSource remoteDataSource;
    private final UserLocalDataSource localDataSource;
    private @Nullable User user;

    public static UserRepository getInstance(UserRemoteDataSource remoteDataSource, UserLocalDataSource localDataSource) {
        if (instance == null){
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository(remoteDataSource, localDataSource);
                }
            }
        }
        return instance;
    }

    private UserRepository(UserRemoteDataSource remoteDataSource, UserLocalDataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
        this.user = localDataSource.getUser();
    }

    public void login(UserLoginRequestDto request, final Callback<User> callback) {
        remoteDataSource.login(request, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User userData = response.body();
                    callback.onResponse(null, Response.success(userData));
                    localDataSource.setUser(userData);
                    user = userData;
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful login response"));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    public void signup(UserCreateRequestDto request, final Callback<User> callback) {
        remoteDataSource.signup(request, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User userData = response.body();
                    callback.onResponse(null, Response.success(userData));
                    localDataSource.setUser(userData);
                    user = userData;
                } else {
                    callback.onFailure(null, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure(null, t);
            }
        });
    }

    public void logout() {
        user = null;
        localDataSource.clearUser();
    }

    public Boolean isLoggedIn() {
        return this.user != null;
    }

    public @Nullable User getUser() {
        return this.user;
    }
}