package com.example.gradproject.data;

public class Course
{
    private String courseId;
    private String courseName;

    public Course()
    {

    }

    public Course(String courseId, String courseName)
    {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public String getCourseId()
    {
        return courseId;
    }

    public String getCourseName()
    {
        return courseName;
    }
}
