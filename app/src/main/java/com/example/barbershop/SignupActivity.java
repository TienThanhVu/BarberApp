package com.example.barbershop;

import static androidx.core.content.ContextCompat.startActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignupActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonSignUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textViewSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        textViewSignIn = findViewById(R.id.signin);
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến SignupActivity khi ấn vào TextView signup

                Intent intent = new Intent(SignupActivity.this, SiginActivity.class);
                startActivity(intent);
            }
        });
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmPassword);
        buttonSignUp = findViewById(R.id.signup);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressGone);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is required!");
                }
                if (password.length() < 6) {
                    editTextPassword.setError("Password must be >= 6 characters");
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, SiginActivity.class));
                                    finish();
                                } else {
                                    // Xử lý lỗi
                                    Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
//            }
//        });
//        textViewSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
//                finish();
//            }
//        });
//        }
//            }
//        });
            }
        });
    }
}