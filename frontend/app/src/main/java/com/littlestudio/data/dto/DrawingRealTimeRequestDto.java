package com.littlestudio.data.dto;

import com.littlestudio.ui.drawing.widget.Stroke;

public class DrawingRealTimeRequestDto {
    public Stroke stroke_data;
    public DrawingRealTimeRequestDto() {
    }

    public DrawingRealTimeRequestDto(Stroke stroke) {
        this.stroke_data = stroke;
    }

}
