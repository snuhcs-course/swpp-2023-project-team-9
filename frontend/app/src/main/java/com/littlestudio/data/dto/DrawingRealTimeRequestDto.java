package com.littlestudio.data.dto;

import com.littlestudio.ui.drawing.widget.Stroke;

public class DrawingRealTimeRequestDto {
    public Stroke stroke_data;
    public String invitationCode;
    public DrawingRealTimeRequestDto() {
    }

    public DrawingRealTimeRequestDto(Stroke stroke, String invitationCode) {
        this.stroke_data = stroke;
        this.invitationCode = invitationCode;

    }

}
