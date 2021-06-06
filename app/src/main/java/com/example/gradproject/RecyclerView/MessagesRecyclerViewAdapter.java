package com.example.gradproject.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradproject.R;
import com.example.gradproject.data.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.MessageViewHolder>
{

    private List<Message> messagesList;
    private FirebaseAuth mAuth;

    public MessagesRecyclerViewAdapter(List<Message> msgList)
    {
        this.messagesList = msgList;
    }

    @NonNull
    @Override
    public MessagesRecyclerViewAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout, parent, false);
        return new MessagesRecyclerViewAdapter.MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesRecyclerViewAdapter.MessageViewHolder holder, int position)
    {
        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        Message message = messagesList.get(position);

        String from = message.getFrom();

        // Here you customize the chat layout.
        if(from.equals(currentUserId))
        {
            holder.messageTextSender.setText(message.getMessage());
            holder.messageTextReceiver.setText(message.getMessage());

            holder.messageTextReceiver.setVisibility(View.GONE);
            holder.profileImage.setVisibility(View.GONE);
        }else
        {
            holder.messageTextSender.setText(message.getMessage());
            holder.messageTextReceiver.setText(message.getMessage());

            holder.messageTextSender.setVisibility(View.GONE);
        }

        holder.messageTextSender.setText(message.getMessage());
        holder.messageTextReceiver.setText(message.getMessage());

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageTextSender;
        public TextView messageTextReceiver;
        public ImageView profileImage;

        public MessageViewHolder(View view)
        {
            super(view);

            messageTextSender = view.findViewById(R.id.sender_messsage_text);
            messageTextReceiver = view.findViewById(R.id.receiver_message_text);
            profileImage = view.findViewById(R.id.message_profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
