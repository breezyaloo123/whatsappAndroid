package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginButton;
    private EditText emaileditext,pwdeditext;
    private TextView signuplink,forgetpawdlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.login_button);
        emaileditext = findViewById(R.id.login_email);
        pwdeditext = findViewById(R.id.login_pwd);
        forgetpawdlink = findViewById(R.id.login_forget_pwd);
        signuplink = findViewById(R.id.login_new_account);

        signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newaccount = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(newaccount);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CorrectLogin();


            }
        });
    }

    private void CorrectLogin() {
        String email = emaileditext.getText().toString();
        String pwd = pwdeditext.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(),"Please enter your Email",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(pwd))
        {
            Toast.makeText(getApplicationContext(),"Please enter your Password",Toast.LENGTH_LONG).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                MainActivityPage();
                                Toast.makeText(getApplicationContext(),"Logged Successful",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                String error  = task.getException().toString();
                                Toast.makeText(getApplicationContext(),"Error: "+error,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }



    private void MainActivityPage() {
        Intent homePage = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(homePage);
        finish();
    }
}
