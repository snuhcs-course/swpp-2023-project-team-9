package com.littlestudio.data.datasource;

import com.littlestudio.data.api.ServiceApi;
import com.littlestudio.data.dto.*;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import retrofit2.Callback;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class DrawingRemoteDataSourceTest {

    @Mock
    private ServiceApi mockServiceApi;

    private DrawingRemoteDataSource dataSource;

    @Before
    public void setUp() {
        //this.dataSource = new DrawingRemoteDataSource();
        MockitoAnnotations.initMocks(DrawingRemoteDataSource.class);
        dataSource = DrawingRemoteDataSource.getInstance(); // Assuming you've added a method for injecting mock
    }

    @Test
    public void getDrawings_ShouldCallServiceApiMethod() {
        Callback<DrawingListResponseDto> mockCallback = mock(Callback.class);

        dataSource.getDrawings(1234, mockCallback);
        verify(mockServiceApi).getDrawings(1234);
    }

    @Test
    public void getDrawing_ShouldCallServiceApiMethod() {
        Callback<DrawingViewResponseDto> mockCallback = mock(Callback.class);

        dataSource.getDrawing(1, mockCallback);
        verify(mockServiceApi).getDrawing(1);
    }


    @Test
    public void createDrawing_ShouldCallServiceApiMethod() {
        DrawingCreateRequestDto requestDto = new DrawingCreateRequestDto(1);
        Callback<DrawingCreateResponseDto> mockCallback = mock(Callback.class);

        dataSource.createDrawing(requestDto, mockCallback);
        verify(mockServiceApi).createDrawing(requestDto);
    }

    @Test
    public void joinDrawing() {
        DrawingJoinRequestDto requestDto = new DrawingJoinRequestDto();
        Callback<ResponseBody> mockCallback = mock(Callback.class);

        dataSource.joinDrawing(requestDto, mockCallback);
        verify(mockServiceApi).joinDrawing(requestDto);
    }

    @Test
    public void finishDrawing() {
        int drawingId = 1; // Example drawing ID
        Callback<ResponseBody> mockCallback = mock(Callback.class);

        dataSource.finishDrawing(drawingId, mockCallback);
        verify(mockServiceApi).finishDrawing(drawingId);
    }

    @Test
    public void submitDrawing() {
        int drawingId = 1; // Example drawing ID
        DrawingSubmitRequestDto requestDto = new DrawingSubmitRequestDto("file", "title", "description"); // Initialize as needed
        Callback<ResponseBody> mockCallback = mock(Callback.class);

        dataSource.submitDrawing(drawingId, requestDto, mockCallback);
        verify(mockServiceApi).submitDrawing(drawingId, requestDto);
    }

    @Test
    public void realTimeDrawing() {
        DrawingRealTimeRequestDto requestDto = new DrawingRealTimeRequestDto();
        Callback<ResponseBody> mockCallback = mock(Callback.class);

        dataSource.realTimeDrawing(requestDto, mockCallback);
        verify(mockServiceApi).uploadRealTimeDrawing(1, requestDto);
    }

    @Test
    public void startDrawing() {
        DrawingStartRequestDto requestDto = new DrawingStartRequestDto("qwe123");
        Callback<ResponseBody> mockCallback = mock(Callback.class);

        dataSource.startDrawing(requestDto, mockCallback);
        verify(mockServiceApi).startDrawing(requestDto);
    }
}