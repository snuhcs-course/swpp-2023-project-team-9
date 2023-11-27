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

    public String getFamilyDisplayName(User currentUser) {
        if (currentUser.id == id) {
            return full_name;
        }
        switch (currentUser.type) {
            case UserType.PARENT:
                if (type.equals(UserType.PARENT)) {
                    return full_name;
                } else if (type.equals(UserType.CHILD)) {
                    if (gender.equals(Gender.FEMALE))
                        return "Daughter";
                    else if (gender.equals(Gender.MALE))
                        return "Son";
                    else
                        return full_name;
                }
            case UserType.CHILD:
                if (type.equals(UserType.CHILD)) {
                    return full_name;
                } else if (type.equals(UserType.PARENT)) {
                    if (gender.equals(Gender.FEMALE))
                        return "Mom";
                    else if (gender.equals(Gender.MALE))
                        return "Dad";
                    else
                        return "Parent";
                }
            default:
                return full_name;
        }
    }

    public static String toJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    public static User fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, User.class);
    }

    public User() {
    }

    public User(
            int id,
            String full_name,
            String password,
            String type,
            String gender,
            int family_id,
            Date created_at
    ) {
        this.id = id;
        this.full_name = full_name;
        this.password = password;
        this.type = type;
        this.gender = gender;
        this.family_id = family_id;
        this.created_at = created_at;
    }
}

