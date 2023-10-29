package com.littlestudio.ui.drawing.widget;

import android.graphics.Path;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Line.class, name = "LINE"),
        @JsonSubTypes.Type(value = Quad.class, name = "QUAD"),
        @JsonSubTypes.Type(value = Move.class, name = "MOVE")
})
public interface Action extends Serializable {
    void perform(Path path);

    void perform(Writer writer) throws IOException;
}

enum ActionType {
    LINE, MOVE, QUAD
}