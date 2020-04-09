package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton button;
    private EditText editText;
    private ScrollView Myview;
    private TextView textView;
    private String currentGroupName;
    private FirebaseAuth mAuth;
    private String currentUserID,currentUserName,currentDate,currentTime;
    private DatabaseReference ref,groupRef,getGroupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(getApplicationContext(),currentGroupName,Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("users");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        currentUserID = mAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.group_chat_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);
        button = findViewById(R.id.send_button);
        editText = findViewById(R.id.edit_group_chat);
        Myview = findViewById(R.id.scroll_view);
        textView = findViewById(R.id.text_group_chat);

        getUserInformation();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMessage();
                editText.setText("");
                Myview.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });

    }


    private void getUserInformation()
    {
        ref.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }

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



    private void SaveMessage()
    {
        String message = editText.getText().toString();

        String messageKey = groupRef.push().getKey();
        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(getApplicationContext(),"Please write message",Toast.LENGTH_LONG).show();
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = format.format(calendar.getTime());

            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
            currentTime = format1.format(calendar1.getTime());

            HashMap<String, Object> groupmessages = new HashMap<>();

            groupRef.updateChildren(groupmessages);

            getGroupMessageKeyRef = groupRef.child(messageKey);
            HashMap<String, Object> messageInfo = new HashMap<>();
            messageInfo.put("name",currentUserName);
            messageInfo.put("message",message);
            messageInfo.put("Date",currentDate);
            messageInfo.put("Time",currentTime);

            getGroupMessageKeyRef.updateChildren(messageInfo);

        }
    }

    private void DisplayMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext())
        {
            String date = ((DataSnapshot)iterator.next()).getValue().toString();
            String time = ((DataSnapshot)iterator.next()).getValue().toString();
            String message = ((DataSnapshot)iterator.next()).getValue().toString();
            String name = ((DataSnapshot)iterator.next()).getValue().toString();

            textView.append(name + " : \n" + message + "\n" +time + "       "+date + "\n\n\n");

            Myview.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
