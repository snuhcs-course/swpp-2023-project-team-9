package com.littlestudio.data.dto;

public class UserCreateRequestDto {
    public String full_name;
    public String username;

    public String password;
    public String gender;
    public String type;
    public UserCreateRequestDto(String full_name, String username, String password, String gender, String family) {
        this.full_name = full_name;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.type = family;
    }
}
