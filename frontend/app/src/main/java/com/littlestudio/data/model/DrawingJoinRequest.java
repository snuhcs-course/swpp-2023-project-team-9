package com.littlestudio.data.model;

public class DrawingJoinRequest {
    public DrawingJoinRequest(int userId, String invitationCode) {
        this.user_id = userId;
        this.invitation_code = invitationCode;
    }

    public int user_id;
    public String invitation_code;
}
