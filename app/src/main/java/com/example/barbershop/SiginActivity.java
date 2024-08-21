package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SiginActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button buttonSignIn;
    TextView textViewSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        textViewSignUp = findViewById(R.id.Signup);
        buttonSignIn = findViewById(R.id.Signin);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        // Initialize Firebase Auth
        // Sau khi khởi tạo FirebaseApp, bạn có thể lấy instance của FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressGone);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SiginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is required!");
                    return;
                }
                if (password.length() < 6) {
                    editTextPassword.setError("Password must be >= 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(SiginActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SiginActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(SiginActivity.this, "Sign In Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
//        textViewSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SiginActivity.this, SignupActivity.class));
//            }
//        });
//        textViewSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Chuyển đến SignupActivity khi ấn vào TextView signup
//                Intent in = new Intent(SiginActivity.this, SignupActivity.class);
//                Intent intent = new Intent(SiginActivity.this, SignupActivity.class);
//                startActivity(intent);
//            }
//        });

    }
}