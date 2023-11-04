package com.littlestudio.data.datasource;


import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;

import retrofit2.Callback;

public interface UserDataSource {

    void userLoginRequest(UserLoginRequestDto request, Callback callback);

    void userCreateRequest(UserCreateRequestDto request, Callback callback);


}
