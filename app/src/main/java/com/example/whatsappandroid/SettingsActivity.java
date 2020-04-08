package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button button;
    private EditText username,photo_status;
    private CircleImageView image;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    String currentUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        currentUserid = mAuth.getCurrentUser().getUid();
        button = findViewById(R.id.settings_button);
        username = findViewById(R.id.username);
        photo_status = findViewById(R.id.photo_status);
        image = findViewById(R.id.profile_image);

        username.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsUpdated();
            }
        });
        RetrieveUsers();
    }



    private void SettingsUpdated() {

        String userName = username.getText().toString();
        String photoStatus = photo_status.getText().toString();
        if(TextUtils.isEmpty(userName))
        {
            Toast.makeText(getApplicationContext(),"Please Enter Your Username",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(photoStatus))
        {
            Toast.makeText(getApplicationContext(),"Please Enter Your Status",Toast.LENGTH_LONG).show();
        }
        else
        {
            HashMap<String, String> map= new HashMap<>();
            map.put("uid",currentUserid);
            map.put("name",userName);
            map.put("status",photoStatus);
            ref.child("users").child(currentUserid).setValue(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                HomePage();
                                Toast.makeText(getApplicationContext(),"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                String error = task.getException().toString();
                                Toast.makeText(getApplicationContext(),"Error: "+error,Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }

    }
    private void RetrieveUsers() {

        ref.child("users").child(currentUserid).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {
                            String retrieveUsername = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrievePhoto = dataSnapshot.child("image").getValue().toString();

                            username.setText(retrieveUsername);
                            photo_status.setText(retrieveStatus);

                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUsername = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            username.setText(retrieveUsername);
                            photo_status.setText(retrieveStatus);
                        }
                        else
                        {
                            username.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Please set Your Settings infos",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void HomePage() {

        Intent home = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(home);
        finish();
    }
}
