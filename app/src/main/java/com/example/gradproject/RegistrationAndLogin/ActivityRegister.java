package com.example.gradproject.RegistrationAndLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.Toast;

import com.example.gradproject.MainActivity;
import com.example.gradproject.R;
import com.example.gradproject.data.Account;
import com.example.gradproject.data.Student;
import com.example.gradproject.data.Tutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRegister extends AppCompatActivity implements View.OnClickListener {

    EditText fullName, email, reEmail, password, repassword, mobile;
    EditText tutorDescription;
    ProgressBar progressBar;
    ActivityUserType userType;
    DatabaseReference accountDatabase;
    DatabaseReference studentsDatabase;
    DatabaseReference tutorsDatabase;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userType = new ActivityUserType();

        fullName = findViewById(R.id.UserFullName);
        email = findViewById(R.id.UserEmail);
        password = findViewById(R.id.UserPassword);
        repassword = findViewById(R.id.UserRePassword);
        mobile = findViewById(R.id.UserMobile);
        reEmail = findViewById(R.id.UserReEmail);

        tutorDescription = findViewById(R.id.TutorDescription);

        findViewById(R.id.RigisterButton).setOnClickListener(this);
        findViewById(R.id.AlreadyRegistered).setOnClickListener(this);


        if(userType.tutorTypeUser == true)
            tutorDescription.setVisibility(View.VISIBLE);
        else
            tutorDescription.setVisibility(View.GONE);


        progressBar = findViewById(R.id.progressBarRegister);

        mAuth = FirebaseAuth.getInstance();
        accountDatabase = FirebaseDatabase.getInstance().getReference("Accounts");
        studentsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Students");
        tutorsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Tutors");
    }

    private void registerUser() {
        String stringEmail = email.getText().toString().trim();
        String stringReEnterEmail = reEmail.getText().toString().trim();
        String stringPassword = password.getText().toString().trim();
        String stringRepassword = repassword.getText().toString().trim();

        if (stringEmail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!stringReEnterEmail.equals(stringEmail)) {
            reEmail.setError("Emails do not match");
            reEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(stringEmail).matches()) {
            email.setError("Please enter a valid email");
            email.requestFocus();
            return;
        }

        if (stringPassword.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if (stringPassword.length() < 6) {
            password.setError("Minimum length of password should be > 6");
            password.requestFocus();
            return;
        }

        if (!stringPassword.equals(stringRepassword)) {
            repassword.setError("Passwords Do not Match");
            return;
        }

        findViewById(R.id.RigisterButton).setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        // Registering the user in firebase ...

        mAuth.createUserWithEmailAndPassword(stringEmail, stringPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ActivityRegister.this, "User Created Successfully.", Toast.LENGTH_SHORT).show();

                    finish();
                    Intent intent = new Intent(ActivityRegister.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // This because if the user press back he will not go to log in again.
                    startActivity(intent);
                    addUserToDatabase();
                    progressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(ActivityRegister.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    findViewById(R.id.RigisterButton).setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void addUserToDatabase()
    {
        String stringfullName = fullName.getText().toString().trim();
        String stringEmail = email.getText().toString().trim();
        String stringMobile = mobile.getText().toString().trim();
        String stringDescription = tutorDescription.getText().toString().trim();

        if (!stringfullName.isEmpty())
        {
            String id = mAuth.getCurrentUser().getUid();

            if(userType.studentTypeUser == true)
            {
                Student student = new Student(id, stringfullName,stringEmail,stringMobile);
                studentsDatabase.child(id).setValue(student);
            }

            if(userType.tutorTypeUser == true)
            {
                Tutor tutor = new Tutor(id, stringfullName,stringEmail,stringMobile, stringDescription,0,0,0,0,0);
                tutorsDatabase.child(id).setValue(tutor);
            }

            //Account account = new Account(id, stringfullName, stringEmail, stringMobile);
            //accountDatabase.child(id).setValue(account);
        } else
        {
            Toast.makeText(this, "Enter full name to be stored.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RigisterButton:
                ActivityUserType.fa.finish();
                registerUser();
                break;

            case R.id.AlreadyRegistered:
                finish();
                startActivity(new Intent(this, ActivityLogin.class));
                break;
        }
    }
}
