package com.littlestudio.data.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.dto.DrawingCanvasRequestDto;
import com.littlestudio.data.dto.DrawingListRequestDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;
import com.littlestudio.data.model.Drawing;

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

    public Drawing fromDrawingViewResponseDto(DrawingViewResponseDto dto) {
        Drawing drawing = objectMapper.convertValue(dto, Drawing.class);
        if (dto.participants != null) {
            drawing.participants = familyMapper.mapMembersToModel(dto.participants);
        }
        return drawing;
    }

    public List<Drawing> fromDrawingListResponseDto(DrawingListResponseDto dto) {
        return dto.drawings.stream()
                .map(this::fromDrawingViewResponseDto)
                .collect(Collectors.toList());
    }

    public DrawingCanvasRequestDto mapToCanvasDto(Drawing model) {
        return objectMapper.convertValue(model, DrawingCanvasRequestDto.class);
    }

    public DrawingListRequestDto mapToListDto(Drawing drawing) {
        return objectMapper.convertValue(drawing, DrawingListRequestDto.class);
    }

    public DrawingRealTimeRequestDto mapToStroke(String request) throws IOException {
        return objectMapper.readValue(request, DrawingRealTimeRequestDto.class);
    }
}