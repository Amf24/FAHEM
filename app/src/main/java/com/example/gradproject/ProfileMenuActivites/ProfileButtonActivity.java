package com.example.gradproject.ProfileMenuActivites;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.WidgetContainer;

import com.example.gradproject.MainActivity;
import com.example.gradproject.R;
import com.example.gradproject.data.Student;
import com.example.gradproject.data.Tutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.internal.zzn;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileButtonActivity extends AppCompatActivity
{

    EditText fullName, mobile, email, password, repassword;
    EditText tutorDescription;

    int tutorNumberOfRatings;
    float tutorExp, tutorRes, tutorAtt;
    float generalTutorRating;

    Button saveButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    DatabaseReference studentsDatabase;
    DatabaseReference tutorsDatabase;
    public static boolean student = false;
    public static boolean tutor = false;

    String userid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_details);

        fullName = findViewById(R.id.UserProfileFullName);
        email = findViewById(R.id.UserProfileEmail);

        password = findViewById(R.id.UserProfilePassword);
        repassword = findViewById(R.id.UserProfileRePassword);

        mobile = findViewById(R.id.UserProfileMobile);
        tutorDescription = findViewById(R.id.TutorProfileDescription);
        saveButton = findViewById(R.id.saveButton);

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userid = mAuth.getCurrentUser().getUid();

        studentsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Students");
        tutorsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Tutors");

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(MainActivity.studentUserType)
                    UpdateUserInfo(fullName.getText().toString(), email.getText().toString(), mobile.getText().toString(), "");
                else
                    UpdateUserInfo(fullName.getText().toString(), email.getText().toString(), mobile.getText().toString(), tutorDescription.getText().toString());
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(MainActivity.studentUserType)
        {
            studentsDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Student s = snapshot.getValue(Student.class);
                        if (s.getAccountId().equals(userid)) {
                            tutorDescription.setVisibility(View.GONE);
                            fullName.setText(s.getFullName());
                            email.setText(s.getEmail());
                            mobile.setText(s.getPhoneNumber());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        if(MainActivity.tutorUserType)
        {
            tutorsDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Tutor t = snapshot.getValue(Tutor.class);
                        if (t.getAccountId().equals(userid)) {
                            tutorDescription.setVisibility(View.VISIBLE);
                            fullName.setText(t.getFullName());
                            email.setText(t.getEmail());
                            mobile.setText(t.getPhoneNumber());
                            tutorDescription.setText(t.getTutorDescription());

                            tutorNumberOfRatings = t.getNumberOfRatings();
                            tutorExp = t.getTutorExplanation();
                            tutorRes = t.getTutorRespectingTime();
                            tutorAtt = t.getTutorAttitude();
                            generalTutorRating = t.getGeneralTutorRating();

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

    private void UpdateUserInfo(final String name, final String email, final String mobile, final String desctiption)
    {
        if(MainActivity.studentUserType)
        {
            mUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Student student = new Student(userid,name,email,mobile);
                        studentsDatabase.child(userid).setValue(student);

                        if(!password.getText().toString().equals(""))
                        {
                            if (UpdateUserPassword(password.getText().toString()))
                            {
                                finish();
                            }
                        }else
                        {
                            Toast.makeText(ProfileButtonActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else
                    {
                        Toast.makeText(ProfileButtonActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            //Toast.makeText(ProfileButtonActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
        }

        if(MainActivity.tutorUserType)
        {
            mUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Tutor tutor = new Tutor(userid, name, email, mobile, desctiption, tutorNumberOfRatings, tutorExp, tutorRes, tutorAtt, generalTutorRating);
                        tutorsDatabase.child(userid).setValue(tutor);

                        if(!password.getText().toString().equals(""))
                        {
                            if (UpdateUserPassword(password.getText().toString()))
                            {
                                finish();
                            }
                        }else
                        {
                            Toast.makeText(ProfileButtonActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else
                    {
                        Toast.makeText(ProfileButtonActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }

    private boolean UpdateUserPassword(String newPassword)
    {
        if (newPassword.length() < 6)
        {
            password.setError("Minimum length of password should be > 6");
            password.requestFocus();
            return false;
        }
        else if(password.getText().toString().equals(repassword.getText().toString()))
        {
            mUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ProfileButtonActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ProfileButtonActivity.this, "Password did not Update!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }else
        {
            repassword.requestFocus();
            repassword.setError("Passwords Do not Match");
            return false;
        }

    }
}
