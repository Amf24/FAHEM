package com.example.gradproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradproject.AppointmentDetailsActivity;
import com.example.gradproject.R;
import com.example.gradproject.data.Appointment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AppointmentFragment extends Fragment
{
    @Nullable
    TextView appointmentsNote;
    RecyclerView appointmentsRecyclerView;
    private FirebaseAuth mAuth;
    boolean appointmentDateValidationResult;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_appointment,container,false);
        appointmentsNote = rootView.findViewById(R.id.appointments_note);
        appointmentsRecyclerView = rootView.findViewById(R.id.appointments_recyclerview);
        appointmentsRecyclerView.setHasFixedSize(true);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();

        Query query = FirebaseDatabase.getInstance().getReference("Appointments").child(mAuth.getCurrentUser().getUid()).orderByChild("appointmentTimestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    appointmentsNote.setVisibility(View.GONE);
                }else
                {
                    appointmentsNote.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        checkAppointmentDate();
        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference("Appointments").child(mAuth.getCurrentUser().getUid()).orderByChild("appointmentTimestamp");
        FirebaseRecyclerAdapter<Appointment, AppointmentFragment.AppointmentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Appointment, AppointmentFragment.AppointmentViewHolder>(Appointment.class, R.layout.appointment_card, AppointmentFragment.AppointmentViewHolder.class, query)
        {
            @Override
            protected void populateViewHolder(AppointmentFragment.AppointmentViewHolder appointmentViewHolder, final Appointment appointment, int i)
            {
                appointmentViewHolder.setAppointmentDay(appointment.getAppointmentDay());
                appointmentViewHolder.setAppointmentDate(appointment.getAppointmentDate());
                appointmentViewHolder.setAppointmentImage();

                appointmentViewHolder.rowLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getContext(), AppointmentDetailsActivity.class);

                        intent.putExtra("appointmentId",appointment.getAppointmentId());
                        intent.putExtra("appointmentDay",appointment.getAppointmentDay());
                        intent.putExtra("appointmentDate",appointment.getAppointmentDate());
                        intent.putExtra("appointmentTimeFrom",appointment.getAppointmentTimeFrom());
                        intent.putExtra("appointmentTimeTo",appointment.getAppointmentTimeTo());
                        intent.putExtra("appointmentPlace",appointment.getAppointmentPlace());
                        intent.putExtra("appointmentPrice",appointment.getAppointmentPrice());
                        intent.putExtra("appointmentStatus",appointment.getAppointmentStatus());
                        intent.putExtra("appointmentCourse",appointment.getAppointmentCourse());
                        intent.putExtra("appointmentTutorName",appointment.getTutorName());
                        intent.putExtra("tutorId",appointment.getTutorId());
                        intent.putExtra("appointmentStudentName",appointment.getStudentName());
                        intent.putExtra("studentId",appointment.getStudentId());
                        intent.putExtra("appointmentTimestamp", appointment.getAppointmentTimestamp());

                        getContext().startActivity(intent);
                    }
                });
            }
        };

        appointmentsRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ConstraintLayout rowLayout;

        public AppointmentViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            rowLayout = itemView.findViewById(R.id.recyclerview_row_appointments);
        }

        public void setAppointmentDay(String day)
        {
            TextView dayTv = mView.findViewById(R.id.appointment_text);
            int indexOfcoma = day.indexOf(',');
            dayTv.setText(day.substring(0,indexOfcoma));
        }

        public void setAppointmentDate(String date)
        {
            TextView dateTv = mView.findViewById(R.id.appointment_details_text);
            dateTv.setText(date);
        }

        public void setAppointmentImage()
        {
            ImageView imageView = mView.findViewById(R.id.myImage);
            imageView.setImageResource(R.drawable.ic_access_time_black_24dp);
        }
    }

    public boolean checkAppointmentDate()
    {
        Calendar calendar = Calendar.getInstance();
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DATE = calendar.get(Calendar.DATE);
        final int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        final int MINUTE = calendar.get(Calendar.MINUTE);

        appointmentDateValidationResult = true;

        Query query = FirebaseDatabase.getInstance().getReference("Appointments").child(mAuth.getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Appointment appointment = snapshot.getValue(Appointment.class);
                        String date = appointment.getAppointmentDate();

                        String sessionKey = snapshot.getKey();//because the appointment is saved under the session that got booked and created an appointment.

                        DatabaseReference AppointmentDatabase = FirebaseDatabase.getInstance().getReference("Appointments/").child(mAuth.getCurrentUser().getUid()).child(sessionKey);

                        String day = date.substring(0,2);
                        String month = date.substring(5,7);
                        String year = date.substring(10);

                        int intDay = Integer.parseInt(day);
                        int intMonth = Integer.parseInt(month);
                        int intYear = Integer.parseInt(year);

                        if(YEAR > intYear)
                        {
                            appointmentDateValidationResult = false;
                            AppointmentDatabase.removeValue();
                        }

                        if((MONTH+1) > intMonth && (YEAR == intYear))
                        {
                            appointmentDateValidationResult = false;
                            AppointmentDatabase.removeValue();
                        }

                        if((DATE > intDay) && ((MONTH+1) == intMonth) && (YEAR == intYear))
                        {
                            appointmentDateValidationResult = false;
                            AppointmentDatabase.removeValue();
                        }

                        if((DATE == intDay) && ((MONTH+1) == intMonth) && (YEAR == intYear))
                        {
                            String appointmenttimeHour = appointment.getAppointmentTimeTo().substring(0,2).replaceAll(":","").replaceAll(" ","");
                            String appointmenttimeMinuts = appointment.getAppointmentTimeTo().substring(4).replaceAll(":","").replaceAll(" ","");

                            int appointmentHour = Integer.parseInt(appointmenttimeHour);
                            int appointmentMinute = Integer.parseInt(appointmenttimeMinuts);

                            String appointmenttimeHourFrom = appointment.getAppointmentTimeFrom().substring(0,2).replaceAll(":","").replaceAll(" ","");
                            int appointmentHourFrom = Integer.parseInt(appointmenttimeHourFrom);

                            if (appointmentHour <= HOUR && appointmentHourFrom < 22)
                            {
                                if(appointmentHour < HOUR)
                                {
                                    AppointmentDatabase.removeValue(); // remove the current appointment from Appointments database.

                                    DatabaseReference previousAppointmentsDatabase = FirebaseDatabase.getInstance().getReference("PreviousAppointments").child(mAuth.getCurrentUser().getUid()); // This is a reference to the user previous appointments list.

                                    String stringAppointmentStatus = "Attended";
                                    Appointment attendedAppointment = new Appointment(appointment.getAppointmentId(),appointment.getAppointmentDay(),appointment.getAppointmentDate(),appointment.getAppointmentTimeFrom(),appointment.getAppointmentTimeTo(),appointment.getAppointmentPlace(),appointment.getAppointmentPrice(),stringAppointmentStatus,appointment.getAppointmentCourse(),appointment.getTutorName(),appointment.getTutorId(),appointment.getStudentName(),appointment.getStudentId(),appointment.getAppointmentTimestamp());
                                    previousAppointmentsDatabase.child(mAuth.getCurrentUser().getUid()).setValue(attendedAppointment); // This adds attended appointment to the user previous appointments list.

                                    FirebaseDatabase.getInstance().getReference("Schedules").child(appointment.getTutorId()).child(sessionKey).removeValue(); //This removes the session which has been attended by the tutor and student.
                                }

                                if(appointmentHour == HOUR)
                                {
                                    if(appointmentMinute < MINUTE)
                                    {
                                        AppointmentDatabase.removeValue(); // remove the current appointment from Appointments database.

                                        DatabaseReference previousAppointmentsDatabase = FirebaseDatabase.getInstance().getReference("PreviousAppointments").child(mAuth.getCurrentUser().getUid()); // This is a reference to the user previous appointments list.

                                        String stringAppointmentStatus = "Attended";
                                        Appointment attendedAppointment = new Appointment(appointment.getAppointmentId(),appointment.getAppointmentDay(),appointment.getAppointmentDate(),appointment.getAppointmentTimeFrom(),appointment.getAppointmentTimeTo(),appointment.getAppointmentPlace(),appointment.getAppointmentPrice(),stringAppointmentStatus,appointment.getAppointmentCourse(),appointment.getTutorName(),appointment.getTutorId(),appointment.getStudentName(),appointment.getStudentId(),appointment.getAppointmentTimestamp());
                                        previousAppointmentsDatabase.child(appointment.getAppointmentId()).setValue(attendedAppointment); // This adds attended appointment to the user previous appointments list.

                                        FirebaseDatabase.getInstance().getReference("Schedules").child(appointment.getTutorId()).child(sessionKey).removeValue(); //This removes the session which has been attended by the tutor and student.
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

        return appointmentDateValidationResult;
    }
}

