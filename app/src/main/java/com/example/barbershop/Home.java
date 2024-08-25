package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    FirebaseAuth mAuth;
    private ImageView imgList;
    private String username = "Vũ Tiến Thành";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // Kiểm tra người dùng hiện tại
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Nếu không có người dùng đang đăng nhập, chuyển về SignInActivity
            Toast.makeText(Home.this, "You must be signed in to access this page", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Home.this, SignIn.class));
            finish();
        }

        imgList = findViewById(R.id.imgList);

        LinearLayout calendarLayout = findViewById(R.id.calendar);

        calendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển đến BookActivity
                Intent intent = new Intent(Home.this, BookDate.class);
                startActivity(intent);
            }
        });

        imgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });


    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popupMenu.getMenu());

        // Thay đổi tiêu đề của MenuItem
        MenuItem usernameItem = popupMenu.getMenu().findItem(R.id.action_userName);
        if (usernameItem != null) {
            usernameItem.setTitle(username);
        }

//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_userName:
//                        // nhấp vào tên người dùng
//                    case R.id.action_notification:
//                        Toast.makeText(Home.this, "Notification clicked", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.action_voucher:
//                        Toast.makeText(Home.this, "Voucher clicked", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.action_logOut:
//                        Intent intent = new Intent(Home.this, SignIn.class);
//                        startActivity(intent);
//                        finish();
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });
//        popupMenu.show();
    }
}