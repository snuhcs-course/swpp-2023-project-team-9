package com.littlestudio.data.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.dto.UserCreateRequestDto;
import com.littlestudio.data.dto.UserLoginRequestDto;
import com.littlestudio.data.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Mock
    private ObjectMapper objectMapper;

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userMapper = new UserMapper(objectMapper);
    }

    @Test
    void testToUserCreateRequestDto() throws Exception {
        UserCreateRequestDto dto = new UserCreateRequestDto("ABCD1","ABCD2", "ABCD3", "female", "child");
        when(objectMapper.convertValue(dto, UserCreateRequestDto.class)).thenReturn(dto);

        UserCreateRequestDto result = userMapper.toUserCreateRequestDto(dto);
        assertEquals(dto, result);
        verify(objectMapper).convertValue(dto, UserCreateRequestDto.class);
    }

    @Test
    void testToUserLoginRequestDto() throws Exception {
        User user = new User(); // Initialize as needed
        UserLoginRequestDto expectedDto = new UserLoginRequestDto("test1","test2");
        when(objectMapper.convertValue(user, UserLoginRequestDto.class)).thenReturn(expectedDto);

        UserLoginRequestDto result = userMapper.toUserLoginRequestDto(user);
        assertEquals(expectedDto, result);
        verify(objectMapper).convertValue(user, UserLoginRequestDto.class);
    }

}
