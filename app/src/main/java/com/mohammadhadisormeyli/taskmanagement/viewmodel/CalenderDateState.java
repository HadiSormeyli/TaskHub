package com.mohammadhadisormeyli.taskmanagement.viewmodel;

import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.widget.CollapsibleCalendar;

public class CalenderDateState {

    private int year;
    private int month;


    public CalenderDateState() {
        this.year = CollapsibleCalendar.Companion.getYEAR();
        this.month = CollapsibleCalendar.Companion.getMONTH();
    }

    public CalenderDateState(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
