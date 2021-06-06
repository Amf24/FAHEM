package com.example.gradproject.SessionsActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gradproject.Fragments.HomeFragmentStudent;
import com.example.gradproject.Fragments.HomeFragmentTutor;
import com.example.gradproject.R;
import com.example.gradproject.data.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityCreateSession extends AppCompatActivity
{

    Button day;
    Button timeFrom, timeTo;
    Button date;
    EditText place;
    EditText price;
    RadioGroup radioGroup;
    RadioButton hourRadioButton;
    Button createScheduleButton;
    ProgressBar createSessionProgressBar;
    long sessionTimestamp;

    Spinner courseChosen;
    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    DatabaseReference userCourseDatabase;
    boolean sessionExist = false;

    DatabaseReference scheduleDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        day = findViewById(R.id.schedule_day);
        timeFrom = findViewById(R.id.session_details_time);
        timeTo = findViewById(R.id.session_details_time2);
        date = findViewById(R.id.session_details_date);
        place = findViewById(R.id.session_details_place);
        price = findViewById(R.id.session_details_price);
        radioGroup = findViewById(R.id.HourRadioGroup);
        createScheduleButton = findViewById(R.id.create_schedule_session_btn);
        createSessionProgressBar = findViewById(R.id.check_session_existence);

        courseChosen = findViewById(R.id.session_details_course_spinner);
        spinnerDataList = new ArrayList<>();
        spinnerDataList.add("");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerDataList);


        mAuth = FirebaseAuth.getInstance();
        scheduleDatabase = FirebaseDatabase.getInstance().getReference("Schedules/" + mAuth.getCurrentUser().getUid());
        userCourseDatabase = FirebaseDatabase.getInstance().getReference("AssignedCourses").child(mAuth.getUid());


        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDate = calendar.get(Calendar.DATE);
        date.setText(currentDate + " / " + (currentMonth + 1) + " / " + currentYear);

        courseChosen.setAdapter(adapter);
        populateSpinnerWithCourses();

        createScheduleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createSessionProgressBar.setVisibility(View.VISIBLE);
                createScheduleButton.setVisibility(View.GONE);
                sessionExistenceChecking();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        createTheSchedule();
                        createSessionProgressBar.setVisibility(View.GONE);
                        createScheduleButton.setVisibility(View.VISIBLE);
                    }
                }, 3500);
            }
        });

        date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dateGetter();
            }
        });

        timeFrom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                timeGetter();
            }
        });

    }

    public void checkRadioButton(View v)
    {
        int radioId = radioGroup.getCheckedRadioButtonId();
        hourRadioButton = findViewById(radioId);
    }

    public void createTheSchedule()
    {
        final String stringDay = day.getText().toString().trim();
        final String stringTimeFrom = timeFrom.getText().toString().trim();
        final String stringTimeTo = timeTo.getText().toString().trim();
        final String stringDate = date.getText().toString().trim();
        final String stringPlace = place.getText().toString().trim();
        final String stringPrice = price.getText().toString().trim();
        final String stringCourse = courseChosen.getSelectedItem().toString().trim();
        final String stringStatus = "Available";

        if(stringDay.isEmpty())
        {
            day.setError("You have to specify a day.");
            Toast.makeText(ActivityCreateSession.this, "You have to specify a day.", Toast.LENGTH_SHORT).show();
            day.requestFocus();
            return;
        }

        if(stringTimeFrom.isEmpty())
        {
            timeFrom.setError("You have to specify a time.");
            Toast.makeText(ActivityCreateSession.this, "You have to specify a time.", Toast.LENGTH_SHORT).show();
            timeFrom.requestFocus();
            return;
        }

        if(stringCourse.isEmpty())
        {
            Toast.makeText(ActivityCreateSession.this, "You have to specify a course to be taught.", Toast.LENGTH_SHORT).show();
            courseChosen.requestFocus();
            return;
        }

        if(stringPlace.isEmpty())
        {
            place.setError("You have to specify a place");
            place.requestFocus();
            return;
        }

        if(stringPrice.isEmpty())
        {
            price.setError("You have to specify a price");
            price.requestFocus();
            return;
        }

        String timeHour = stringTimeFrom.substring(0,2).replaceAll(":","").replaceAll(" ","");
        int appointmentHour = Integer.parseInt(timeHour);

        if(appointmentHour > 21)
        {
            timeFrom.setError("Sessions Times can be from 7:00 AM to 11:59 PM.");
            Toast.makeText(ActivityCreateSession.this, "Sessions Times can be from 7:00 AM to 10:59 PM.", Toast.LENGTH_SHORT).show();
            timeFrom.requestFocus();
            return;
        }

        System.out.println("================================================ sessionExist =" + sessionExist + "===========================================================");

        if(!sessionExist)
        {
            String sessionId = scheduleDatabase.push().getKey();
            convertDateToSessionTimestamp(stringDate, stringTimeFrom);//Here the session Date and timeFrom is converted to epoch timestamp.

            Session session = new Session(sessionId, stringDay, stringDate, stringTimeFrom, stringTimeTo, stringPlace, stringPrice, stringStatus, stringCourse, sessionTimestamp);

            scheduleDatabase.child(sessionId).setValue(session);
            Toast.makeText(ActivityCreateSession.this, "Session Created Successfully.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void dateGetter()
    {
        final Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                String dateString = "";
                if(dayOfMonth > 9 && month > 9)
                    dateString  = dayOfMonth + " / " + (month + 1) + " / " + year;
                else
                {
                    if(dayOfMonth < 9 && month < 9)
                        dateString  = "0" + dayOfMonth + " / " + "0" + (month + 1) + " / " + year;

                    if(dayOfMonth > 9 && month < 9)
                        dateString  = dayOfMonth + " / " + "0" + (month + 1) + " / " + year;

                    if(dayOfMonth < 9 && month > 9)
                        dateString  = "0" + dayOfMonth + " / " + (month + 1) + " / " + year;
                }


                date.setText(dateString);

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, dayOfMonth);

                CharSequence dateCharSequence = DateFormat.format("EEEE, dd MMM yyyy", calendar1);


                day.setText(dateCharSequence);
            }
        }, YEAR, MONTH, DATE);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void timeGetter()
    {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR_OF_DAY); //int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        boolean is24HourFormat = DateFormat.is24HourFormat(this);

        int radioId = radioGroup.getCheckedRadioButtonId();
        hourRadioButton = findViewById(radioId);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                String timeStringFrom = (hourOfDay) + " : " + minute;
                String timeStringTo;

                if(hourRadioButton.getText().toString().equals("One Hour Session"))
                {
                    if((hourOfDay + 1) >= 24)
                        timeStringTo = ((hourOfDay + 1) - 24) + " : " + minute;
                    else
                        timeStringTo = (hourOfDay + 1) + " : " + minute;
                }
                else
                {
                    if((hourOfDay + 2) >= 24)
                        timeStringTo = ((hourOfDay + 2) - 24) + " : " + minute;
                    else
                        timeStringTo = (hourOfDay + 2) + " : " + minute;
                }

                timeFrom.setText(timeStringFrom);
                timeTo.setText(timeStringTo);
            }
        }, HOUR, MINUTE,is24HourFormat);

        timePickerDialog.show();
    }

    public void populateSpinnerWithCourses()
    {
        listener = userCourseDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    spinnerDataList.add(item.getValue().toString().substring(12,17));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void convertDateToSessionTimestamp(String appointmentDate, String appointmentTimeFrom)
    {
        String date = appointmentDate;
        String timeFrom = appointmentTimeFrom;

        String day = date.substring(0,2);
        String month = date.substring(5,7);
        String year = date.substring(10);

        String timeHour = timeFrom.substring(0,2).replaceAll(":","").replaceAll(" ","");
        String timeMinuts = timeFrom.substring(4).replaceAll(":","").replaceAll(" ","");

        String date1 = month + "-" + day + "-"+ year;
        String time1 = timeHour + ":" + timeMinuts;

        try
        {
            long epoch = new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm").parse(date1 + " " + time1).getTime() / 1000;
            sessionTimestamp = epoch;
        }catch (Exception e) { e.printStackTrace(); }
    }

    private void sessionExistenceChecking()
    {
        final String stringDay = day.getText().toString().trim();
        final String stringTimeFrom = timeFrom.getText().toString().trim();

        Query query = scheduleDatabase.orderByChild("sessionId");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                System.out.println("================================================INSIDE NEW -1============================================================");
                if(dataSnapshot.exists())
                {
                    System.out.println("================================================INSIDE NEW 0============================================================");
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Session session = snapshot.getValue(Session.class);
                        System.out.println("================================================INSIDE NEW 1============================================================");

                        if(session.getDay().equals(stringDay) && session.getTimeFrom().equals(stringTimeFrom))
                        {
                            System.out.println("================================================INSIDE NEW 3============================================================");
                            sessionExist = true;
                            Toast.makeText(ActivityCreateSession.this, "Session Already Exist.", Toast.LENGTH_LONG).show();
                            return;
                        }else
                        {
                            sessionExist = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
