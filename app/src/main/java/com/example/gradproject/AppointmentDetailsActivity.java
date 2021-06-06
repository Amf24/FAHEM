package com.example.gradproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gradproject.EmailSender.SendMail;
import com.example.gradproject.Notifications.ReminderNotification;
import com.example.gradproject.SessionsActivities.ActivitySessionDetails;
import com.example.gradproject.data.Appointment;
import com.example.gradproject.data.ChatRoom;
import com.example.gradproject.data.Session;
import com.example.gradproject.data.Student;
import com.example.gradproject.data.Tutor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.concurrent.locks.ReadWriteLock;

public class AppointmentDetailsActivity extends AppCompatActivity
{
    private static final int REQUEST_CALL = 1;

    TextView appointmentTutorStudentNameTv;
    TextView appointmentTutorStudentName, appointmentDate, appointmentTimeFrom, appointmentTimeTo, appointmentPlace, appointmentPrice, appointmentCourse, appointmentStatus;
    Button cancelButton, chatButton;
    ImageView phoneCall;

    private FirebaseAuth mAuth;
    String stringAppointmentId, stringAppointmentDay, stringAppointmentDate, stringAppointmentTimeFrom, stringAppointmentTimeTo, stringAppointmentPlace, stringAppointmentPrice, stringAppointmentStatus, stringAppointmentCourse, stringTutorName, stringTutorId, stringStudentName, stringStudentId;
    long longAppointmentTimestamp;
    String sessionIdCopy;

    String studentEmail, tutorEmail;
    String userNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        appointmentTutorStudentNameTv = findViewById(R.id.appointment_tutor_name);
        appointmentTutorStudentName = findViewById(R.id.appointment_tutor_student_name);
        appointmentDate = findViewById(R.id.appointment_date_info);
        appointmentTimeFrom = findViewById(R.id.appointment_time_info_from);
        appointmentTimeTo = findViewById(R.id.appointment_time_info_to);
        appointmentPlace = findViewById(R.id.appointment_place_info);
        appointmentPrice = findViewById(R.id.appointment_price_info);
        appointmentCourse = findViewById(R.id.session_course_info);
        appointmentStatus = findViewById(R.id.appointment_status_info);

        cancelButton = findViewById(R.id.cancel_appointment_button);
        chatButton = findViewById(R.id.chat_to_tutor_button);
        phoneCall = findViewById(R.id.phone_call_user);

        mAuth = FirebaseAuth.getInstance();

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
        longAppointmentTimestamp = intent.getLongExtra("appointmentTimestamp", 0);

