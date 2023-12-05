package com.littlestudio.data.datasource;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.data.dto.DrawingListResponseDto;
import com.littlestudio.data.dto.DrawingViewResponseDto;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.model.User;
import com.littlestudio.ui.ImageActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
public class DrawingRemoteDataSourceTest {

    @Rule
    public ActivityScenarioRule<ImageActivity> mActivityRule = new ActivityScenarioRule<>(ImageActivity.class);
    @Rule
    public IntentsTestRule<ImageActivity> intentsTestRule = new IntentsTestRule<>(ImageActivity.class);

    private DrawingRemoteDataSource drawingRemoteDataSource;
    private User testUser;

    private DrawingMapper drawingMapper;

    @Before
    public void setup() {
        drawingRemoteDataSource = DrawingRemoteDataSource.getInstance();
        testUser = new User(1, "test", "test", "test", "test", 1, new Date());
        drawingMapper = new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()));
    }

    @Test
    public void getDrawingsTest() {
        drawingRemoteDataSource.getDrawings(testUser.id, new Callback<DrawingListResponseDto>() {
            @Override
            public void onResponse(Call<DrawingListResponseDto> call, Response<DrawingListResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Drawing> drawings = drawingMapper.fromDrawingListResponseDto(response.body());
                    Assert.assertNotNull(drawings);

                }
            }

            @Override
            public void onFailure(Call<DrawingListResponseDto> call, Throwable t) {
                // 테스트 실패 시 동작을 작성하세요.
            }
        });
        // 테스트할 동작을 작성하세요.
    }

    @Test
    public void getDrawingTest() {
        int drawingId = 1; // 테스트할 드로잉 ID를 입력하세요.
        drawingRemoteDataSource.getDrawing(drawingId, new Callback<DrawingViewResponseDto>() {
            @Override
            public void onResponse(Call<DrawingViewResponseDto> call, Response<DrawingViewResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Drawing drawing = drawingMapper.fromDrawingViewResponseDto(response.body());
                    Assert.assertNotNull(drawing);
                }
            }

            @Override
            public void onFailure(Call<DrawingViewResponseDto> call, Throwable t) {
                // 테스트 실패 시 동작을 작성하세요.
            }
        });
        // 테스트할 동작을 작성하세요.
    }





}