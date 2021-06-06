package com.example.gradproject.ProfileMenuActivites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradproject.MainActivity;
import com.example.gradproject.R;
import com.example.gradproject.data.Course;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddCoursesActivity extends AppCompatActivity
{
    TextView chooseCoursesNote;
    Spinner courseChosen;
    Button addCourseButton;
    DatabaseReference coursesDatabase;
    DatabaseReference userCourseDatabase;

    DatabaseReference usersCourses;

    private FirebaseAuth mAuth;

    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    RecyclerView CoursesRecyclerView;

    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_add_courses);

        courseChosen = findViewById(R.id.courses_spinner);
        addCourseButton = findViewById(R.id.add_course_button);
        chooseCoursesNote = findViewById(R.id.choose_courses);

        mAuth = FirebaseAuth.getInstance();

        coursesDatabase = FirebaseDatabase.getInstance().getReference("Courses");

        userCourseDatabase = FirebaseDatabase.getInstance().getReference("AssignedCourses").child(mAuth.getUid());

        usersCourses = FirebaseDatabase.getInstance().getReference("AssignedCourses/" + mAuth.getUid());
        usersCourses.keepSynced(true);

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerDataList);

        CoursesRecyclerView = findViewById(R.id.courses_list);
        CoursesRecyclerView.setHasFixedSize(true);
        CoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseChosen.setAdapter(adapter);
        populateSpinnerWithCourses();

        addCourseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assignCourse();
            }
        });

        if(MainActivity.tutorUserType)
        {
            chooseCoursesNote.setText("Choose the courses you will be teaching: -");
        }

        if(MainActivity.studentUserType)
        {
            chooseCoursesNote.setText("Choose the courses you want learn: -");
        }
    }

    public void assignCourse()
    {
        coursesDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    String courseName = courseChosen.getSelectedItem().toString().trim();
                    String spinnerCourse = item.getValue().toString().substring(12,17);

                    Course course;

                    if(courseName.equals(spinnerCourse))
                    {
                        course = item.getValue(Course.class);
                        userCourseDatabase.child(course.getCourseId()).setValue(course);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        Toast.makeText(this,"Course Added", Toast.LENGTH_LONG).show();
    }

    public void populateSpinnerWithCourses()
    {
        listener = coursesDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    Course course = item.getValue(Course.class); //This did not exist before. There was a problem Faden report.the problem was spinner filled with courses id insteed of courses names. This problem appear only when used in the phone device.
                    spinnerDataList.add(course.getCourseName()); //spinnerDataList.add(item.getValue().toString().substring(12,17)); << this was before Faden problem.

                    System.out.println("item.getValue().toString().substring(12,17)>>>>>" + item.getValue().toString().substring(12,17) + "=========================item.getValue().toString()" + item.getValue().toString());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Course, CourseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(Course.class, R.layout.course_card,CourseViewHolder.class, usersCourses)
        {
            @Override
            protected void populateViewHolder(CourseViewHolder courseViewHolder, final Course course, int i)
            {
                courseViewHolder.setName(course.getCourseName());


                courseViewHolder.rowLayout.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        showDeleteDialog(course.getCourseName(),course.getCourseId());
                        return false;
                    }
                });
            }
        };

        CoursesRecyclerView.setAdapter(firebaseRecyclerAdapter);
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

    private void showDeleteDialog(String courseName, final String courseId)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_course_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button yesButton = dialogView.findViewById(R.id.yes_button);
        final Button noButton = dialogView.findViewById(R.id.no_button);

        dialogBuilder.setTitle(courseName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        yesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteCourse(mAuth.getCurrentUser().getUid(),courseId);
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

    public void deleteCourse(String userid, String courseid)
    {
        DatabaseReference usersCourses = FirebaseDatabase.getInstance().getReference("AssignedCourses/").child(userid).child(courseid);
        usersCourses.removeValue();

        Toast.makeText(this, "Course Deleted",Toast.LENGTH_LONG).show();
    }
}
