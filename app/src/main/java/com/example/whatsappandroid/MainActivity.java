package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TablayoutClass tablayoutClass;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private MessageHelper helper;
    private String email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new MessageHelper(getApplicationContext());
        Toast.makeText(getApplicationContext(),"BD CREATED",Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference();
        toolbar = (Toolbar) findViewById(R.id.toolbar_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WhatsApp");

        viewPager = (ViewPager) findViewById(R.id.tabs_view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab1);
       tablayoutClass = new TablayoutClass(getSupportFragmentManager());
       viewPager.setAdapter(tablayoutClass);
       tabLayout.setupWithViewPager(viewPager);



    }

    private void readData() {

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

        Cursor cursor = db.query(Message.TABLE,request,null,null,null,null,null);

        while (cursor.moveToNext())
        {
            String id = cursor.getString(0);
            String nn = cursor.getString(1);
            String pp = cursor.getString(2);
            String email = cursor.getString(3);
            String pwd = cursor.getString(4);
            String mess = cursor.getString(5);
            String date = cursor.getString(6);
            String time = cursor.getString(7);
            Log.d("IDD",id);
            Log.d("USERNAME",nn);
            Log.d("STATUS",pp);
            Log.d("EMAIL",email);
            Log.d("PWD",pwd);
            Log.d("MESS",mess);
            Log.d("DATE",date);
            Log.d("TIME",time);
        }

        cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser==null)
        {
            Login();
        }
        else 
        {
            VerifyUser();
        }
    }

    private void VerifyUser() {
        String currentUserid = mAuth.getCurrentUser().getUid();
        ref.child("users").child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists()))
                {
//                    readData();
                    Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_LONG).show();
                }
                else
                {
                    SettingsPage();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Login() {
        Intent login = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.log_out)
        {
            mAuth.signOut();
            Login();
        }
        if (item.getItemId()==R.id.settings)
        {
            SettingsPage();
        }
        if (item.getItemId()==R.id.group_chat)
        {
            CreateGroup();
        }
        if (item.getItemId()==R.id.find_friends)
        {
            Intent find_friends_page = new Intent(getApplicationContext(),FindFriendsActivity.class);
            startActivity(find_friends_page);
        }
        if(item.getItemId()==R.id.new_disc){


        }
        return true;
    }

    private void CreateGroup()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogTheme);
        builder.setTitle("Enter Groups Name");
        final EditText field = new EditText(getApplicationContext());
        field.setHint("for example hackerone");
        builder.setView(field);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = field.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(getApplicationContext(),"Please give your group's name",Toast.LENGTH_LONG).show();
                }
                else
                {
                    NewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();

    }



    private void NewGroup(final String groupName)
    {
        ref.child("Groups").child(groupName).setValue("").
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            createGroupp(groupName);
                            Toast.makeText(getApplicationContext(),"Group Name created successfully",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void createGroupp(String groupName)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Message.GROUPS,groupName);
        db.insertWithOnConflict(Message.TABLE1,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    private void SettingsPage() {
//        email=getIntent().getExtras().get("email").toString();
//        password=getIntent().getExtras().get("password").toString();
        Intent settingPage = new Intent(getApplicationContext(),SettingsActivity.class);
       // settingPage.putExtra("email",email);
      //  settingPage.putExtra("password",password);
        startActivity(settingPage);
       // finish();
    }
}
