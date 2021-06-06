package com.example.gradproject.Notifications;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gradproject.ActivityTutorRating;
import com.example.gradproject.MainActivity;
import com.example.gradproject.R;
import com.example.gradproject.data.Student;
import com.example.gradproject.data.Tutor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class TutorRatingNotification extends BroadcastReceiver
{
    private FirebaseAuth mAuth;
    DatabaseReference studentsDatabase;
    DatabaseReference tutorsDatabase;

    String userId;
    String userName;

    Intent tutorRatingIntent;

    public void onReceive(final Context context, Intent intent)
    {
        mAuth = FirebaseAuth.getInstance();

        studentsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Students");
        tutorsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Tutors");

        userId = mAuth.getCurrentUser().getUid();

        getTheUserName();

        tutorRatingIntent = new Intent(context, ActivityTutorRating.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ActivityTutorRating.class); // AppointmentFragment / AppointmentDetailsActivity / MainActivity
        stackBuilder.addNextIntent(tutorRatingIntent);

        final PendingIntent pendingAppointmentIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                final String notificationMessage;
                if(userName != null)
                    notificationMessage = "Hi " + userName +", " + " How was your last session ?"; // This if the app in background.
                else
                    notificationMessage = "Hi, " + " How was your last session ?"; // This if the user close the app.

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "FahemApp")
                        .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                        .setContentTitle("Rate Your Tutor.")
                        .setContentText(notificationMessage)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingAppointmentIntent)
                        .setAutoCancel(true);

                Random random = new Random();
                int randomNumber = random.nextInt(101);//This is in order to receive multiple notifications;

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(randomNumber,builder.build());

                System.out.println("============================username:" + userName + "============================");
            }
        }, 3000);
    }

    public void getTheUserName()
    {
        if(MainActivity.studentUserType)
        {
            Query query = studentsDatabase.orderByChild("accountId").equalTo(userId).limitToFirst(1);
            query.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        Student s = dataSnapshot.getChildren().iterator().next().getValue(Student.class);
                        if(s != null)
                            userName = s.getFullName();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }
}
