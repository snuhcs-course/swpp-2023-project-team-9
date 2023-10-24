package com.littlestudio.data.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.dto.FamilyListResponseDto;
import com.littlestudio.data.dto.FamilyMemberResponseDto;
import com.littlestudio.data.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class FamilyMapper {

    private final ObjectMapper objectMapper;

    public FamilyMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public User mapMemberToModel(FamilyMemberResponseDto dto) {
        return objectMapper.convertValue(dto, User.class);
    }

    public List<User> mapMembersToModel(List<FamilyMemberResponseDto> dtos) {
        return dtos.stream()
                .map(this::mapMemberToModel)
                .collect(Collectors.toList());
    }

    public List<User> mapListToModel(FamilyListResponseDto dto) {
        return dto.users;
    }
}
