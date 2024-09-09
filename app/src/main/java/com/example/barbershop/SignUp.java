package com.example.barbershop;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextName;
    Button buttonSignUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textViewSignIn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textViewSignIn = findViewById(R.id.signin);
        editTextName = findViewById(R.id.name);
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
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                String name = editTextName.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Bắt buộc phải có email!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Cần phải nhập mật khẩu!");
                }
                if (password.length() < 6) {
                    editTextPassword.setError("Mật khẩu của bạn phải dài hơn 6 ký tự!");
                }
                if (!password.equals(confirmPassword)) {
                    editTextConfirmPassword.setError("Mật khẩu không khớp!");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    editTextName.setError("Họ và tên không được để trống!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Đăng ký thành công
                                    String userId = mAuth.getCurrentUser().getUid();

                                    // Lưu thông tin người dùng vào Firestore
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("fullName", name); // tên người dùng nhập từ TextInput
                                    user.put("email", email);
                                    user.put("role", "user");

                                    // Lưu thông tin người dùng vào Firestore
                                    db.collection("users").document(userId)
                                            .set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Sau khi lưu thành công, chuyển về màn hình Home
                                                        Toast.makeText(SignUp.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(SignUp.this, SignIn.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignUp.this, "Lỗi khi lưu thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Xử lý lỗi đăng ký
                                    Toast.makeText(SignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // người dùng nhấn vào Sign In đăng nhập
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });
    }
}