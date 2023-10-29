package com.littlestudio.data.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.dto.DrawingCanvasRequestDto;
import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingListRequestDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingSubmitRequestDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.model.DrawingCreateRequest;
import com.littlestudio.data.model.DrawingCreateResponse;
import com.littlestudio.data.model.DrawingJoinRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DrawingMapper {

    private final ObjectMapper objectMapper;
    private final FamilyMapper familyMapper;

    public DrawingMapper(ObjectMapper objectMapper, FamilyMapper familyMapper) {
        this.objectMapper = objectMapper;
        this.familyMapper = familyMapper;
    }

    public Drawing mapViewToModel(DrawingViewResponseDto dto) {
        Drawing drawing = objectMapper.convertValue(dto, Drawing.class);
        if (dto.participants != null) {
            drawing.participants = familyMapper.mapMembersToModel(dto.participants);
        }
        return drawing;
    }

    public List<Drawing> mapListToModel(DrawingListResponseDto dto) {
        return dto.drawings.stream()
                .map(this::mapViewToModel)
                .collect(Collectors.toList());
    }

    public DrawingCanvasRequestDto mapToCanvasDto(Drawing model) {
        return objectMapper.convertValue(model, DrawingCanvasRequestDto.class);
    }

    public DrawingListRequestDto mapToListDto(Drawing drawing) {
        return objectMapper.convertValue(drawing, DrawingListRequestDto.class);
    }

    public DrawingCreateRequestDto toDrawingCreateRequestDto(DrawingCreateRequest request) {
        return objectMapper.convertValue(request, DrawingCreateRequestDto.class);
    }

    public DrawingCreateResponse fromDrawingCreateResponseDto(DrawingCreateResponseDto dto) {
        return objectMapper.convertValue(dto, DrawingCreateResponse.class);
    }

    public DrawingJoinRequestDto toDrawingJoinRequestDto(DrawingJoinRequest drawing) {
        return objectMapper.convertValue(drawing, DrawingJoinRequestDto.class);

    public DrawingRealTimeRequestDto mapToStroke(String request) throws IOException {
        return objectMapper.readValue(request, DrawingRealTimeRequestDto.class);
    }
}