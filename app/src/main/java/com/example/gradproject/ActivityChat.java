package com.example.gradproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.gradproject.RecyclerView.MessagesRecyclerViewAdapter;
import com.example.gradproject.data.ChatRoom;
import com.example.gradproject.data.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ActivityChat extends AppCompatActivity
{

    private DatabaseReference chatDatabase;
    private DatabaseReference messagesDatabase;

    private FirebaseAuth mAuth;
    private String tutorId, studentId;
    private String tutorName, studentName;
    private String userid;

    private String reciverid;

    private ImageButton sendButton;
    private EditText messageText;

    private RecyclerView messagesList;
    private final List<Message> messagesRecyclerList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesRecyclerViewAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        tutorId = intent.getStringExtra("tutorId");
        studentId = intent.getStringExtra("studentId");
        tutorName = intent.getStringExtra("tutorName");
        studentName = intent.getStringExtra("studentName");

        sendButton = findViewById(R.id.send_message_btn);
        messageText = findViewById(R.id.input_message);

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        chatDatabase = FirebaseDatabase.getInstance().getReference("ChatRoom");
        messagesDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        adapter = new MessagesRecyclerViewAdapter(messagesRecyclerList);

        messagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);

        messagesList.setHasFixedSize(true);
        messagesList.setLayoutManager(linearLayoutManager);
        messagesList.setAdapter(adapter);

        loadMessages();

        reciverid = "";
        if(MainActivity.tutorUserType)
            reciverid = studentId;

        if(MainActivity.studentUserType)
            reciverid = tutorId;


        chatDatabase.child(userid).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(reciverid))
                {
                    String chatRoomId = chatDatabase.push().getKey();
                    Date date = new Date();

                    ChatRoom chatRoomStudent = new ChatRoom(chatRoomId, tutorName, tutorId,false, date.getTime());
                    ChatRoom chatRoomTutor = new ChatRoom(chatRoomId, studentName, studentId,false, date.getTime());

                    chatDatabase.child(userid).child(reciverid).setValue(chatRoomStudent);
                    chatDatabase.child(reciverid).child(userid).setValue(chatRoomTutor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendMessage();
            }
        });
    }


    private void sendMessage()
    {
        String message = messageText.getText().toString();
        Date date = new Date();

        String messageId = messagesDatabase.push().getKey();

        if(!message.isEmpty())
        {
            Message messageObject = new Message(messageId, message,false,"text", date.getTime(), userid);

            reciverid = "";
            if(MainActivity.tutorUserType)
                reciverid = studentId;

            if(MainActivity.studentUserType)
                reciverid = tutorId;

            messageText.setText("");
            messagesDatabase.child(userid).child(reciverid).child(messageId).setValue(messageObject);
            messagesDatabase.child(reciverid).child(userid).child(messageId).setValue(messageObject);
        }
    }

    private void loadMessages()
    {

        System.out.println("====================================Tutor id Chat: " + tutorId + " =========Student if Chat" + studentId + "===================");
        reciverid = "";

        if(MainActivity.tutorUserType)
             reciverid = studentId;

        if(MainActivity.studentUserType)
            reciverid = tutorId;

        messagesDatabase.child(userid).child(reciverid).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Message message = dataSnapshot.getValue(Message.class);
                System.out.println("====================================INSEDDEEEEEE ============================");
                messagesRecyclerList.add(message);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
