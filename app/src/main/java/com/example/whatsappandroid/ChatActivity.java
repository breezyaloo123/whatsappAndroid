package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
private String receiverID,receiverName,receiverImage,senderID;

private TextView name,lastseen;
private CircleImageView imageView;
private Toolbar toolbar;
private ImageButton sendMessage;
private FirebaseAuth auth;
private DatabaseReference mRootRef;
private EditText editText;
private final List<Messages> messageList = new ArrayList<>();
private LinearLayoutManager layoutManager;
private MessageAdapter adapter;
private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        auth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        senderID = auth.getCurrentUser().getUid();
        receiverID  = getIntent().getExtras().get("id").toString();
        receiverName = getIntent().getExtras().get("username").toString();
        receiverImage = getIntent().getExtras().get("image").toString();
        toolbar = findViewById(R.id.private_chat_bar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView= inflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);
        name = findViewById(R.id.profile_name_chat);
        lastseen = findViewById(R.id.profile_lastseen_chat);
        imageView = findViewById(R.id.profile_image_chat);
        sendMessage = findViewById(R.id.send_private_chat_button);
        editText = findViewById(R.id.edit_private_chat);

        adapter = new MessageAdapter(messageList);
        recyclerView = findViewById(R.id.private_messages_list_users);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        name.setText(receiverName);
        Picasso.get().load(receiverImage).placeholder(R.drawable.profile).into(imageView);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mRootRef.child("Messages").child(senderID).child(receiverID).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messageList.add(messages);
                        adapter.notifyDataSetChanged();

                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
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

    private void sendMessage() {
        String message = editText.getText().toString();
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(getApplicationContext(),"Write the message",Toast.LENGTH_LONG).show();
        }
        else {
            String messageSenderRef = "Messages/" + senderID + "/" + receiverID;
            String messageReceiverRef = "Messages/" + receiverID + "/" + senderID;

            DatabaseReference userRef = mRootRef.child("Messages").
                    child(senderID).child(receiverID)
                    .push();

            String messagePushID = userRef.getKey();
            Map messageBody = new HashMap();
            messageBody.put("message",message);
            messageBody.put("type","text");
            messageBody.put("from",senderID);

            Map messageDetails = new HashMap();

            messageDetails.put(messageSenderRef + "/ "+ messagePushID,messageBody);
            messageDetails.put(messageReceiverRef + "/ "+ messagePushID,messageBody);

            mRootRef.updateChildren(messageDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Message sent",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                    }

                    editText.setText("");

                }
            });


        }
    }
}
