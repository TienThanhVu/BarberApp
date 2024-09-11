package com.example.barbershop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHome extends AppCompatActivity {

    FirebaseAuth mAuth;
    private ImageView imgList;
    private FirebaseFirestore db;
    private TextView txtUsername;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        txtUsername = findViewById(R.id.txtUsername);

        // quản lý người dùng
        LinearLayout userLayout = findViewById(R.id.user);
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHome.this, AdminManageUser.class);
                startActivity(intent);
            }
        });

        // quản lý cửa hàng
        LinearLayout storeLayout = findViewById(R.id.store);
        storeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHome.this, AdminManageLacation.class);
                startActivity(intent);
            }
        });

        // quản lý đơn hàng
        LinearLayout cartLayout = findViewById(R.id.cart);
        cartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển đến CartManagementActivity
            }
        });

        // quản lý thợ cắt
        LinearLayout barberLayout = findViewById(R.id.barber);
        barberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển đến BarberManagementActivity
                Intent intent = new Intent(AdminHome.this, AdminManageBarber.class);
                startActivity(intent);
            }
        });

        // quản lý voucher
        LinearLayout voucherLayout = findViewById(R.id.voucher);
        voucherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển đến VoucherManagementActivity
                Intent intent = new Intent(AdminHome.this, AdminManageVoucher.class);
                startActivity(intent);
            }
        });

        // quản lý địa chỉ
        LinearLayout locationLayout = findViewById(R.id.location);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển đến LocationManagementActivity
            }
        });

        imgList = findViewById(R.id.imgList);
        // hiển thị PopupMenu
        imgList.setOnClickListener(v -> showPopupMenu(v));
    }

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logOut) {
                // Xử lý sự kiện logout
                mAuth.signOut();
                startActivity(new Intent(AdminHome.this, SignIn.class));
                finish();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}