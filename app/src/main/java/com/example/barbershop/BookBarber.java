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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class BookBarber extends AppCompatActivity {
    String[] branches = {
            "84 Lê Lợi, Q1, TP.HCM",
            "120 Quang Trung, Q. Gò Vấp, TP.HCM",
            "19 Linh Đông, TP. Thủ Đức, TP.HCM"
    };
    Button dateButton;
    Button timeButton;

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
        Button adressButton = findViewById(R.id.adressButton);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);

        // Thiết lập sự kiện cho Button
        adressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo AlertDialog để hiển thị các chi nhánh
                AlertDialog.Builder builder = new AlertDialog.Builder(BookBarber.this);
                builder.setTitle("Chọn chi nhánh");

                // Thiết lập danh sách chi nhánh trong AlertDialog
                builder.setItems(branches, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cập nhật chi nhánh đã chọn lên Button
                        adressButton.setText(branches[which]);
                    }
                });

                // Hiển thị AlertDialog
                builder.show();
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
                        BookBarber.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Khi người dùng chọn ngày, cập nhật text cho dateButton1
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
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
                        BookBarber.this,
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
}