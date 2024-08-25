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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignIn extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button buttonSignIn;
    TextView textViewSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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

                Intent intent = new Intent(SignIn.this, Home.class);
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

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this, Home.class));
                        } else {
                            Toast.makeText(SignIn.this, "Sign In Failed: Passwords or Email do not match!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        // Xử lý sự kiện khi người dùng nhấn vào "Sign Up"
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });

        // Chuyển đến HomeActivity nếu không cần nhập thông tin (phục vụ cho mục đích thử nghiệm)
        buttonSignIn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(SignIn.this, Home.class));
                finish();
                return true;
            }
        });
    }
}