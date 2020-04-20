package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button button;
    private EditText username,photo_status;
    private CircleImageView image;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private StorageReference storageReference;
    private String currentUserid,downloadUrl;
    private MessageHelper helper;
    private String email,password;
    private static final int galleriID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        email=getIntent().getExtras().get("email").toString();
  //      password=getIntent().getExtras().get("password").toString();

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Image");
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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,galleriID);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==galleriID && resultCode==RESULT_OK && data!=null)
        {
            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                Uri resultUri = result.getUri();
                final StorageReference storeRef = storageReference.child(currentUserid + ".jpg");

                final UploadTask uploadTask = storeRef.putFile(resultUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String messageError = e.toString();
                        Toast.makeText(getApplicationContext(),"Error: "+messageError,Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"image saved successfully",Toast.LENGTH_LONG).show();

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful())
                                {
                                    throw task.getException();
                                }

                                downloadUrl = storeRef.getDownloadUrl().toString();
                                return storeRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful())
                                {
                                    downloadUrl = task.getResult().toString();
                                    Toast.makeText(getApplicationContext(),"Image saved SUCCEFULLY",Toast.LENGTH_LONG).show();
                                    ref.child("users").child(currentUserid).child("image").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(getApplicationContext(),"image saved successfully",Toast.LENGTH_LONG).show();
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
                        });

                    }
                });
//                storeRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful())
//                        {
//                            Toast.makeText(getApplicationContext(),"Profile Image Updloaded successfully",Toast.LENGTH_LONG).show();
//                            //final String downloadUrl = task.s
//
//                            ref.child("users").child(currentUserid).child("image").setValue(downloadUrl)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful())
//                                    {
//                                        Toast.makeText(getApplicationContext(),"image saved successfully",Toast.LENGTH_LONG).show();
//                                    }
//                                    else
//                                    {
//                                        String error = task.getException().toString();
//                                        Toast.makeText(getApplicationContext(),"Error: "+error,Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            });
//                        }
//                        else
//                        {
//                            String error = task.getException().toString();
//                            Toast.makeText(getApplicationContext(),"Error : "+error,Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//                });
            }
        }
    }
    private void insertData(String userName, String photoStatus) {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Message.USERNAME,userName);
        values.put(Message.STATUS,photoStatus);
        values.put(Message.EMAIL,email);
        values.put(Message.PASSWORD,password);
        values.put(Message.MESSAGE,"");
        values.put(Message.DATE,"");
        values.put(Message.TIME,"");

        //String selection = Message._ID + "= "+readData();

        db.insertWithOnConflict(Message.TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);

        db.close();

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
           // insertData(userName,photoStatus);
            HashMap<String, Object> map= new HashMap<>();
            map.put("uid",currentUserid);
            map.put("name",userName);
            map.put("status",photoStatus);
            ref.child("users").child(currentUserid).updateChildren(map)
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

//    private void readData() {
//
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String[] request = {
//                Message._ID,
//                Message.EMAIL,
//                Message.PASSWORD
//        };
//
//        Cursor cursor = db.query(Message.TABLE,request,null,null,null,null,null);
//
//        while (cursor.moveToNext())
//        {
//            String id = cursor.getString(0);
//            String nn = cursor.getString(1);
//            String pp = cursor.getString(2);
//            Log.d("IDD",id);
//            Log.d("EMAIL",nn);
//            Log.d("PWD",pp);
//        }
//
//        cursor.close();
//
//    }



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
                            //insertData(retrieveUsername,retrieveStatus);
                            username.setText(retrieveUsername);
                            photo_status.setText(retrieveStatus);
                            Picasso.get().load(retrievePhoto).into(image);

                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUsername = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                           // insertData(retrieveUsername,retrieveStatus);
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
