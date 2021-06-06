package com.example.gradproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gradproject.data.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ActivityPreviousAppointmentsDetails extends AppCompatActivity {

    TextView appointmentTutorStudentNameTv;
    TextView appointmentTutorStudentName, appointmentDate, appointmentTimeFrom, appointmentTimeTo, appointmentPlace, appointmentPrice, appointmentCourse, appointmentStatus;

    String stringAppointmentId, stringAppointmentDay, stringAppointmentDate, stringAppointmentTimeFrom, stringAppointmentTimeTo, stringAppointmentPlace, stringAppointmentPrice, stringAppointmentStatus, stringAppointmentCourse, stringTutorName, stringTutorId, stringStudentName, stringStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_appointments_details);

        appointmentTutorStudentNameTv = findViewById(R.id.previous_appointment_tutor_name);
        appointmentTutorStudentName = findViewById(R.id.previous_appointment_tutor_student_name);
        appointmentDate = findViewById(R.id.previous_appointment_date_info);
        appointmentTimeFrom = findViewById(R.id.previous_appointment_time_info_from);
        appointmentTimeTo = findViewById(R.id.previous_appointment_time_info_to);
        appointmentPlace = findViewById(R.id.previous_appointment_place_info);
        appointmentPrice = findViewById(R.id.previous_appointment_price_info);
        appointmentCourse = findViewById(R.id.previous_appointment_course_info);
        appointmentStatus = findViewById(R.id.previous_appointment_status_info);

        Intent intent = getIntent();

        stringAppointmentId = intent.getStringExtra("appointmentId");
        stringAppointmentDay = intent.getStringExtra("appointmentDay");
        stringAppointmentDate = intent.getStringExtra("appointmentDate");
        stringAppointmentTimeFrom = intent.getStringExtra("appointmentTimeFrom");
        stringAppointmentTimeTo = intent.getStringExtra("appointmentTimeTo");
        stringAppointmentPlace = intent.getStringExtra("appointmentPlace");
        stringAppointmentPrice = intent.getStringExtra("appointmentPrice");
        stringAppointmentStatus = intent.getStringExtra("appointmentStatus");
        stringAppointmentCourse = intent.getStringExtra("appointmentCourse");
        stringTutorName = intent.getStringExtra("appointmentTutorName");
        stringTutorId = intent.getStringExtra("tutorId");
        stringStudentName = intent.getStringExtra("appointmentStudentName");
        stringStudentId = intent.getStringExtra("studentId");

        if(MainActivity.tutorUserType)
        {
            appointmentTutorStudentNameTv.setText("Student Name:");
            appointmentTutorStudentName.setText(stringStudentName);
        }else
        {
            appointmentTutorStudentName.setText(stringTutorName);
        }

        appointmentDate.setText(stringAppointmentDate);
        appointmentTimeFrom.setText(stringAppointmentTimeFrom);
        appointmentTimeTo.setText(stringAppointmentTimeTo);
        appointmentPlace.setText(stringAppointmentPlace);
        appointmentPrice.setText(stringAppointmentPrice);
        appointmentCourse.setText(stringAppointmentCourse);
        appointmentStatus.setTextColor(Color.parseColor("#D2C531"));
        appointmentStatus.setText(stringAppointmentStatus);
    }
}
