package com.example.gradproject.RegistrationAndLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.gradproject.R;
import com.example.gradproject.RegistrationAndLogin.ActivityRegister;

public class ActivityUserType extends AppCompatActivity implements View.OnClickListener
{

    public static boolean studentTypeUser = false;
    public static boolean tutorTypeUser = false;
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        findViewById(R.id.student_type_account).setOnClickListener(this);
        findViewById(R.id.tutor_type_account).setOnClickListener(this);

        fa = this;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.student_type_account:
                studentTypeUser = true;
                tutorTypeUser = false;
                startActivity(new Intent(this, ActivityRegister.class));
                break;

            case R.id.tutor_type_account:
                tutorTypeUser = true;
                studentTypeUser = false;
                startActivity(new Intent(this, ActivityRegister.class));
                break;
        }
    }
}
