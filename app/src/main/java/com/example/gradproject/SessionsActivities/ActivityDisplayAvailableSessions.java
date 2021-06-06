package com.example.gradproject.SessionsActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.gradproject.R;
import com.example.gradproject.data.Appointment;
import com.example.gradproject.data.Session;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ActivityDisplayAvailableSessions extends AppCompatActivity
{
    DatabaseReference scheduleDatabase;
    String tutorId, tutorName;

    TextView tutorSessionsNote;
    RecyclerView displayedSessionsRecyclerView;

    boolean dateValidationResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_available_sessions);

        Intent intent = getIntent();

        tutorId = intent.getStringExtra("tutorId");
        tutorName = intent.getStringExtra("tutorName");

        scheduleDatabase = FirebaseDatabase.getInstance().getReference("Schedules/" + tutorId);
        scheduleDatabase.keepSynced(true);

        tutorSessionsNote = findViewById(R.id.tutor_sessoins_note);
        displayedSessionsRecyclerView = findViewById(R.id.available_sessions_recycler_view);
        displayedSessionsRecyclerView.setHasFixedSize(true);
        displayedSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkSessionsDate();
        checkIfTutorHasAvailableSessions();
    }

    public void checkIfTutorHasAvailableSessions()
    {
        /*
        * showing the available appointments can be done in two ways
        * ethier show just the available ones and the dates of them is not in order
        * or the other way around, ordered dates but booked sessions is shown as well
        * 1st way: FirebaseDatabase.getInstance().getReference("Schedules/" + tutorId).orderByChild("sessionTimestamp");
        * 2sd way: FirebaseDatabase.getInstance().getReference("Schedules/" + tutorId).orderByChild("status").equalTo("Available");
        * */

        final Query query = FirebaseDatabase.getInstance().getReference("Schedules/" + tutorId).orderByChild("status").equalTo("Available");//orderByChild("sessionTimestamp");

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() != null)
                {
                    FirebaseRecyclerAdapter<Session, ActivityDisplayAvailableSessions.SessionViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Session, ActivityDisplayAvailableSessions.SessionViewHolder>(Session.class, R.layout.session_card, ActivityDisplayAvailableSessions.SessionViewHolder.class, query)
                    {
                        @Override
                        protected void populateViewHolder(ActivityDisplayAvailableSessions.SessionViewHolder sessionViewHolder, final Session session, int i)
                        {
                            sessionViewHolder.setSessionDay(session.getDay());
                            sessionViewHolder.setSessionDate(session.getDate());

                            sessionViewHolder.rowLayout.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = new Intent(ActivityDisplayAvailableSessions.this, ActivitySessionDetails.class);

                                    intent.putExtra("sessionId",session.getSessionId());
                                    intent.putExtra("sessionDay",session.getDay());
                                    intent.putExtra("sessionDate",session.getDate());
                                    intent.putExtra("sessionTimeFrom",session.getTimeFrom());
                                    intent.putExtra("sessionTimeTo",session.getTimeTo());
                                    intent.putExtra("sessionPlace",session.getPlace());
                                    intent.putExtra("sessionPrice",session.getPrice());
                                    intent.putExtra("sessionStatus",session.getStatus());
                                    intent.putExtra("sessionCourse",session.getSessionCourse());
                                    intent.putExtra("sessionTimestamp", session.getSessionTimestamp());

                                    intent.putExtra("tutorId", tutorId);
                                    intent.putExtra("tutorName", tutorName);

                                    ActivityDisplayAvailableSessions.this.startActivity(intent);
                                }
                            });
                        }
                    };

                    displayedSessionsRecyclerView.setAdapter(firebaseRecyclerAdapter);
                }
                else
                {
                    tutorSessionsNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
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
        final int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        final int MINUTE = calendar.get(Calendar.MINUTE);

        dateValidationResult = true;

        scheduleDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Session session = snapshot.getValue(Session.class);
                        String date = session.getDate();
                        DatabaseReference sessionDatabase = FirebaseDatabase.getInstance().getReference("Schedules/").child(tutorId).child(session.getSessionId());

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

                        if(DATE == intDay && (MONTH+1) == intMonth && (YEAR == intYear))
                        {
                            String appointmenttimeHour = session.getTimeTo().substring(0,2).replaceAll(":","").replaceAll(" ","");
                            String appointmenttimeMinuts = session.getTimeTo().substring(4).replaceAll(":","").replaceAll(" ","");

                            int appointmentHour = Integer.parseInt(appointmenttimeHour);
                            int appointmentMinute = Integer.parseInt(appointmenttimeMinuts);

                            String appointmenttimeHourFrom = session.getTimeFrom().substring(0,2).replaceAll(":","").replaceAll(" ","");
                            int appointmentHourFrom = Integer.parseInt(appointmenttimeHourFrom);

                            if (appointmentHour <= HOUR && appointmentHourFrom < 22)
                            {
                                if(appointmentHour < HOUR)
                                {
                                    sessionDatabase.removeValue(); // remove the current session from sessions database.
                                }

                                if(appointmentHour == HOUR)
                                {
                                    if(appointmentMinute < MINUTE)
                                    {
                                        sessionDatabase.removeValue(); // remove the current session from sessions database.
                                    }
                                }

                            }else if(appointmentHour <= HOUR && appointmentHourFrom > 22)//Here if the appointment is from 22 pm and over which means it will enter a new day.
                            {
                                /*
                                 *Here there is a case: -
                                 * if the user book a two session at 23:00 it will finish at 1:00,
                                 * and the appointment will be considered attended but in fact no one attend the session !!.
                                 * */


                                //Write the wanted scenario here.
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return dateValidationResult;
    }
}
