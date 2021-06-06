package com.example.gradproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gradproject.SessionsActivities.ActivityCreateSession;
import com.example.gradproject.SessionsActivities.ActivityDisplayAvailableSessions;
import com.example.gradproject.data.Course;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TutorDisplayedProfile extends AppCompatActivity
{

    String stringTutorId, stringTutorName;
    TextView tutorName;
    TextView tutorDescription;
    ImageView tutorProfilePic;
    Button displaySchedule;

    DatabaseReference usersCourses;
    RecyclerView tutorDisplayedCoursesRecyclerView;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_displayed_profile);

        tutorName = findViewById(R.id.tutor_displayed_name);
        tutorDescription = findViewById(R.id.tutor_displayed_description);
        tutorProfilePic = findViewById(R.id.tutor_displayed_profile_pic);
        displaySchedule = findViewById(R.id.display_tutor_schedule);

        tutorDisplayedCoursesRecyclerView = findViewById(R.id.tutor_displayed_courses);
        tutorDisplayedCoursesRecyclerView.setHasFixedSize(true);
        tutorDisplayedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();

        stringTutorId = intent.getStringExtra("tutorId");
        stringTutorName = intent.getStringExtra("tutorName");
        tutorName.setText(stringTutorName);
        tutorDescription.setText("Tutor Description : -\n\n" + intent.getStringExtra("tutorDescription" ) + "\n\n");
        tutorDescription.setMovementMethod(new ScrollingMovementMethod());

        usersCourses = FirebaseDatabase.getInstance().getReference("AssignedCourses/" + stringTutorId);

        displaySchedule.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(TutorDisplayedProfile.this, ActivityDisplayAvailableSessions.class);
                intent.putExtra("tutorId", stringTutorId);
                intent.putExtra("tutorName", stringTutorName); //This is for passing on the tutor id from the previous Student Home Fragment to ActivityDisplayAvailableSessions.
                startActivity(intent);
            }
        });
    }

    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Course, TutorDisplayedProfile.CourseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, TutorDisplayedProfile.CourseViewHolder>(Course.class, R.layout.course_card, TutorDisplayedProfile.CourseViewHolder.class, usersCourses)
        {
            @Override
            protected void populateViewHolder(TutorDisplayedProfile.CourseViewHolder courseViewHolder, final Course course, int i)
            {
                courseViewHolder.setName(course.getCourseName());
            }
        };

        tutorDisplayedCoursesRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ConstraintLayout rowLayout;

        public CourseViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            rowLayout = itemView.findViewById(R.id.recyclerview_courses);
        }

        public void setName(String name)
        {
            TextView nameTv = mView.findViewById(R.id.course_name);
            nameTv.setText(name);
        }
    }
}
