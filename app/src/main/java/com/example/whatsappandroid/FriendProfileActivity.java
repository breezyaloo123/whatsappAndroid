package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {

    private String friend_id,state,currentUserID;
    private CircleImageView imageView;
    private TextView username,status;
    private Button sendMessage,cancelMessage;
    private DatabaseReference ref,chatRequestRef,contactRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        imageView = findViewById(R.id.friend_profile_image);
        username = findViewById(R.id.friend_profile_username);
        status = findViewById(R.id.friend_profile_status);
        sendMessage = findViewById(R.id.friend_profile_button);
        cancelMessage = findViewById(R.id.friend_profile_button_cancel);
        friend_id = getIntent().getExtras().get("id").toString();
        Toast.makeText(getApplicationContext(),"lll"+friend_id,Toast.LENGTH_LONG).show();
        state = "new";

        userInfo();
    }

    private void userInfo()
    {
        ref.child(friend_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                {
                    String image = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    username.setText(userName);
                    status.setText(userStatus);
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(imageView);
                    requestManagement();
                }
                else
                {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    username.setText(userName);
                    status.setText(userStatus);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void chatRequest() {

        chatRequestRef.child(currentUserID).child(friend_id).child("request_type").
                setValue("sent").
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            chatRequestRef.child(friend_id).child(currentUserID).child("request_type").
                                    setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        sendMessage.setEnabled(true);
                                        state = "request_sent";
                                        sendMessage.setText("Cancel Chat");
                                        Toast.makeText(getApplicationContext(),"Gooodd",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                    }
                });
    }

    private void requestManagement() {

        chatRequestRef.child(currentUserID).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(friend_id))
                        {
                            String request_type = dataSnapshot.child(friend_id).child("request_type").getValue().toString();
                            if (request_type.equals("sent"))
                            {
                                state = "request_sent";
                                sendMessage.setText("Cancel Chat");
                            }
                            else if (request_type.equals("received"))
                            {
                                state = "request_received";
                                sendMessage.setText("Accept Chat");
                                cancelMessage.setVisibility(View.VISIBLE);
                                cancelMessage.setEnabled(true);
                                cancelMessage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CancelChat();
                                    }
                                });
                            }

                        }
                        else {
                            contactRef.child(currentUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(friend_id))
                                            {
                                                state = "friends";
                                                sendMessage.setText("Remove this contact");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        if(!currentUserID.equals(friend_id))
        {
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage.setEnabled(false);
                    if(state.equals("new"))
                    {
                        chatRequest();
                        Toast.makeText(getApplicationContext(),"Gooddd",Toast.LENGTH_LONG).show();
                    }
                    if (state.equals("request_sent"))
                    {
                        CancelChat();
                    }
                    if (state.equals("request_received"))
                    {
                        AcceptChat();
                    }
                    if (state.equals("friends"))
                    {
                        RemoveContacts();
                    }
                }
            });
        }
        else
        {
            sendMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveContacts() {


        contactRef.child(currentUserID).child(friend_id).
                removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    contactRef.child(friend_id).child(currentUserID).removeValue().
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        sendMessage.setEnabled(true);
                                        state="new";
                                        sendMessage.setText("send Chat");

                                        cancelMessage.setVisibility(View.VISIBLE);
                                        cancelMessage.setEnabled(false);
                                    }

                                }
                            });
                }

            }
        });

    }

    private void AcceptChat()
    {
        contactRef.child(currentUserID).child(friend_id).child("contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            contactRef.child(friend_id).child(currentUserID).child("contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                chatRequestRef.child(currentUserID).child(friend_id)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    chatRequestRef.child(friend_id).child(currentUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    sendMessage.setEnabled(true);
                                                                                    state = "friends";
                                                                                    sendMessage.setText("Remove this Contact");
                                                                                    cancelMessage.setVisibility(View.INVISIBLE);
                                                                                    cancelMessage.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }

                    }
                });

    }

    private void CancelChat() {

        chatRequestRef.child(currentUserID).child(friend_id).
                removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    chatRequestRef.child(friend_id).child(currentUserID).removeValue().
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        sendMessage.setEnabled(true);
                                        state="new";
                                        sendMessage.setText("send Chat");

                                        cancelMessage.setVisibility(View.VISIBLE);
                                         cancelMessage.setEnabled(false);
                                    }

                                }
                            });
                }

            }
        });
    }


}
