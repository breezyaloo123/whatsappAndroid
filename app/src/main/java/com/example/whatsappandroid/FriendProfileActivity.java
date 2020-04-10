package com.example.whatsappandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class FriendProfileActivity extends AppCompatActivity {

    private String friend_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        friend_id = getIntent().getExtras().get("id").toString();
    }
}
