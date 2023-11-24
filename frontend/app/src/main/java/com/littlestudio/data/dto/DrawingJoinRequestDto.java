package com.littlestudio.data.dto;

public class DrawingJoinRequestDto {
    public DrawingJoinRequestDto(int userId, String invitationCode) {
        this.user_id = userId;
        this.invitation_code = invitationCode;
    }

    public DrawingJoinRequestDto() {
    }

    public int user_id;
    public String invitation_code;
}

