package com.quasar.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class Satellite {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Position position;
    @JsonProperty("name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonProperty("distance")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Float distanceToShip;
    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ArrayList<String> message;

    public Position getCoordinate() { return position; }
    public void setCoordinate(float x, float y) { this.position = new Position(x, y); }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public Float getDistanceToShip() { return distanceToShip; }
    public void setDistanceToShip(Float value) { this.distanceToShip = value; }

    public ArrayList<String> getMessage() { return message; }
    public void setMessage(ArrayList<String> value) { this.message = value; }

    public Satellite() {
        super();
        setMessage(new ArrayList<>());
    }

    public Satellite(String name, float distanceToShip, ArrayList<String> message)
    {
        super();
        setName(name);
        setDistanceToShip(distanceToShip);
        setMessage(message);
    }

    public Satellite(Float x, Float y, String name)
    {
        if (x != null && y != null) setCoordinate(x, y);
        setName(name);
    }

    public float getX() { return position.getX(); }
    public float getY() { return position.getY(); }
    public float getXDistanceTo(float point) {
        return point - this.position.getX();
    }
    public float getYDistanceTo(float point) {
        return point - this.position.getY();
    }
    public float getDistanceTo(Position position)
    {
        return (float)Math.sqrt(Math.pow(getXDistanceTo(position.getX()), 2) + Math.pow(getYDistanceTo(position.getY()), 2));
    }
}