        if(MainActivity.tutorUserType)
        {
            appointmentTutorStudentNameTv.setText("Student Name:");
            appointmentTutorStudentName.setText(stringStudentName);
            chatButton.setVisibility(View.GONE);
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
        appointmentStatus.setTextColor(Color.parseColor("#BFE91E25"));
        appointmentStatus.setText(stringAppointmentStatus);

        gettingStudentAndTutorEmails();
        getUserNumber();
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDeleteDialog(stringAppointmentDay);
                //cancelAppointment(); //show dialog
                cancelAppointmentNotification(); //This method is to cancel the reminder for that appointment.
                sendEmailForAppointmentCancellation(studentEmail, tutorEmail, stringAppointmentDay, stringStudentName, stringTutorName, stringAppointmentTimeFrom, stringAppointmentTimeTo);
            }
        });

        if(checkAppointmentTime())
        {
            phoneCall.setVisibility(View.VISIBLE);
            phoneCall.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    makePhoneCall();
                }
            });
        }


        chatButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AppointmentDetailsActivity.this, ActivityChat.class);

                intent.putExtra("tutorId",stringTutorId);
                intent.putExtra("studentId",stringStudentId);

                intent.putExtra("tutorName",stringTutorName);
                intent.putExtra("studentName",stringStudentName);

                startActivity(intent);
            }
        });
    }

    private void showDeleteDialog(String appointmentDay)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.cancel_appointment_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button yesButton = dialogView.findViewById(R.id.yes_button_app);
        final Button noButton = dialogView.findViewById(R.id.no_button_app);

        dialogBuilder.setTitle(appointmentDay);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        yesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cancelAppointment();
                b.hide();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                b.hide();
            }
        });
    }

    public void cancelAppointment()
    {
        final DatabaseReference canceledAppointmentDatabase = FirebaseDatabase.getInstance().getReference("PreviousAppointments").child(mAuth.getCurrentUser().getUid());// This is PreviousAppointments DB.

        if(MainActivity.studentUserType) //here if the student cancel the Appointment.
        {
            Query query = FirebaseDatabase.getInstance().getReference("Appointments").child(stringStudentId);//stringStudentId

            query.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren())
                        {
                            Appointment appointment = snapshot.getValue(Appointment.class);

                            String sessionKey = snapshot.getKey();//because the appointment is saved under the session that got booked and created an appointment.
                            sessionIdCopy = snapshot.getKey();//this is a copy of hte session id.

                            DatabaseReference AppointmentDatabase = FirebaseDatabase.getInstance().getReference("Appointments/").child(stringStudentId).child(sessionKey);

                            if(appointment.getAppointmentId().equals(stringAppointmentId))
                            {
                                updateSessionStatusToAvailable(sessionIdCopy);

                                stringAppointmentStatus = "Canceled";

                                Appointment canceledAppointment = new Appointment(stringAppointmentId,stringAppointmentDay,stringAppointmentDate,stringAppointmentTimeFrom,stringAppointmentTimeTo,stringAppointmentPlace,stringAppointmentPrice,stringAppointmentStatus,stringAppointmentCourse,stringTutorName,stringTutorId,stringStudentName,stringStudentId,longAppointmentTimestamp);
                                canceledAppointmentDatabase.child(stringAppointmentId).setValue(canceledAppointment);

                                AppointmentDatabase.removeValue();//This removes the appointment from the student list.
                                FirebaseDatabase.getInstance().getReference("Appointments/").child(stringTutorId).child(sessionKey).removeValue();//This removes the appointment from the tutor list.

                                Toast.makeText(AppointmentDetailsActivity.this, "Appointment Canceled.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });


        }else if(MainActivity.tutorUserType) //Here if the tutor cancel the Appointment.
            {
                /*
                * Here the concept is that if the tutor cancel the appointment it also gets canceled from the student end
                * but if the student cancel the appointment it gets canceled only form the student end, and
                * from the tutor end, the status of the session related to that appointment change to Available
                * and the session can be seen by other students again.*/

                Query query = FirebaseDatabase.getInstance().getReference("Appointments").child(stringTutorId);

                query.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            for(DataSnapshot snapshot: dataSnapshot.getChildren())
                            {
                                Appointment appointment = snapshot.getValue(Appointment.class);

                                String sessionKey = snapshot.getKey(); //because the appointment is saved under the session that got booked and created an appointment.
                                sessionIdCopy = snapshot.getKey(); //this is a copy of hte session id.

                                DatabaseReference AppointmentDatabase = FirebaseDatabase.getInstance().getReference("Appointments/").child(stringTutorId).child(sessionKey);

                                if(appointment.getAppointmentId().equals(stringAppointmentId))
                                {
                                    FirebaseDatabase.getInstance().getReference("Schedules").child(stringTutorId).child(sessionKey).removeValue(); //This removes the session which the tutor canceled its appointment.
                                    stringAppointmentStatus = "Canceled";
                                    Appointment canceledAppointment = new Appointment(stringAppointmentId,stringAppointmentDay,stringAppointmentDate,stringAppointmentTimeFrom,stringAppointmentTimeTo,stringAppointmentPlace,stringAppointmentPrice,stringAppointmentStatus,stringAppointmentCourse,stringTutorName,stringTutorId,stringStudentName,stringStudentId,longAppointmentTimestamp);

                                    canceledAppointmentDatabase.child(stringAppointmentId).setValue(canceledAppointment); // This adds canceled appointment to the tutor previous appointments list.
                                    FirebaseDatabase.getInstance().getReference("PreviousAppointments").child(stringStudentId).child(stringAppointmentId).setValue(canceledAppointment); //This adds canceled appointment to the student previous appointments list.

                                    AppointmentDatabase.removeValue();//This removes the appointment from the tutor list.
                                    FirebaseDatabase.getInstance().getReference("Appointments/").child(stringStudentId).child(sessionKey).removeValue();//This removes the appointment from the student list.

                                    Toast.makeText(AppointmentDetailsActivity.this, "Appointment Canceled.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
    }

    public void updateSessionStatusToAvailable(final String sessionId) //This method return the session to Available status if ONLY the student cancel the appointment.
    {
        //DatabaseReference sessionsDatabase = FirebaseDatabase.getInstance().getReference("Schedules").child(stringTutorId);

        FirebaseDatabase.getInstance().getReference("Schedules").child(stringTutorId).child(sessionId).removeValue();

        String stringSessionStatus = "Available";

        Session sessionObj = new Session(sessionId,stringAppointmentDay,stringAppointmentDate,stringAppointmentTimeFrom, stringAppointmentTimeTo,stringAppointmentPlace, stringAppointmentPrice,stringSessionStatus,  stringAppointmentCourse, longAppointmentTimestamp);

        FirebaseDatabase.getInstance().getReference("Schedules").child(stringTutorId).child(sessionId).setValue(sessionObj);
    }

    public void cancelAppointmentNotification()
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, ReminderNotification.class);

        String appointmentDay = stringAppointmentDate.substring(0,2);
        String appointmentTimeFrom = stringAppointmentTimeFrom.substring(0,2).replaceAll(":","").replaceAll(" ","");

        /* notificationId is used to identify every and each of the notification,
        as well as relate every notification with its appointment.
        the id consist of (the number of the day + number of the staring hour).
        */

        int notificationId = ActivitySessionDetails.middleTFiveDigits(longAppointmentTimestamp);
        //int notificationId = Integer.parseInt(appointmentDay) + Integer.parseInt(appointmentTimeFrom);

        PendingIntent broadcast = PendingIntent.getBroadcast(this, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(broadcast);
    }

    private void sendEmailForAppointmentCancellation(String studentEmail, String tutorEmail, String appointmrntDay, String studentName, String tutorName, String timeFrom, String timeTo)
    {
        //Getting content for email
        String studentEmailAddress = studentEmail;//Here put the user email.
        String studentSubject = "FahemApp appointment cancellation"; //Here you put the subject of the message.
        String studentMessage = "Hello, \n\nWe would like to inform you that your appointment at\n" + appointmrntDay +
                "\nFrom: " + timeFrom + "\nTo: " + timeTo + "\nWith " + tutorName + "\nhas been canceled." + "\n\n" +
                "Thank you.\n\n" + "This an automatic message form FahemApp.";//Here you put the body of the message.


        //Getting content for email
        String tutorEmailAddress = tutorEmail;//Here put the user email.
        String tutorSubject = "FahemApp appointment cancellation"; //Here you put the subject of the message.
        String tutorMessage = "Hello, \n\nWe would like to inform you that your appointment at\n" + appointmrntDay +
                "\nFrom: " + timeFrom + "\nTo: " + timeTo + "\nWith " + studentName + "\nhas been canceled." + "\n\n" +
                "Thank you.\n\n" + "This an automatic message form FahemApp.";//Here you put the body of the message.


        //Creating SendMail object
        SendMail sendEmailToStudent = new SendMail(this, studentEmailAddress, studentSubject, studentMessage);
        SendMail sendEmailToTutor = new SendMail(this, tutorEmailAddress, tutorSubject, tutorMessage);

        //Executing sendmail to send email
        sendEmailToStudent.execute();
        sendEmailToTutor.execute();
    }

    private void gettingStudentAndTutorEmails()
    {
        Query studentQuery = FirebaseDatabase.getInstance().getReference("Accounts/Students").orderByChild("accountId").equalTo(stringStudentId);
        studentQuery.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    studentEmail = dataSnapshot.getChildren().iterator().next().getValue(Student.class).getEmail();
                    System.out.println("===================THIS IS STUDENT EMAIL: " + studentEmail + "===============================");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        Query tutorQuery = FirebaseDatabase.getInstance().getReference("Accounts/Tutors").orderByChild("accountId").equalTo(stringTutorId);
        tutorQuery.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    tutorEmail = dataSnapshot.getChildren().iterator().next().getValue(Tutor.class).getEmail();
                    System.out.println("===================THIS IS TUTOR EMAIL: " + tutorEmail + "===============================");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public boolean checkAppointmentTime()
    {
        Calendar calendar = Calendar.getInstance();
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DATE = calendar.get(Calendar.DATE);
        final int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        final int MINUTE = calendar.get(Calendar.MINUTE);

        String date = stringAppointmentDate;
        String day = date.substring(0,2);
        String month = date.substring(5,7);
        String year = date.substring(10);

        int intDay = Integer.parseInt(day);
        int intMonth = Integer.parseInt(month);
        int intYear = Integer.parseInt(year);

        if((DATE == intDay) && ((MONTH+1) == intMonth) && (YEAR == intYear))
        {
            String appointmenttimeHour = stringAppointmentTimeFrom.substring(0,2).replaceAll(":","").replaceAll(" ","");
            String appointmenttimeMinuts = stringAppointmentTimeFrom.substring(4).replaceAll(":","").replaceAll(" ","");

            int appointmentHour = Integer.parseInt(appointmenttimeHour);
            int appointmentMinute = Integer.parseInt(appointmenttimeMinuts);

            if(appointmentHour-1 <= HOUR)
            {
                if(appointmentMinute >= MINUTE)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private String getUserNumber()
    {
        if(MainActivity.studentUserType)
        {
            Query studentQuery = FirebaseDatabase.getInstance().getReference("Accounts/Students").orderByChild("accountId").equalTo(stringStudentId);
            studentQuery.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        userNumber = dataSnapshot.getChildren().iterator().next().getValue(Student.class).getPhoneNumber();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        if(MainActivity.tutorUserType)
        {
            Query tutorQuery = FirebaseDatabase.getInstance().getReference("Accounts/Tutors").orderByChild("accountId").equalTo(stringTutorId);
            tutorQuery.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        userNumber = dataSnapshot.getChildren().iterator().next().getValue(Tutor.class).getEmail();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        System.out.println("===================THIS IS USER PHONE: " + userNumber + "===============================");
        return userNumber;
    }

    private void makePhoneCall()
    {
        String number = userNumber;

        if(!number.isEmpty())
        {
            if(ContextCompat.checkSelfPermission(AppointmentDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(AppointmentDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }else
            {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == REQUEST_CALL)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                makePhoneCall();
            }else
            {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
