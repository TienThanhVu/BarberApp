package com.example.barbershop;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserBookBarber extends AppCompatActivity {
    Button dateButton;
    Button timeButton;
    Button adressButton;
    List<String> branchesList = new ArrayList<>(); // Danh sách chi nhánh từ Firestore
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.book_barber);
        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        // Khai báo biến Button
        adressButton = findViewById(R.id.adressButton);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);

        db = FirebaseFirestore.getInstance();

        // Load danh sách chi nhánh từ Firestore
        loadBranchesFromFirestore();

        adressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!branchesList.isEmpty()) {
                    // Chuyển danh sách thành mảng String[] để sử dụng trong AlertDialog
                    String[] branchesArray = branchesList.toArray(new String[0]);

                    // Tạo AlertDialog để hiển thị các chi nhánh
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserBookBarber.this);
                    builder.setTitle("Chọn chi nhánh");

                    // Thiết lập danh sách chi nhánh trong AlertDialog
                    builder.setItems(branchesArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cập nhật chi nhánh đã chọn lên Button
                            adressButton.setText(branchesArray[which]);
                        }
                    });

                    // Hiển thị AlertDialog
                    builder.show();
                }
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ngày hiện tại
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Tạo DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UserBookBarber.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Khi người dùng chọn ngày, cập nhật text cho dateButton
                                String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                                dateButton.setText(selectedDate);
                            }
                        },
                        year, month, day);

                // Hiển thị DatePickerDialog
                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giờ hiện tại
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Tạo TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        UserBookBarber.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Khi người dùng chọn giờ, cập nhật text cho timeButton
                                String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                                timeButton.setText(selectedTime);
                            }
                        },
                        hour, minute, true);

                // Hiển thị TimePickerDialog
                timePickerDialog.show();
            }
        });
    }

    // phương thức loadBranchesFromFirestore
    private void loadBranchesFromFirestore() {
        // Truy vấn từ bộ sưu tập "stores" để lấy danh sách chi nhánh
        db.collection("stores")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        branchesList.clear(); // Xóa danh sách cũ
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Lấy địa chỉ từ mỗi cửa hàng và thêm vào danh sách
                            String branchAddress = document.getString("address");
                            if (branchAddress != null) {
                                branchesList.add(branchAddress);
                            }
                        }
                    }
                });
    }
}