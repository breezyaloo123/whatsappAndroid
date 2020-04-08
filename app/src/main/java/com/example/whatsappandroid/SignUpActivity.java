package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private Button signButton;
    private EditText signupemail,signuppwd;
    private TextView account;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signButton = findViewById(R.id.sign_button);
        signupemail = findViewById(R.id.sign_email);
        signuppwd = findViewById(R.id.sign_pwd);
        account = findViewById(R.id.sign_new_account);
        loading = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginPage();
            }
        });

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void LoginPage() {
        Intent loginPage=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(loginPage);
    }

    private void CreateAccount() {

        String email = signupemail.getText().toString();
        String pwd = signuppwd.getText().toString();
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
            loading.setTitle("Creating new Account");
            loading.setMessage("Please wait while we are creating new Account for You");
            loading.setCanceledOnTouchOutside(true);
            loading.show();
            mAuth.createUserWithEmailAndPassword(email,pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                String currentUserId=mAuth.getCurrentUser().getUid();
                                ref.child("users").child(currentUserId).setValue("");
                                HomePage();
                                Toast.makeText(getApplicationContext(),"Account created successfully",Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }
                            else
                            {
                                String error = task.getException().toString();
                                Toast.makeText(getApplicationContext(),"Error: "+error,Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }

                        }
                    });
        }
    }

    private void HomePage() {

        Intent home = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(home);
        finish();
    }
}
