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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button buttonSignIn;
    TextView textViewSignUp;
    FirebaseFirestore db; // Thêm FirebaseFirestore

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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore
        progressBar = findViewById(R.id.progressGone);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email không đúng!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Mật khẩu không đúng!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            // Lấy thông tin người dùng từ Firestore dựa trên userId
                            db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");
                                            Intent intent;
                                            if ("admin".equals(role)) {
                                                // người dùng là admin
                                                Toast.makeText(SignIn.this, "Đăng nhập thành công với vai trò admin!", Toast.LENGTH_SHORT).show();
                                                intent = new Intent(SignIn.this, AdminHome.class); // Màn hình admin
                                            } else {
                                                // người dùng là user
                                                Toast.makeText(SignIn.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                                intent = new Intent(SignIn.this, Main.class); // Màn hình chứa Fragment
                                            }
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignIn.this, "Đăng nhập không thành công: Mật khẩu hoặc Email không khớp! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Người dùng nhấn vào Sign Up tạo tài khoản
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
    }

}
