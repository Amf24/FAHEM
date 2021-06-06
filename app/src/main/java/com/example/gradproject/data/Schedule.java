package com.example.gradproject.data;

public class Schedule
{
    private String scheduleId;
    private String day;

    public Schedule()
    {

    }

    public Schedule(String scheduleId, String day)
    {
        this.scheduleId = scheduleId;
        this.day = day;
    }

    public String getScheduleId()
    {
        return scheduleId;
    }

    public String getDay()
    {
        return day;
    }
}
