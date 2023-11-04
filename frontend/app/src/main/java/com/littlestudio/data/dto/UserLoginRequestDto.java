package com.littlestudio.data.dto;

public class UserLoginRequestDto {
    public String username;
    public String password;

    public UserLoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

