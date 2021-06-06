package com.example.gradproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gradproject.ProfileMenuActivites.PreviousAppointmentsButtonActivity;
import com.example.gradproject.ProfileMenuActivites.ProfileButtonActivity;
import com.example.gradproject.ProfileMenuActivites.AddCoursesActivity;
import com.example.gradproject.R;
import com.example.gradproject.RegistrationAndLogin.ActivityLogin;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile,null);

        Button prfbtn = (Button) view.findViewById(R.id.profile_button);
        prfbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), ProfileButtonActivity.class);
                startActivity(intent);
            }
        });

        Button stgbtn = (Button) view.findViewById(R.id.add_courses_button);
        stgbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), AddCoursesActivity.class);
                startActivity(intent);
            }
        });

        Button ptvbtn = (Button) view.findViewById(R.id.previousappointments_button);
        ptvbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), PreviousAppointmentsButtonActivity.class);
                startActivity(intent);
            }
        });

        Button logoutbtn = (Button) view.findViewById(R.id.logout_button);

        logoutbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                Intent intent = new Intent(getContext(), ActivityLogin.class);
                startActivity(intent);
            }
        });

        return view;
    }
}

