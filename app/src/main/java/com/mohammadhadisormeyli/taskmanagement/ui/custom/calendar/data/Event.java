package com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.data;


import java.util.Date;

public class Event {
    private Date day;
    private String color;

    public Event(Date day) {
        this.day = day;
    }

    public Event(Date day, String color) {
        this.day = day;
        this.color = color;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
