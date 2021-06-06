package com.example.gradproject.RegistrationAndLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gradproject.MainActivity;
import com.example.gradproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener
{

    FirebaseAuth mAuth;
    EditText email, password;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.UserEmailLogin);
        password = findViewById(R.id.UserPasswordLogin);
        progressBar = findViewById(R.id.progressBarLogin);

        findViewById(R.id.RigisterHere).setOnClickListener(this);
        findViewById(R.id.LoginButton).setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
    }

    private void userLogin()
    {
        String stringEmail = email.getText().toString().trim();
        String stringPassword = password.getText().toString().trim();

        if (stringEmail.isEmpty())
        {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(stringEmail).matches()) {
            email.setError("Please enter a valid email");
            email.requestFocus();
            return;
        }

        if (stringPassword.isEmpty())
        {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if (stringPassword.length() < 6)
        {
            password.setError("Minimum length of password should be > 6");
            password.requestFocus();
            return;
        }

        findViewById(R.id.LoginButton).setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(stringEmail, stringPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    finish();
                    Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // This because if the user press back he will not go to log in again.
                    startActivity(intent);
                } else
                    {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    findViewById(R.id.LoginButton).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(mAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.RigisterHere:
                finish();
                startActivity(new Intent(this, ActivityUserType.class));
                break;

            case R.id.LoginButton:
                userLogin();
                break;
        }
    }
}
