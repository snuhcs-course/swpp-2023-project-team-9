package com.littlestudio.data.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.dto.FamilyMemberResponseDto;
import com.littlestudio.data.dto.FamilyListResponseDto;
import com.littlestudio.data.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FamilyMapperTest {

    @Mock
    private ObjectMapper objectMapper;

    private FamilyMapper familyMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        familyMapper = new FamilyMapper(objectMapper);
    }

    @Test
    void testMapMemberToModel() throws Exception {
        FamilyMemberResponseDto dto = new FamilyMemberResponseDto();
        User expectedUser = new User();
        when(objectMapper.convertValue(dto, User.class)).thenReturn(expectedUser);

        User actualUser = familyMapper.mapMemberToModel(dto);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void testMapMembersToModel() throws Exception {
        List<FamilyMemberResponseDto> dtos = Arrays.asList(new FamilyMemberResponseDto(), new FamilyMemberResponseDto());
        User user1 = new User();
        User user2 = new User();
        when(objectMapper.convertValue(any(FamilyMemberResponseDto.class), eq(User.class)))
                .thenReturn(user1)
                .thenReturn(user2);

        List<User> users = familyMapper.mapMembersToModel(dtos);
        assertEquals(2, users.size());
        verify(objectMapper, times(2)).convertValue(any(FamilyMemberResponseDto.class), eq(User.class));
    }

    @Test
    void testMapListToModel() throws Exception {
        FamilyListResponseDto dto = new FamilyListResponseDto();
        dto.users = Arrays.asList(new User(), new User());

        List<User> users = familyMapper.mapListToModel(dto);
        assertEquals(dto.users.size(), users.size());
    }

}
