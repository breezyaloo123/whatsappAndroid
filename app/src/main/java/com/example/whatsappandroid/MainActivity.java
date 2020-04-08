package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        if (item.getItemId()==R.id.find_friends)
        {

        }
        return true;
    }

    private void SettingsPage() {
        Intent settingPage = new Intent(getApplicationContext(),SettingsActivity.class);
        startActivity(settingPage);
        finish();
    }
}
