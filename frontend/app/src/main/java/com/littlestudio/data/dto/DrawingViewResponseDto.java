package com.littlestudio.data.dto;

import java.util.Date;
import java.util.List;

public class DrawingViewResponseDto {
    public int id;
    public String title;
    public String description;
    public String image_url;
    public String jumping_url;
    public String dab_url;
    public String wave_hello_url;
    public String type;
    public int host_id;
    public List<FamilyMemberResponseDto> participants;
    public int voice_id;
    public Date created_at;
    public Date updated_at;
}
