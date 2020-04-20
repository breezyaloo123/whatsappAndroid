package com.example.whatsappandroid;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth auth;
    private DatabaseReference ref;

    public MessageAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessagesText,receiverMessagesText;
        public CircleImageView receiverImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessagesText = itemView.findViewById(R.id.sender_message_text);
            receiverMessagesText = itemView.findViewById(R.id.receiver_message_text);
            receiverImage = itemView.findViewById(R.id.message_profile_image);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout,parent,false);

        auth= FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
    {
        String messageSenderID = auth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);
        String fromUserID = messages.getFrom();
        String type = messages.getType();
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(fromUserID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("image"))
                {
                    String receiverprofileimage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverprofileimage).placeholder(R.drawable.profile).into(holder.receiverImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (type.equals("text"))
        {
            holder.receiverMessagesText.setVisibility(View.INVISIBLE);
            holder.receiverImage.setVisibility(View.INVISIBLE);
            holder.senderMessagesText.setVisibility(View.INVISIBLE);
            if (fromUserID.equals(messageSenderID))
            {
                holder.senderMessagesText.setVisibility(View.VISIBLE);
                holder.senderMessagesText.setBackgroundResource(R.drawable.sender_message);
                holder.senderMessagesText.setText(messages.getMessage());
            }
            else
            {

                holder.receiverImage.setVisibility(View.VISIBLE);
                holder.receiverMessagesText.setVisibility(View.VISIBLE);

                holder.receiverMessagesText.setBackgroundResource(R.drawable.receiver_message);
                holder.receiverMessagesText.setTextColor(Color.BLACK);
                holder.receiverMessagesText.setText(messages.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
