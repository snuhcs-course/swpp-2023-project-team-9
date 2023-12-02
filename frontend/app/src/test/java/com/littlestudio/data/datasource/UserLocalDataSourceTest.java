package com.littlestudio.data.datasource;

import android.content.Context;
import android.content.SharedPreferences;

import com.littlestudio.data.model.User;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Date;
//@RunWith(RobolectricTestRunner.class)

@RunWith(MockitoJUnitRunner.class)
public class UserLocalDataSourceTest {

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    private UserLocalDataSource userLocalDataSource;

    @BeforeEach
    public void setUp() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.remove(anyString())).thenReturn(mockEditor);

        // Resetting the singleton instance before each test
        //UserLocalDataSource.resetInstance();
        userLocalDataSource = UserLocalDataSource.getInstance(mockContext);

    }


    @Test
    public void setUser_ShouldSaveUserInSharedPreferences() {
        User user = new User(
                1, "myname", "password", "child", "female", 2, new Date("2023-12-02"));

        userLocalDataSource.setUser(user);

        verify(mockEditor).putString(UserLocalDataSource.PREF_USER, User.toJson(user));
        verify(mockEditor).commit();
    }

    @Test
    public void getUser_ShouldReturnUserFromSharedPreferences() {
        User expectedUser = new User(
                1, "myname", "password", "child", "female", 2, new Date("2023-12-02"));

        String userJson = User.toJson(expectedUser);

        when(mockSharedPreferences.getString(UserLocalDataSource.PREF_USER, "")).thenReturn(userJson);

        User actualUser = userLocalDataSource.getUser();

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void clearUser_ShouldRemoveUserFromSharedPreferences() {
        userLocalDataSource.clearUser();

        verify(mockEditor).remove(UserLocalDataSource.PREF_USER);
        verify(mockEditor).commit();
    }
}
