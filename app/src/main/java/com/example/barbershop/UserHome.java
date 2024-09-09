package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHome extends AppCompatActivity {
    FirebaseAuth mAuth;
    private ImageView imgList;
    private FirebaseFirestore db;
    private TextView txtUsername;
    private String username; // biến lưu tên người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        txtUsername = findViewById(R.id.txtUsername);

        // Kiểm tra người dùng hiện tại
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Nếu không có người dùng đang đăng nhập, chuyển về SignInActivity
            Toast.makeText(UserHome.this, "Bạn phải đăng nhập để truy cập trang này", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserHome.this, SignIn.class));
            finish();
        } else {
            // Lấy UID của người dùng hiện tại
            String userId = currentUser.getUid();

            // Truy cập Firestore để lấy thông tin người dùng
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // tên từ Firestore và đặt vào TextView
                        username = document.getString("fullName");
                        txtUsername.setText(username);
                    } else {
                        Toast.makeText(UserHome.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý lỗi
                    Toast.makeText(UserHome.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        }

        imgList = findViewById(R.id.imgList);
        // hiển thị PopupMenu
        imgList.setOnClickListener(v -> showPopupMenu(v));

        // layout calendar
        LinearLayout calendarLayout = findViewById(R.id.calendar);
        calendarLayout.setOnClickListener(v -> {
            // Intent chuyển đến BookActivity
            Intent intent = new Intent(UserHome.this, UserBookBarber.class);
            startActivity(intent);
        });


    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popupMenu.getMenu());

        //  tên người dùng
        MenuItem usernameItem = popupMenu.getMenu().findItem(R.id.action_userName);
        if (usernameItem != null && username != null) {
            usernameItem.setTitle(username); // Cập nhật tên người dùng vào menu
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logOut) {
                // Xử lý sự kiện logout
                mAuth.signOut();
                startActivity(new Intent(UserHome.this, SignIn.class));
                finish();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}