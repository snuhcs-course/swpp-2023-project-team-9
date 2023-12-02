package com.littlestudio.data.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.littlestudio.data.model.User;

public class UserLocalDataSource {
    private static volatile UserLocalDataSource instance;
    private Context context;
    static final String PREF_USER = "user";

    public static UserLocalDataSource getInstance(Context context) {
        if (instance == null) {
            synchronized (UserLocalDataSource.class) {
                if (instance == null) {
                    instance = new UserLocalDataSource(context);
                }
            }
        }
        return instance;
    }

    private UserLocalDataSource(Context context) {
        this.context = context;
    }

    SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void setUser(User user) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_USER, User.toJson(user));
        editor.commit();
    }

    public @Nullable User getUser() {
        String userJson = getSharedPreferences().getString(PREF_USER, "");
        if (userJson.isEmpty()) {
            return null;
        }
        return User.fromJson(userJson);
    }

    public void clearUser() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(PREF_USER);
        editor.commit();
    }
}