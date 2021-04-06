package com.quasar.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"cause", "stackTrace", "suppressed", "localizedMessage", "message", "status"})
public class Response {
    @JsonProperty("Message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonProperty("Position")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Position position;
    private int status;

    public String getMessage() { return message;}
    public void setMessage(String value) { this.message = value; }

    public Position getPosition() { return position; }
    public void setPosition(Position value) { this.position = value; }

    public int getStatus() { return status; }
    public void setStatus(int value) { this.status = value; }

    public Response(String message, Position position)
    {
        setMessage(message);
        setPosition(position);
    }

    public Response(String message, Position position, int status)
    {
        setMessage(message);
        setPosition(position);
        setStatus(status);
    }
}
