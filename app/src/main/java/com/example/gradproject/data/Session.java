package com.example.gradproject.data;

import java.sql.Timestamp;

public class Session
{
    private String sessionId;
    private String day;
    private String date;
    private String timeFrom, timeTo;
    private String place;
    private String price;
    private String status;
    private String sessionCourse;
    private long sessionTimestamp;

    public Session()
    {

    }

    public Session(String sessionId, String day, String date, String timeFrom, String timeTo, String place, String price, String status, String sessionCourse, long sessionTimestamp)
    {
        this.sessionId = sessionId;
        this.day = day;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.place = place;
        this.price = price;
        this.status = status;
        this.sessionCourse = sessionCourse;
        this.sessionTimestamp = sessionTimestamp;
    }

    public long getSessionTimestamp() { return sessionTimestamp; }

    public String getSessionId() {
        return sessionId;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public String getPlace() {
        return place;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getSessionCourse() {
        return sessionCourse;
    }
}
