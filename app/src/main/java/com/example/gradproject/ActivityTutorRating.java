package com.example.gradproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.gradproject.data.Appointment;
import com.example.gradproject.data.Tutor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

public class ActivityTutorRating extends AppCompatActivity
{

    TextView appointmentTutorNameRating, appointmentDateRating, appointmentTimeFromRating, appointmentTimeToRating, appointmentCourseRating;
    RatingBar tutorExplanation, tutorRespectingTime, tutorAttitude;
    Button submitButton, skipButton;
    int numberOfratings;


    String tutorName, sessionDate, sessionTimeFrom, sessionTimeTo, sessionCourse;
    String tutorId;

    private FirebaseAuth mAuth;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_rating);

        appointmentTutorNameRating = findViewById(R.id.appointment_tutor__name_info_rating);
        appointmentDateRating = findViewById(R.id.appointment_date_info_rating);
        appointmentTimeFromRating = findViewById(R.id.appointment_time_info_from_rating);
        appointmentTimeToRating = findViewById(R.id.appointment_time_info_to_rating);
        appointmentCourseRating = findViewById(R.id.appointment_course_info_rating);

        tutorExplanation = findViewById(R.id.explanation_rating);
        tutorRespectingTime = findViewById(R.id.respecting_time_rating);
        tutorAttitude = findViewById(R.id.attitude_rating);

        submitButton = findViewById(R.id.submit_rating_button);
        skipButton = findViewById(R.id.skip_rating_button);

        mAuth = FirebaseAuth.getInstance();
        userId =  mAuth.getCurrentUser().getUid();

        getAppointmentDetails();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                appointmentTutorNameRating.setText(tutorName);
                appointmentDateRating.setText(sessionDate);
                appointmentTimeFromRating.setText(sessionTimeFrom);
                appointmentTimeToRating.setText(sessionTimeTo);
                appointmentCourseRating.setText(sessionCourse);

                System.out.println("========tutorName=" + tutorName + "=======sessionDate" + sessionDate);
            }
        }, 1000);


        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ratingTutor();
                Intent intent = new Intent(ActivityTutorRating.this, MainActivity.class);
                finish();
                startActivity(intent);

                System.out.println("===This is inside the SUBMIT button=========================================================");
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ActivityTutorRating.this, MainActivity.class);
                finish();
                startActivity(intent);
                System.out.println("===This is inside the SKIP button=========================================================");
            }
        });









        /*
        *
        *
        *
        *
        *
        *
        * IF THE APPOINTMENT INFORMATION IS DRAWN CORRECTLY THEN,
        * WE CONTINUE THE RATING ACCUSATION AND STORE IN THE RATING DATABASE.
        *
        *
        *
        *
        *
        * */



    }

    public void getAppointmentDetails()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Appointments").child(userId).orderByChild("appointmentTimestamp").limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                System.out.println("===================================INSIDE -1====================================");
                if(dataSnapshot.exists())
                {
                    System.out.println("===================================INSIDE 0====================================");
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Appointment appointment = snapshot.getValue(Appointment.class);
                        String date = appointment.getAppointmentDate();

                        tutorName = appointment.getTutorName();
                        tutorId = appointment.getTutorId();
                        sessionDate = appointment.getAppointmentDate();
                        sessionTimeFrom = appointment.getAppointmentTimeFrom();
                        sessionTimeTo = appointment.getAppointmentTimeTo();
                        sessionCourse = appointment.getAppointmentCourse();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    public void ratingTutor()
    {
        final DatabaseReference tutorsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Tutors");

        Query query = FirebaseDatabase.getInstance().getReference("Accounts/Tutors").orderByChild("accountId").equalTo(tutorId);

        query.addListenerForSingleValueEvent(new ValueEventListener() //addListenerForSingleValueEvent
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Tutor t = dataSnapshot.getChildren().iterator().next().getValue(Tutor.class);
                    System.out.println("===This is inside the ratingTutor Method======================11111===================================");
                    if(t.getNumberOfRatings() == 0)
                    {
                        float tutorExp = tutorExplanation.getRating();
                        float tutorRes = tutorRespectingTime.getRating();
                        float tutorAtt = tutorAttitude.getRating();
                        int numberOfRating = t.getNumberOfRatings()+1;

                        float tutorGen = (tutorExp + tutorRes + tutorAtt);

                        Tutor tutor = new Tutor(t.getAccountId(), t.getFullName(),t.getEmail(),t.getPhoneNumber(), t.getTutorDescription(),numberOfRating,tutorExp,tutorRes,tutorAtt,tutorGen);
                        tutorsDatabase.child(t.getAccountId()).setValue(tutor);
                        System.out.println("===This is inside the ratingTutor Method======================2222===================================");

                    }else
                    {
                        float tutorExp = t.getTutorExplanation() + tutorExplanation.getRating();
                        float tutorRes = t.getTutorRespectingTime() + tutorRespectingTime.getRating();
                        float tutorAtt = t.getTutorAttitude() + tutorAttitude.getRating();
                        int numberOfRating = t.getNumberOfRatings()+1;

                        float tutorGen = (tutorExp + tutorRes + tutorAtt)/numberOfRating;


                        Tutor tutor = new Tutor(t.getAccountId(), t.getFullName(),t.getEmail(),t.getPhoneNumber(), t.getTutorDescription(),numberOfRating,tutorExp,tutorRes,tutorAtt,tutorGen);
                        tutorsDatabase.child(t.getAccountId()).setValue(tutor);
                        System.out.println("===This is inside the ratingTutor Method======================3333===================================");
                    }
                }
                System.out.println("===This is inside the ratingTutor Method======================4444===================================");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}
