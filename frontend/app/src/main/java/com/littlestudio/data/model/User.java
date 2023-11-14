package com.littlestudio.data.model;

import com.google.gson.Gson;

import java.util.Date;

public class User {
    public int id;
    public String full_name;
    public String username;

    public String password;
    public String type;
    public String gender;

    public int family_id;
    public Date created_at;

    public String getFullName() {
        return full_name;
    }

    public static String toJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    public static User fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, User.class);
    }
}

