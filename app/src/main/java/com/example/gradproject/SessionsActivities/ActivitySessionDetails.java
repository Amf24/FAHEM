package com.example.gradproject.SessionsActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gradproject.EmailSender.SendMail;
import com.example.gradproject.MainActivity;
import com.example.gradproject.Notifications.TutorRatingNotification;
import com.example.gradproject.R;
import com.example.gradproject.Notifications.ReminderNotification;
import com.example.gradproject.data.Appointment;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivitySessionDetails extends AppCompatActivity {

    TextView sessionDate, sessionTimeFrom, sessionTimeTo, sessionPlace, sessionPrice, sessionStatus, sessionCourse;
    String stringSessionId, stringSessionDay, stringSessionDate, stringSessionTimeFrom, stringSessionTimeTo, stringSessoionPlace, stringSessionPrice, stringSessionStatus, stringSessionCourse;
    long longSessionTimestamp;
    Button deleteSessionButton, bookSessionButton;
    String studentName;
    private FirebaseAuth mAuth;
    String userId = "";

    String studentEmail, tutorEmail;

    //public static AlarmManager alarmManager; //------
    //public static PendingIntent broadcast; //------

    public static Intent appointmentDetailsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        sessionDate = findViewById(R.id.session_date_info);
        sessionTimeFrom = findViewById(R.id.session_time_info_from);
        sessionTimeTo = findViewById(R.id.session_time_info_to);
        sessionPlace = findViewById(R.id.session_place_info);
        sessionPrice = findViewById(R.id.session_price_info);
        sessionStatus = findViewById(R.id.session_status_info);
        sessionCourse = findViewById(R.id.session_course_info);
        deleteSessionButton = findViewById(R.id.delete_session_button);
        bookSessionButton = findViewById(R.id.book_session_button);

        mAuth = FirebaseAuth.getInstance();
        userId =  mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();

        stringSessionId = intent.getStringExtra("sessionId");
        stringSessionDate = intent.getStringExtra("sessionDate");
        stringSessionDay = intent.getStringExtra("sessionDay");
        stringSessionTimeFrom = intent.getStringExtra("sessionTimeFrom");
        stringSessionTimeTo = intent.getStringExtra("sessionTimeTo");
        stringSessoionPlace = intent.getStringExtra("sessionPlace");
        stringSessionPrice = intent.getStringExtra("sessionPrice");
        stringSessionStatus = intent.getStringExtra("sessionStatus");
        stringSessionCourse = intent.getStringExtra("sessionCourse");
        longSessionTimestamp = intent.getLongExtra("sessionTimestamp",0);

        sessionDate.setText(stringSessionDate);
        sessionTimeFrom.setText(stringSessionTimeFrom);
        sessionTimeTo.setText(stringSessionTimeTo);
        sessionPlace.setText(stringSessoionPlace);
        sessionPrice.setText(stringSessionPrice);
        sessionCourse.setText(stringSessionCourse);
        sessionStatus.setText(stringSessionStatus);

        if(stringSessionStatus.equals("Booked"))
            sessionStatus.setTextColor(Color.parseColor("#FF0000"));
        else
            sessionStatus.setTextColor(Color.parseColor("#FF009688"));

        if(MainActivity.tutorUserType)
        {
            bookSessionButton.setVisibility(View.GONE);

            if(stringSessionStatus.equals("Booked"))
                deleteSessionButton.setVisibility(View.GONE);
            else
                deleteSessionButton.setVisibility(View.VISIBLE);

            deleteSessionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteSession();
                }
            });
        }else if(MainActivity.studentUserType)
        {
            deleteSessionButton.setVisibility(View.GONE);
            bookSessionButton.setVisibility(View.VISIBLE);
            getStudentName();

            bookSessionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    updateSessionStatusToBooked();
                }
            });
        }

        gettingStudentAndTutorEmails(userId, intent.getStringExtra("tutorId"));
    }

    public void deleteSession()
    {
        DatabaseReference sessionDatabase = FirebaseDatabase.getInstance().getReference("Schedules/").child(userId).child(stringSessionId);
        sessionDatabase.removeValue();
        Toast.makeText(this, "Session Deleted",Toast.LENGTH_LONG).show();
        finish();
    }

    public void updateSessionStatusToBooked()
    {
        Intent intent = getIntent();
        String tutorId = intent.getStringExtra("tutorId");
        String tutorName = intent.getStringExtra("tutorName");
        DatabaseReference sessionsDatabase = FirebaseDatabase.getInstance().getReference("Schedules").child(tutorId).child(stringSessionId);

        stringSessionStatus = "Booked";

        sessionsDatabase.removeValue();
        Session session = new Session(stringSessionId,stringSessionDay,stringSessionDate,stringSessionTimeFrom, stringSessionTimeTo,stringSessoionPlace, stringSessionPrice,stringSessionStatus,  stringSessionCourse, longSessionTimestamp);
        sessionsDatabase.setValue(session);

        Toast.makeText(this, "Session Booked",Toast.LENGTH_LONG).show();

        createAppointment(tutorName, studentName, userId, tutorId, stringSessionId);
        gettingStudentAndTutorEmails(userId, tutorId);
        finish();
    }

    public void createAppointment(String tutorName, String sutdentName, String studentId, String tutorId, String sessionId)
    {
        DatabaseReference appointmentDatabaseStudent = FirebaseDatabase.getInstance().getReference("Appointments").child(studentId);
        DatabaseReference appointmentDatabaseTutor = FirebaseDatabase.getInstance().getReference("Appointments").child(tutorId);

        String appointmentId = appointmentDatabaseStudent.push().getKey();

        Appointment appointment = new Appointment(appointmentId,stringSessionDay,stringSessionDate,stringSessionTimeFrom,stringSessionTimeTo,stringSessoionPlace,stringSessionPrice,stringSessionStatus,stringSessionCourse,tutorName,tutorId,sutdentName,studentId,longSessionTimestamp);

        appointmentDatabaseStudent.child(sessionId).setValue(appointment);
        appointmentDatabaseTutor.child(sessionId).setValue(appointment);

        createReminderNotification(stringSessionDate, stringSessionTimeFrom, longSessionTimestamp);
        createTutorRatingNotification(stringSessionDate, stringSessionTimeTo);


        sendEmailForAppointmentBooking(studentEmail, tutorEmail, stringSessionDay,studentName, tutorName, stringSessionTimeFrom, stringSessionTimeTo);
    }

    public void createReminderNotification(String appointmentDate, String appointmentTimeFrom, long appointmentTimestamp)
    {
        Calendar calendar = Calendar.getInstance();
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DATE = calendar.get(Calendar.DATE);

        int HOUR = calendar.get(Calendar.HOUR_OF_DAY); //int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        String date = appointmentDate;
        String timeFrom = appointmentTimeFrom;

        String day = date.substring(0,2);
        String month = date.substring(5,7);
        String year = date.substring(10);

        String timeHour = timeFrom.substring(0,2).replaceAll(":","").replaceAll(" ","");
        String timeMinuts = timeFrom.substring(4).replaceAll(":","").replaceAll(" ","");

        int diffhours = 0;
        int diffDays = 0;
        int diffmin = 0;

        try
        {
            String date1 = month + "-" + day + "-"+ year;
            String time1 = timeHour + ":" + timeMinuts;

            String date2 = (MONTH+1) + "-" + DATE + "-"+ YEAR;
            String time2 =  HOUR + ":" + MINUTE;

            String format = "MM-dd-yyyy HH:mm";

            SimpleDateFormat sdf = new SimpleDateFormat(format);

            Date dateObj1 = sdf.parse( date1 + " " + time1);
            Date dateObj2 = sdf.parse(date2 + " " + time2);
            System.out.println("DateObj1 Appointment = " + dateObj1);
            System.out.println("DateObj1 Current =" + dateObj2 + "\n");

            System.out.println("==================================================================");

            DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");

            //getTime() returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object
            long diff = dateObj1.getTime() - dateObj2.getTime();

            diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            System.out.println("========================difference between days: " + diffDays +"========================");

            diffhours = (int) (diff / (60 * 60 * 1000));
            System.out.println("========================difference between hours: " + crunchifyFormatter.format(diffhours) + "=====diffhours:"+diffhours+"=======HOUR="+HOUR+"===========");

            diffmin = (int) (diff / (60 * 1000));
            System.out.println("========================difference between minutues: " + crunchifyFormatter.format(diffmin) + "========================");

            long currentEpoch = System.currentTimeMillis()/1000;
            long appointmentEpoch = new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm").parse(date1 + " " + time1).getTime() / 1000;

            System.out.println("========================appointmentEpoch " + appointmentEpoch +"=======currentEpoc "+ currentEpoch +"=================");
        }catch (Exception e)
        {
            System.out.println("=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=");
            e.printStackTrace();
        }


        System.out.println("=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=");

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent notificationIntent = new Intent(this, ReminderNotification.class);

        /* notificationId is used to identify every and each of the notification,
        as well as relate every notification with its appointment.
        the id consist of (the number of the day + number of the staring hour).
        */

        int notificationId = middleTFiveDigits(appointmentTimestamp); // id here is middle four digits of the timestamp.
        //int notificationId = Integer.parseInt(day) + Integer.parseInt(timeHour);  //The id is consisting of (day number + time from hour number). Ex: day 25, Time from : 13. id is : (25+13) = 38.

        System.out.println("===================== notificationId: " + notificationId + "======================");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.SECOND, 5); //5 Seconds

        //Here time becomes in the same day of the appointment.
        //WE USED DIFFERENCE IN MINUTES SO IT IS MORE ACCURATE NOW :).
        cal.add(Calendar.MINUTE, diffmin-59); //diffmin is the difference between the current time and the appointment time in minutes minus 60 to remind user before one hour of the appointment time.

        System.out.println("=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=");
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }

    public void createTutorRatingNotification(String appointmentDate, String appointmentTimeTo)
    {
        /*
        * RATING NOTIFICATION HAS THE SAME CONCEPT AS REMINDER NOTIFICATION BUT INSTEAD OF USING TIME FROM
        * WE ARE GOING TO USE TIME TO.
        * WE USED DIFFERENCE IN MINUTES SO IT IS MORE ACCURATE NOW :).
        */

        Calendar calendar = Calendar.getInstance();
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DATE = calendar.get(Calendar.DATE);

        int HOUR = calendar.get(Calendar.HOUR_OF_DAY); //int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        String date = appointmentDate;
        String timeTo = appointmentTimeTo;

        String day = date.substring(0,2);
        String month = date.substring(5,7);
        String year = date.substring(10);

        String timeHour = timeTo.substring(0,2).replaceAll(":","").replaceAll(" ","");
        String timeMinuts = timeTo.substring(4).replaceAll(":","").replaceAll(" ","");

        int diffhours = 0;
        int diffDays = 0;
        int diffmin = 0;

        try
        {
            String date1 = month + "-" + day + "-"+ year;
            String time1 = timeHour + ":" + timeMinuts;

            String date2 = (MONTH+1) + "-" + DATE + "-"+ YEAR;
            String time2 =  HOUR + ":" + MINUTE;

            String format = "MM-dd-yyyy HH:mm";

            SimpleDateFormat sdf = new SimpleDateFormat(format);

            Date dateObj1 = sdf.parse( date1 + " " + time1);
            Date dateObj2 = sdf.parse(date2 + " " + time2);

            DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");

            //getTime() returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object
            long diff = dateObj1.getTime() - dateObj2.getTime();

            diffDays = (int) (diff / (24 * 60 * 60 * 1000));

            diffhours = (int) (diff / (60 * 60 * 1000));

            diffmin = (int) (diff / (60 * 1000));
            System.out.println("==========Rating==============difference between minutues: " + crunchifyFormatter.format(diffmin) + "========================");

        }catch (Exception e) { e.printStackTrace(); }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent notificationIntent = new Intent(this, TutorRatingNotification.class);

        /* notificationId is used to identify every and each of the notification,
        as well as relate every notification with its appointment.
        the id consist of (the number of the day + number of the staring hour).
        */

        int notificationId = Integer.parseInt(day) + Integer.parseInt(timeHour); //The id is consisting of (day number + time to hour number). Ex: day 25, Time to : 13. id is : (25+13) = 38.
        PendingIntent broadcast = PendingIntent.getBroadcast(this, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.SECOND, 10); //10 Seconds

        //Here time becomes in the same day of the appointment.
        //WE USED DIFFERENCE IN MINUTES SO IT IS MORE ACCURATE NOW :).
        cal.add(Calendar.MINUTE, diffmin); //diffmin is the difference between the current time and the appointment time in minutes, and here we want to remind the user at the exact time of the appointment end.
        System.out.println("==========Rating2222==============difference between minutues: " + diffmin + "========================");
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }


    public static int middleTFiveDigits(long timestamp)
    {
        String s = String.valueOf(timestamp);
        s = s.substring(3,8);
        return Integer.parseInt(s);
    }

    public void getStudentName()
    {
        Query getStudentName = FirebaseDatabase.getInstance().getReference("Accounts/Students").orderByChild("accountId").equalTo(userId);
        getStudentName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Student student = dataSnapshot.getChildren().iterator().next().getValue(Student.class);
                    studentName = student.getFullName();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void gettingStudentAndTutorEmails(String stringStudentId, String stringTutorId)
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

    private void sendEmailForAppointmentBooking(String studentEmail, String tutorEmail, String appointmrntDay, String studentName, String tutorName, String timeFrom, String timeTo)
    {

        System.out.println("=======22============THIS IS STUDENT EMAIL: " + studentEmail + "===============================");
        System.out.println("========22===========THIS IS TUTOR EMAIL: " + tutorEmail + "===============================");

        //Getting content for email
        String studentEmailAddress = studentEmail;//Here put the user email.
        String studentSubject = "FahemApp appointment booking"; //Here you put the subject of the message.
        String studentMessage = "Hello, \n\nWe would like to inform you that your appointment at\n" + appointmrntDay +
                "\nFrom: " + timeFrom + "\nTo: " + timeTo + "\nWith " + tutorName + "\nhas been booked." + "\n\n" +
                "Thank you.\n\n" + "This an automatic message form FahemApp.";//Here you put the body of the message.


        //Getting content for email
        String tutorEmailAddress = tutorEmail;//Here put the user email.
        String tutorSubject = "FahemApp appointment booking"; //Here you put the subject of the message.
        String tutorMessage = "Hello, \n\nWe would like to inform you that your appointment at\n" + appointmrntDay +
                "\nFrom: " + timeFrom + "\nTo: " + timeTo + "\nWith " + studentName + "\nhas been booked." + "\n\n" +
                "Thank you.\n\n" + "This an automatic message form FahemApp.";//Here you put the body of the message.


        //Creating SendMail object
        SendMail sendEmailToStudent = new SendMail(this, studentEmailAddress, studentSubject, studentMessage);
        SendMail sendEmailToTutor = new SendMail(this, tutorEmailAddress, tutorSubject, tutorMessage);

        //Executing sendmail to send email
        sendEmailToStudent.execute();
        sendEmailToTutor.execute();
    }
}
