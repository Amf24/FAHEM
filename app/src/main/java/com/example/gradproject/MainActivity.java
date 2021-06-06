package com.example.gradproject;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.gradproject.Fragments.AppointmentFragment;
import com.example.gradproject.Fragments.ChatFragment;
import com.example.gradproject.Fragments.HomeFragmentStudent;
import com.example.gradproject.Fragments.HomeFragmentTutor;
import com.example.gradproject.Fragments.ProfileFragment;
import com.example.gradproject.data.Appointment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{


    //DatabaseReference studentsDatabase;
    DatabaseReference tutorsDatabase;
    private FirebaseAuth mAuth;
    ProgressBar loadingHomeFragment;
    Toolbar toolbar;

    public static boolean tutorUserType = false;
    public static boolean studentUserType = false;

    public static boolean appointmentNotificationReminder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        createNotificationChannel();

        mAuth = FirebaseAuth.getInstance();
        //studentsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Students");
        tutorsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Tutors");
        loadingHomeFragment = findViewById(R.id.fragmet_loading);

        toolbar = findViewById(R.id.tutor_search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        userType();

        loadingHomeFragment.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(appointmentNotificationReminder)
                {
                    //loadFragment(new AppointmentFragment());
                    View myView = findViewById(R.id.navigation_appointments);
                    myView.performClick();
                    appointmentNotificationReminder = false;
                }
                else
                {
                    if (studentUserType)
                    {
                        getSupportActionBar().show();
                        loadFragment(new HomeFragmentStudent());
                    }

                    if (tutorUserType)
                        loadFragment(new HomeFragmentTutor());
                }
                loadingHomeFragment.setVisibility(View.GONE);
            }
        }, 5000);
    }

    private boolean loadFragment(Fragment fragment)
    {

        if(fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        Fragment fragment = null;

        switch(menuItem.getItemId())
        {
            case R.id.navigation_home:
                if(studentUserType)
                {
                    fragment = new HomeFragmentStudent();
                    getSupportActionBar().show();
                }
                if(tutorUserType)
                    fragment = new HomeFragmentTutor();
                break;

            case R.id.navigation_appointments:
                fragment = new AppointmentFragment();
                getSupportActionBar().hide();
                break;

            case R.id.navigation_chat:
                fragment = new ChatFragment();
                getSupportActionBar().hide();
                break;

                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    getSupportActionBar().hide();


                break;
        }
        return loadFragment(fragment); // loadRecyclerViewFragment
    }

    public void userType()
    {
        Query query = tutorsDatabase.orderByChild("accountId").equalTo(mAuth.getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() != null)
                {
                    tutorUserType = true;
                    studentUserType = false;
                    //Toast.makeText(getApplicationContext(), "tutorUserType: " + tutorUserType, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    studentUserType = true;
                    tutorUserType = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "FahemReminderChannel";
            String description = "Channel for Fahem App Reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("FahemApp", name, importance);

            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search_for_tutor_menu, menu);
        MenuItem item = menu.findItem(R.id.tutor_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search by Tutor Name or Rating...  ");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                HomeFragmentStudent.adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
