package com.littlestudio.data.dto;

public class DrawingSubmitRequestDto {
    public String file;
    public String title;
    public String description;
    public int host_id;

    public DrawingSubmitRequestDto(String file, String title, String description, int host_id) {
        this.file = file;
        this.title = title;
        this.description = description;
        this.host_id = host_id;
    }
}
