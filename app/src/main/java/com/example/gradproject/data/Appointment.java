package com.example.gradproject.data;

public class Appointment
{
    private String appointmentId;
    private String appointmentDay;
    private String appointmentDate;
    private String appointmentTimeFrom, appointmentTimeTo;
    private String appointmentPlace;
    private String appointmentPrice;
    private String appointmentStatus;
    private String appointmentCourse;
    private String tutorName;
    private String tutorId;
    private String studentName;
    private String studentId;
    private long appointmentTimestamp;

    public Appointment() {
    }

    public Appointment(String appointmentId, String appointmentDay, String appointmentDate, String appointmentTimeFrom, String appointmentTimeTo, String appointmentPlace, String appointmentPrice, String appointmentStatus, String appointmentCourse, String tutorName, String tutorId, String studentName, String studentId, long appointmentTimestamp) {
        this.appointmentId = appointmentId;
        this.appointmentDay = appointmentDay;
        this.appointmentDate = appointmentDate;
        this.appointmentTimeFrom = appointmentTimeFrom;
        this.appointmentTimeTo = appointmentTimeTo;
        this.appointmentPlace = appointmentPlace;
        this.appointmentPrice = appointmentPrice;
        this.appointmentStatus = appointmentStatus;
        this.appointmentCourse = appointmentCourse;
        this.tutorName = tutorName;
        this.tutorId = tutorId;
        this.studentName = studentName;
        this.studentId = studentId;
        this.appointmentTimestamp = appointmentTimestamp;
    }

    public long getAppointmentTimestamp() {
        return appointmentTimestamp;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getAppointmentDay() {
        return appointmentDay;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTimeFrom() {
        return appointmentTimeFrom;
    }

    public String getAppointmentTimeTo() {
        return appointmentTimeTo;
    }

    public String getAppointmentPlace() {
        return appointmentPlace;
    }

    public String getAppointmentPrice() {
        return appointmentPrice;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public String getAppointmentCourse() {
        return appointmentCourse;
    }

    public String getTutorName() {
        return tutorName;
    }

    public String getTutorId() {
        return tutorId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }
}
