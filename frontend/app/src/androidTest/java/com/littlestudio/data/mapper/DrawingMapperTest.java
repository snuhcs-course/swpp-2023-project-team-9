package com.littlestudio.data.mapper;

import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.dto.DrawingCanvasRequestDto;
import com.littlestudio.data.dto.DrawingCreateRequestDto;
import com.littlestudio.data.dto.DrawingCreateResponseDto;
import com.littlestudio.data.dto.DrawingJoinRequestDto;
import com.littlestudio.data.dto.DrawingJoinResponseDto;
import com.littlestudio.data.dto.DrawingListRequestDto;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingRealTimeRequestDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@RunWith(AndroidJUnit4.class)
public class DrawingMapperTest {

    private DrawingMapper drawingMapper;
    private ObjectMapper objectMapper;
    private FamilyMapper familyMapper;

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        familyMapper = new FamilyMapper(objectMapper);
        drawingMapper = new DrawingMapper(objectMapper, familyMapper);
    }

    @Test
    public void fromDrawingViewResponseDtoTest() {
        // 테스트에 필요한 데이터를 생성합니다.
        DrawingViewResponseDto dto = new DrawingViewResponseDto();
        // dto에 필요한 데이터 설정을 수행합니다.

        // 테스트 대상 메서드를 호출합니다.
        Drawing drawing = drawingMapper.fromDrawingViewResponseDto(dto);

        // 결과를 검증합니다.
        assertNotNull(drawing);
        // drawing 객체의 필드 값들을 검증합니다.
    }

//    @Test
//    public void fromDrawingListResponseDtoTest() {
//        // 테스트에 필요한 데이터를 생성합니다.
//        DrawingViewResponseDto drawing = new DrawingViewResponseDto();
//        // drawingviewResponse를 만들어서 setup후 설정해주세요.
//        DrawingListResponseDto dto = new DrawingListResponseDto();
//        // dto에 필요한 데이터 설정을 수행합니다.
//
//        // 테스트 대상 메서드를 호출합니다.
//        List<Drawing> drawings = drawingMapper.fromDrawingListResponseDto(dto);
//
//        // 결과를 검증합니다.
//        assertNotNull(drawings);
//        // drawings 리스트의 각 drawing 객체의 필드 값들을 검증합니다.
//    }

    @Test
    public void fromDrawingCreateResponseDtoTest() {
        // 테스트에 필요한 데이터를 생성합니다.
        DrawingCreateResponseDto dto = new DrawingCreateResponseDto();
        // dto에 필요한 데이터 설정을 수행합니다.

        // 테스트 대상 메서드를 호출합니다.
        DrawingCreateResponseDto response = drawingMapper.fromDrawingCreateResponseDto(dto);

        // 결과를 검증합니다.
        assertNotNull(response);
        // response 객체의 필드 값들을 검증합니다.
    }

    @Test
    public void toDrawingJoinRequestDtoTest() {
        // 테스트에 필요한 데이터를 생성합니다.
        DrawingJoinRequestDto drawing = new DrawingJoinRequestDto();
        // drawing에 필요한 데이터 설정을 수행합니다.

        // 테스트 대상 메서드를 호출합니다.
        DrawingJoinRequestDto dto = drawingMapper.toDrawingJoinRequestDto(drawing);

        // 결과를 검증합니다.
        assertNotNull(dto);
        // dto 객체의 필드 값들을 검증합니다.
    }

    @Test
    public void mapToStrokeTest() {
        // 테스트에 필요한 데이터를 생성합니다.
        String request = "테스트용 스트로크 데이터";
        // request에 필요한 데이터 설정을 수행합니다.

        // 테스트 대상 메서드를 호출합니다.
        try {
            DrawingRealTimeRequestDto dto = drawingMapper.mapToStroke(request);

            // 결과를 검증합니다.
            assertNotNull(dto);
            // dto 객체의 필드 값들을 검증합니다.
        } catch (IOException e) {
            e.printStackTrace();
            // 예외가 발생한 경우 예외 처리를 수행합니다.
        }
    }

    @Test
    public void fromJoinResponseDtoTest() {
        // 테스트에 필요한 데이터를 생성합니다.
        String response = "테스트용 응답 데이터";
        // response에 필요한 데이터 설정을 수행합니다.

        // 테스트 대상 메서드를 호출합니다.
        try {
            DrawingJoinResponseDto dto = drawingMapper.fromJoinResponseDto(response);

            // 결과를 검증합니다.
            assertNotNull(dto);
            // dto 객체의 필드 값들을 검증합니다.
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // 예외가 발생한 경우 예외 처리를 수행합니다.
        }
    }
}