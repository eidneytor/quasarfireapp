package com.quasar.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {

    @JsonProperty("X")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private float x;
    @JsonProperty("Y")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private float y;

    public Position(float x, float y) {
        super();
        setX(x);
        setY(y);
    }

    public float getX() { return x; }
    public void setX(float value) { this.x = value; }

    public float getY() { return y;}
    public void setY(float value) { this.y = value; }
}