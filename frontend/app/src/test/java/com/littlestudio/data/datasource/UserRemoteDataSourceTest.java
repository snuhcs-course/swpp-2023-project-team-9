package com.littlestudio.data.datasource;

import com.littlestudio.data.api.ServiceApi;
import com.littlestudio.data.dto.*;
import com.littlestudio.data.model.User;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import retrofit2.Callback;

import static org.mockito.Mockito.*;

public class UserRemoteDataSourceTest {

    @Mock
    private ServiceApi mockServiceApi;

    private UserRemoteDataSource userRemoteDataSource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(UserRemoteDataSource.class);
        userRemoteDataSource = UserRemoteDataSource.getInstance(); // Assuming you've added a method for injecting mock

    }

    @Test
    public void login_ShouldCallServiceApiMethod() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("Test1", "Test2"); // Initialize as needed
        Callback<User> mockCallback = mock(Callback.class);

        userRemoteDataSource.login(requestDto, mockCallback);
        verify(mockServiceApi).login(requestDto);
    }

    @Test
    public void signup_ShouldCallServiceApiMethod() throws Exception {
        UserCreateRequestDto requestDto = new UserCreateRequestDto("adam", "test1",  "test2", "female", "parent"); // Initialize as needed
        Callback<User> mockCallback = mock(Callback.class);

        userRemoteDataSource.signup(requestDto, mockCallback);
        verify(mockServiceApi).signup(requestDto);
    }

    @Test
    public void getFamily_ShouldCallServiceApiMethod() throws Exception {
        int userId = 123; // Example user ID
        Callback<FamilyListResponseDto> mockCallback = mock(Callback.class);

        userRemoteDataSource.getFamily(userId, mockCallback);
        verify(mockServiceApi).getFamily(userId);
    }

    // Additional tests for other methods...
}