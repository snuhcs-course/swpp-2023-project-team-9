package com.littlestudio.data.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.model.User;

public class UserMapper {

    private final ObjectMapper objectMapper;

    public UserMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public UserCreateRequestDto mapToCreateDto(User user) {
        return objectMapper.convertValue(user, UserCreateRequestDto.class);
    }

    public UserLoginRequestDto mapToLoginDto(User user) {
        return objectMapper.convertValue(user, UserLoginRequestDto.class);
    }
}
