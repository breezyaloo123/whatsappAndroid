package com.example.whatsappandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TablayoutClass tablayoutClass;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    private void Login() {
        Intent login = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(login);
    }
}
