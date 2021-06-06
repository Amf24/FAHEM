package com.example.gradproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradproject.ActivityChat;
import com.example.gradproject.MainActivity;
import com.example.gradproject.R;
import com.example.gradproject.data.ChatRoom;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ChatFragment extends Fragment
{

    private DatabaseReference chatDatabase;
    private DatabaseReference messagesDatabase;
    RecyclerView chatRoomRecyclerView;

    TextView chatRoomNote;

    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_chat,null);

        mAuth = FirebaseAuth.getInstance();

        chatDatabase = FirebaseDatabase.getInstance().getReference("ChatRoom");
        messagesDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        chatRoomRecyclerView =  rootView.findViewById(R.id.chat_recyclerview);
        chatRoomRecyclerView.setHasFixedSize(true);
        chatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatRoomNote = rootView.findViewById(R.id.chat_room_note);

        Query query = FirebaseDatabase.getInstance().getReference("ChatRoom").child(mAuth.getCurrentUser().getUid()).orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    chatRoomNote.setVisibility(View.GONE);
                    System.out.println("INSEDE ==========================================" + dataSnapshot.getChildren().iterator().next().getValue(ChatRoom.class).getChatRoomRecevierName() );
                }else
                {
                    chatRoomNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return rootView;
    }

    public void onStart()
    {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference("ChatRoom").child(mAuth.getCurrentUser().getUid()).orderByChild("timestamp");
        FirebaseRecyclerAdapter<ChatRoom, ChatFragment.ChatRoomViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatRoom, ChatRoomViewHolder>(ChatRoom.class, R.layout.chat_room_card, ChatFragment.ChatRoomViewHolder.class, query)
        {
            @Override
            protected void populateViewHolder(ChatRoomViewHolder chatRoomViewHolder, final ChatRoom chatRoom, int i)
            {
                chatRoomViewHolder.setUserName(chatRoom.getChatRoomRecevierName());
                chatRoomViewHolder.setChatImage();

                chatRoomViewHolder.rowLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getContext(), ActivityChat.class);

                        if(MainActivity.studentUserType)
                        {
                            intent.putExtra("tutorId", chatRoom.getRecevierId());
                            intent.putExtra("studentId", mAuth.getCurrentUser().getUid());
                        }

                        if(MainActivity.tutorUserType)
                        {
                            intent.putExtra("studentId", chatRoom.getRecevierId());
                            intent.putExtra("tutorId", mAuth.getCurrentUser().getUid());
                        }

                        getContext().startActivity(intent);
                    }
                });
            }
        };

        chatRoomRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ConstraintLayout rowLayout;

        public ChatRoomViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
            rowLayout = itemView.findViewById(R.id.recyclerview_row_chat_room);
        }

        public void setUserName(String name)
        {
            TextView nameTv = mView.findViewById(R.id.tutor_student_name_chat_room);
            nameTv.setText(name);
        }

        public void setChatImage()
        {
            ImageView imageView = mView.findViewById(R.id.chat_image);
            imageView.setImageResource(R.drawable.ic_chat_black_24dp);
        }
    }
}
