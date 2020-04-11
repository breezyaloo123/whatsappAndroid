package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {

    private String friend_id;
    private CircleImageView imageView;
    private TextView username,status;
    private Button sendMessage;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ref = FirebaseDatabase.getInstance().getReference().child("users");
        imageView = findViewById(R.id.friend_profile_image);
        username = findViewById(R.id.friend_profile_username);
        status = findViewById(R.id.friend_profile_status);
        sendMessage = findViewById(R.id.friend_profile_button);
        friend_id = getIntent().getExtras().get("id").toString();

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
}
