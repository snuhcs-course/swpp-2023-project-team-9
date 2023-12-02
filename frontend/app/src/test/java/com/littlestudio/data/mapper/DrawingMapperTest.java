package com.littlestudio.data.mapper;

import static org.hamcrest.CoreMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.dto.DrawingCanvasRequestDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingJoinResponseDto;
import com.littlestudio.data.dto.DrawingListRequestDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;
import com.littlestudio.data.model.Drawing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DrawingMapperTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private FamilyMapper familyMapper;
    private DrawingMapper drawingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        drawingMapper = new DrawingMapper(objectMapper, familyMapper);
    }

    @Test
    void testFromDrawingViewResponseDto() throws Exception {
        DrawingViewResponseDto dto = mock(DrawingViewResponseDto.class);
        Drawing expectedDrawing = new Drawing();
        when(objectMapper.convertValue(dto, Drawing.class)).thenReturn(expectedDrawing);

        Drawing actualDrawing = drawingMapper.fromDrawingViewResponseDto(dto);
        assertEquals(expectedDrawing, actualDrawing);
    }


    @Test
    void testFromDrawingListResponseDto() throws Exception {
        DrawingListResponseDto dto = mock(DrawingListResponseDto.class);
        dto.drawings = Arrays.asList(new DrawingViewResponseDto(), new DrawingViewResponseDto());
        List<Drawing> drawingList = drawingMapper.fromDrawingListResponseDto(dto);
        assertEquals(drawingList.size(), dto.drawings.size() );
    }


    @Test
    void testMapToCanvasDto() throws Exception {
        Drawing model = new Drawing();
        DrawingCanvasRequestDto expectedDto = new DrawingCanvasRequestDto();
        when(objectMapper.convertValue(model, DrawingCanvasRequestDto.class)).thenReturn(expectedDto);
        DrawingCanvasRequestDto actualDto = drawingMapper.mapToCanvasDto(model);
        assertEquals(expectedDto, actualDto);
    }


    @Test
    void testMapToListDto() throws Exception {
        Drawing drawing = new Drawing();
        DrawingListRequestDto expectedDto = new DrawingListRequestDto();
        when(objectMapper.convertValue(drawing, DrawingListRequestDto.class)).thenReturn(expectedDto);

        DrawingListRequestDto actualDto = drawingMapper.mapToListDto(drawing);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testMapToStroke() throws Exception {
        String request = "{\"example\":\"value\"}";
        DrawingRealTimeRequestDto expectedDto = new DrawingRealTimeRequestDto();
        when(objectMapper.readValue(request, DrawingRealTimeRequestDto.class)).thenReturn(expectedDto);

        DrawingRealTimeRequestDto actualDto = drawingMapper.mapToStroke(request);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFromJoinResponseDto() throws Exception {
        String response = "{\"example\":\"value\"}";
        DrawingJoinResponseDto expectedDto = new DrawingJoinResponseDto();
        when(objectMapper.readValue(response, DrawingJoinResponseDto.class)).thenReturn(expectedDto);

        DrawingJoinResponseDto actualDto = drawingMapper.fromJoinResponseDto(response);
        assertEquals(expectedDto, actualDto);
    }

}