package com.example.barbershop;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class UserCart extends AppCompatActivity {

    private FirebaseFirestore db;
    private BookingAdapter adapter;
    private List<Booking> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_cart);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList);
        recyclerView.setAdapter(adapter);

        db.collection("bookings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Booking booking = document.toObject(Booking.class);
                            bookingList.add(booking);
                        }
                        adapter.notifyDataSetChanged();  // Cập nhật dữ liệu cho RecyclerView
                    }
                });

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> cancelBooking());

    }

    private void cancelBooking() {
        int position = adapter.getSelectedPosition(); // Lấy vị trí của item được chọn

        if (position >= 0) {
            Booking selectedBooking = bookingList.get(position);
            String bookingDate = selectedBooking.getDate(); // Ngày đặt lịch
            String bookingTime = selectedBooking.getTime(); // Thời gian đặt lịch
            String bookingId = selectedBooking.getId(); // ID của đơn hàng

            // Tạo và hiển thị hộp thoại xác nhận
            new AlertDialog.Builder(UserCart.this)
                    .setTitle("Xác nhận hủy đơn hàng")
                    .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này? Đơn chỉ có thể được hủy trước ít nhất 2 giờ so với thời gian đặt lịch.")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Kiểm tra thời gian hủy đơn
                        if (isCancellationAllowed(bookingDate, bookingTime)) {
                            // Xóa đơn hàng khỏi Firestore
                            db.collection("bookings").document(bookingId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Thêm thông tin hủy vào lịch sử
                                        addCancellationToHistory(selectedBooking);

                                        // Cập nhật danh sách và giao diện người dùng
                                        bookingList.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, bookingList.size());
                                        Toast.makeText(UserCart.this, "Xóa thành công đơn hàng", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("UserCart", "Error deleting document.", e);
                                        Toast.makeText(UserCart.this, "Có lỗi xảy ra khi xóa đơn hàng", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(UserCart.this, "Không thể hủy đơn hàng. Đơn chỉ có thể được hủy trước ít nhất 2 giờ so với thời gian đặt lịch.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss()) // Đóng hộp thoại mà không thực hiện hành động
                    .create()
                    .show();
        } else {
            Toast.makeText(UserCart.this, "Vui lòng chọn đơn hàng để hủy", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCancellationAllowed(String bookingDate, String bookingTime) {
        // Định dạng ngày và giờ
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        try {
            // Lấy thời gian hiện tại
            Calendar nowCalendar = Calendar.getInstance();
            String nowDate = dateFormat.format(nowCalendar.getTime());
            String nowTime = timeFormat.format(nowCalendar.getTime());
            String nowDateTimeString = nowDate + " " + nowTime;

            Date currentDateTime = dateTimeFormat.parse(nowDateTimeString);

            // Lấy thời gian đặt lịch từ Firestore
            String bookingDateTimeString = bookingDate + " " + bookingTime;
            Date bookingDateTime = dateTimeFormat.parse(bookingDateTimeString);

            // Kiểm tra thời gian hủy đơn
            if (currentDateTime != null && bookingDateTime != null) {
                long timeDifference = bookingDateTime.getTime() - currentDateTime.getTime();
                long oneHourInMillis = 2 * 60 * 60 * 1000;

                return timeDifference > oneHourInMillis;
            }

            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addCancellationToHistory(Booking booking) {
        // Tạo ID ngẫu nhiên cho lịch sử hủy
        String historyId = UUID.randomUUID().toString();

        // Tạo đối tượng lịch sử hủy
        Map<String, Object> cancellationHistory = new HashMap<>();
        cancellationHistory.put("bookingId", booking.getId());
        cancellationHistory.put("userName", booking.getUserName());
        cancellationHistory.put("branch", booking.getBranchId());
        cancellationHistory.put("barber", booking.getBarberName());
        cancellationHistory.put("date", booking.getDate());
        cancellationHistory.put("time", booking.getTime());
        cancellationHistory.put("service", booking.getService());
        cancellationHistory.put("voucher", booking.getVoucher());
        cancellationHistory.put("address", booking.getAddress());
        cancellationHistory.put("cancellationDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime()));

        // Lưu vào Firestore
        db.collection("cancellation_history").document(historyId)
                .set(cancellationHistory)
                .addOnSuccessListener(aVoid -> Log.d("UserCart", "Cancellation history added successfully"))
                .addOnFailureListener(e -> Log.e("UserCart", "Error adding cancellation history", e));
    }

}