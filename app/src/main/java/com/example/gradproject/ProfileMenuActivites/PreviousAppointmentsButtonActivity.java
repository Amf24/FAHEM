package com.example.gradproject.ProfileMenuActivites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradproject.ActivityPreviousAppointmentsDetails;
import com.example.gradproject.AppointmentDetailsActivity;
import com.example.gradproject.R;
import com.example.gradproject.data.Appointment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PreviousAppointmentsButtonActivity extends AppCompatActivity
{
    TextView previousAppointmentsNote;
    RecyclerView previousAppointmentsRecyclerView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_previous_appointments);

        previousAppointmentsNote = findViewById(R.id.previous_appointment_note);
        previousAppointmentsRecyclerView = findViewById(R.id.previous_appointments_recyclerview);
        previousAppointmentsRecyclerView.setHasFixedSize(true);
        previousAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();

        Query query = FirebaseDatabase.getInstance().getReference("PreviousAppointments").child(mAuth.getCurrentUser().getUid()).orderByChild("appointmentTimestamp");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    previousAppointmentsNote.setVisibility(View.GONE);
                }else
                {
                    previousAppointmentsNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void onStart()
    {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference("PreviousAppointments").child(mAuth.getCurrentUser().getUid()).orderByChild("appointmentTimestamp");

        FirebaseRecyclerAdapter<Appointment, PreviousAppointmentsButtonActivity.previousAppointmentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Appointment, PreviousAppointmentsButtonActivity.previousAppointmentViewHolder>(Appointment.class, R.layout.appointment_card, PreviousAppointmentsButtonActivity.previousAppointmentViewHolder.class, query)
        {
            @Override
            protected void populateViewHolder(PreviousAppointmentsButtonActivity.previousAppointmentViewHolder appointmentViewHolder, final Appointment appointment, int i)
            {
                appointmentViewHolder.setAppointmentDay(appointment.getAppointmentDay());
                appointmentViewHolder.setAppointmentDate(appointment.getAppointmentDate());
                appointmentViewHolder.setAppointmentImage();

                appointmentViewHolder.rowLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(PreviousAppointmentsButtonActivity.this, ActivityPreviousAppointmentsDetails.class);

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

                        PreviousAppointmentsButtonActivity.this.startActivity(intent);
                    }
                });
            }
        };

        previousAppointmentsRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class previousAppointmentViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ConstraintLayout rowLayout;

        public previousAppointmentViewHolder(View itemView)
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
}
