package com.example.gradproject.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gradproject.Notifications.ReminderNotification;
import com.example.gradproject.SessionsActivities.ActivityCreateSession;
import com.example.gradproject.SessionsActivities.ActivitySessionDetails;
import com.example.gradproject.R;
import com.example.gradproject.data.Session;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import static android.content.Context.ALARM_SERVICE;

public class HomeFragmentTutor extends Fragment
{

    TextView createScheduleNote, addCoursesNote;
    Button createScheduleButton;
    FloatingActionButton floatingAddButton;
    Toolbar toolbar;
    DatabaseReference scheduleDatabase, coursesDatabase;
    private FirebaseAuth mAuth;
    boolean noAssignedCourses = false;

    RecyclerView scheduleRecyclerView;

    boolean dateValidationResult;
    static boolean  setRemidersWhenSrart = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_home_tutor,container,false);


        createScheduleNote = rootView.findViewById(R.id.create_schedule_tv);
        addCoursesNote = rootView.findViewById(R.id.please_add_courses_tutor);
        createScheduleButton = rootView.findViewById(R.id.create_schedule_btn);
        floatingAddButton = rootView.findViewById(R.id.floating_add_button);

        toolbar = rootView.findViewById(R.id.tutor_search_toolbar);
        toolbar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        scheduleDatabase = FirebaseDatabase.getInstance().getReference("Schedules/" + mAuth.getCurrentUser().getUid());
        scheduleDatabase.keepSynced(true);

        coursesDatabase = FirebaseDatabase.getInstance().getReference("AssignedCourses/" + mAuth.getCurrentUser().getUid());

        scheduleRecyclerView = rootView.findViewById(R.id.sessions_recycler_view);
        scheduleRecyclerView.setHasFixedSize(true);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        createScheduleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), ActivityCreateSession.class);
                startActivity(intent);
            }
        });

        floatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityCreateSession.class);
                startActivity(intent);
            }
        });

        checkUserCourses();
        scheduleDatabase.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(checkUserCourses())
                    {
                        addCoursesNote.setVisibility(View.VISIBLE);
                        createScheduleNote.setVisibility(View.GONE);
                        createScheduleButton.setVisibility(View.GONE);

                    }else
                    {
                        if(dataSnapshot.getValue() == null)
                        {
                            createScheduleNote.setVisibility(View.VISIBLE);
                            createScheduleButton.setVisibility(View.VISIBLE);
                            addCoursesNote.setVisibility(View.GONE);
                            floatingAddButton.setVisibility(View.GONE);
                        }else
                        {
                            createScheduleNote.setVisibility(View.GONE);
                            createScheduleButton.setVisibility(View.GONE);
                            floatingAddButton.setVisibility(View.VISIBLE);
                            addCoursesNote.setVisibility(View.GONE);
                            toolbar.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

        checkSessionsDate();
        if(setRemidersWhenSrart)
        {
            checkBookedSessionsForReminder();
            setRemidersWhenSrart = false;
        }
        return rootView;
    }

    public void onStart()
    {
        super.onStart();
        Query query = scheduleDatabase.orderByChild("sessionTimestamp");

        FirebaseRecyclerAdapter<Session, HomeFragmentTutor.SessionViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Session, HomeFragmentTutor.SessionViewHolder>(Session.class, R.layout.session_card, HomeFragmentTutor.SessionViewHolder.class, query)
        {
            @Override
            protected void populateViewHolder(HomeFragmentTutor.SessionViewHolder sessionViewHolder, final Session session, int i)
            {
                sessionViewHolder.setSessionDay(session.getDay());
                sessionViewHolder.setSessionDate(session.getDate());

                sessionViewHolder.rowLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getContext(), ActivitySessionDetails.class);

                        intent.putExtra("sessionId",session.getSessionId());
                        intent.putExtra("sessionDay",session.getDay());
                        intent.putExtra("sessionDate",session.getDate());
                        intent.putExtra("sessionTimeFrom",session.getTimeFrom());
                        intent.putExtra("sessionTimeTo",session.getTimeTo());
                        intent.putExtra("sessionPlace",session.getPlace());
                        intent.putExtra("sessionPrice",session.getPrice());
                        intent.putExtra("sessionStatus",session.getStatus());
                        intent.putExtra("sessionCourse",session.getSessionCourse());

                        getContext().startActivity(intent);
                    }
                });
            }
        };

        scheduleRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ConstraintLayout rowLayout;

        public SessionViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            rowLayout = itemView.findViewById(R.id.recyclerview_row_session);
        }

        public void setSessionDay(String day)
        {
            TextView dayTv = mView.findViewById(R.id.session_day);
            int indexOfcoma = day.indexOf(',');
            dayTv.setText(day.substring(0,indexOfcoma));
        }

        public void setSessionDate(String date)
        {
            TextView dateTv = mView.findViewById(R.id.session_date);
            dateTv.setText(date);
        }
    }

    public boolean checkSessionsDate()
    {
        Calendar calendar = Calendar.getInstance();
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DATE = calendar.get(Calendar.DATE);
        dateValidationResult = true;

        scheduleDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Session session = snapshot.getValue(Session.class);
                        String date = session.getDate();
                        DatabaseReference sessionDatabase = FirebaseDatabase.getInstance().getReference("Schedules/").child(mAuth.getCurrentUser().getUid()).child(session.getSessionId());

                        String day = date.substring(0,2);
                        String month = date.substring(5,7);
                        String year = date.substring(10);

                        int intDay = Integer.parseInt(day);
                        int intMonth = Integer.parseInt(month);
                        int intYear = Integer.parseInt(year);

                        if(YEAR > intYear)
                        {
                            dateValidationResult = false;
                            sessionDatabase.removeValue();
                        }

                        if((MONTH+1) > intMonth && (YEAR == intYear))
                        {
                            dateValidationResult = false;
                            sessionDatabase.removeValue();
                        }

                        if(DATE > intDay && (MONTH+1) == intMonth && (YEAR == intYear))
                        {
                            dateValidationResult = false;
                            sessionDatabase.removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return dateValidationResult;
    }

    public void checkBookedSessionsForReminder() //Here it checks all the tutor booked sessions and set a reminder for it.
    {
        /*
        * Here the concept is that if the tutor log in into his account the application check if
        * there is booked sessions and then create an appointments for each one,
        * if time the app is "lunched" with tutor account reminders are created,
        * even if they have been created before it will over write them. */

        final Query query = FirebaseDatabase.getInstance().getReference("Schedules/" + mAuth.getCurrentUser().getUid()).orderByChild("status").equalTo("Booked");//orderByChild("sessionTimestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Session s = snapshot.getValue(Session.class);
                        createReminderForBookedSessions(s.getDate(),s.getTimeFrom());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void createReminderForBookedSessions(String date, String timeFrom)
    {
        Calendar calendar = Calendar.getInstance();
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DATE = calendar.get(Calendar.DATE);

        final int HOUR = calendar.get(Calendar.HOUR_OF_DAY); //int HOUR = calendar.get(Calendar.HOUR);
        final int MINUTE = calendar.get(Calendar.MINUTE);

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

            DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");

            //getTime() returns the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this Date object
            long diff = dateObj1.getTime() - dateObj2.getTime();

            diffDays = (int) (diff / (24 * 60 * 60 * 1000));
            System.out.println("==============HomeFragmentTutor==========difference between days: " + diffDays +"========================");

            diffhours = (int) (diff / (60 * 60 * 1000));
            System.out.println("===============HomeFragmentTutor=========difference between hours: " + crunchifyFormatter.format(diffhours) + "=====diffhours:"+diffhours+"=======HOUR="+HOUR+"===========");

            diffmin = (int) (diff / (60 * 1000));
        }catch (Exception e)
        {
            System.out.println("=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=2=");
            e.printStackTrace();
        }

        if(getContext() != null)
        {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);

            Intent notificationIntent = new Intent(getContext(), ReminderNotification.class);

            /* notificationId is used to identify every and each of the notification,
            as well as relate every notification with its appointment.
            the id consist of (the number of the day + number of the staring hour).
             */

            int notificationId = Integer.parseInt(day) + Integer.parseInt(timeHour);
            PendingIntent broadcast = PendingIntent.getBroadcast(getContext(), notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            //cal.add(Calendar.SECOND, 8);// 8 Seconds.

            //Here time becomes in the same day of the appointment.
            cal.add(Calendar.MINUTE, diffmin-59);

            System.out.println("=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=1=");
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        }
    }


    private boolean checkUserCourses()
    {
        coursesDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() == null)
                {
                    noAssignedCourses = true;
                }else
                {
                    noAssignedCourses = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return noAssignedCourses;
    }
}
