package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private String userID;
    private MessageHelper helper;
    private String email;
    private String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        userID = getIntent().getExtras().get("userID").toString();
        helper = new MessageHelper(getApplicationContext());
        Toast.makeText(getApplicationContext(),userID,Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail().toString();
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
                    status = dataSnapshot.child("status").getValue().toString();
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

                    DisplayMessages(dataSnapshot);



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    DisplayMessages(dataSnapshot);



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
        //loadMessages();
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

            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Message.USERNAME,currentUserName);
            values.put(Message.STATUS,status);
            values.put(Message.EMAIL,email);
            values.put(Message.PASSWORD,"");
            values.put(Message.MESSAGE,message);
            values.put(Message.DATE,currentDate);
            values.put(Message.TIME,currentTime);
            values.put(Message.GROUPS,currentGroupName);
            //String selection = Message._ID + " LIKE "+userID;
            //db.update(Message.TABLE,values,selection,null);
            db.insertWithOnConflict(Message.TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

        }
    }

    private void loadMessages()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] request = {
                Message._ID,
                Message.USERNAME,
                Message.PASSWORD,
                Message.EMAIL,
                Message.PASSWORD,
                Message.MESSAGE,
                Message.DATE,
                Message.TIME
        };
    String selection = Message.GROUPS + " LIKE "+currentGroupName;
       Cursor cursor = db.query(Message.TABLE,request,selection,null,null,null,null);
       while (cursor.moveToNext())
       {
           String name= cursor.getString(1);
           String message = cursor.getString(5);
           String date = cursor.getString(6);
           String time = cursor.getString(7);
           textView.append(name + " : \n" + message + "\n" +time + "       "+date + "\n\n\n");
           Myview.fullScroll(ScrollView.FOCUS_DOWN);
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
