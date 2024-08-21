package com.example.barbershop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
//    private ImageView imgList;
//    private String username = "Vũ Tiến Thành";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        imgList = findViewById(R.id.imgList);
//
//        LinearLayout calendarLayout = findViewById(R.id.calendar);
//
//        calendarLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Tạo Intent để chuyển đến BookActivity
//                Intent intent = new Intent(HomeActivity.this, BookActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        imgList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPopupMenu(v);
//            }
//        });
//
//        // Ẩn ActionBar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
//    }
//
//    private void showPopupMenu(View view) {
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        MenuInflater inflater = popupMenu.getMenuInflater();
//        inflater.inflate(R.menu.menu_popup, popupMenu.getMenu());
//
//        // Thay đổi tiêu đề của MenuItem
//        MenuItem usernameItem = popupMenu.getMenu().findItem(R.id.action_userName);
//        if (usernameItem != null) {
//            usernameItem.setTitle(username);
//        }
//
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_userName:
//                        // nhấp vào tên người dùng
//                    case R.id.action_notification:
//                        Toast.makeText(HomeActivity.this, "Notification clicked", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.action_voucher:
//                        Toast.makeText(HomeActivity.this, "Voucher clicked", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.action_logOut:
//                        Intent intent = new Intent(HomeActivity.this, SiginActivity.class);
//                        startActivity(intent);
//                        finish();
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });
//        popupMenu.show();
//    }
}