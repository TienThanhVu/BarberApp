package com.example.barbershop;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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


public class UserCart extends AppCompatActivity {

    private FirebaseFirestore db;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private String userName;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_cart);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Khởi tạo FirebaseUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList);
        recyclerView.setAdapter(adapter);

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Lấy tên người dùng từ bộ sưu tập users
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userName = documentSnapshot.getString("fullName");

                            if (userName != null) {
                                // Truy vấn chỉ lấy đơn hàng của người dùng hiện tại
                                db.collection("bookings")
                                        .whereEqualTo("userName", userName) // Truy vấn theo userName
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d("UserCart", "DocumentSnapshot data: " + document.getData());
                                                    Booking booking = document.toObject(Booking.class);
                                                    bookingList.add(booking);
                                                }
                                                adapter.notifyDataSetChanged();  // Cập nhật dữ liệu cho RecyclerView
                                            } else {
                                                Log.e("UserCart", "Error getting documents.", task.getException());
                                                Toast.makeText(UserCart.this, "Lỗi khi tải dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(this, "Tên người dùng không có sẵn", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserCart", "Error getting user data.", e);
                        Toast.makeText(this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> cancelBooking());

        Button buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(v -> {
            int position = adapter.getSelectedPosition(); // Lấy vị trí booking được chọn
            if (position >= 0) {
                Booking selectedBooking = bookingList.get(position); // Lấy booking đã chọn
                String bookingId = selectedBooking.getId(); // Lấy bookingId từ booking đã chọn

                // Gọi hàm confirmBooking với bookingId và đối tượng Booking
                confirmBooking(bookingId, selectedBooking);
            } else {
                Toast.makeText(this, "Vui lòng chọn đơn để xác nhận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelBooking() {
        int position = adapter.getSelectedPosition(); // Lấy vị trí của item được chọn

        if (position >= 0) {
            Booking selectedBooking = bookingList.get(position);
            String bookingDate = selectedBooking.getDate(); // Ngày đặt lịch
            String bookingTime = selectedBooking.getTime(); // Thời gian đặt lịch
            String bookingId = selectedBooking.getId(); // ID của đơn hàng

            new AlertDialog.Builder(UserCart.this)
                    .setTitle("Xác nhận hủy dich vụ")
                    .setMessage("Bạn có chắc chắn muốn hủy đơn dịch vụ này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Kiểm tra thời gian hủy đơn
                        if (isCancellationAllowed(bookingDate, bookingTime)) {
                            // Lưu thông tin hủy vào history
                            Map<String, Object> historyData = new HashMap<>();
                            historyData.put("action", "Đã hủy đơn dịch vụ");
                            historyData.put("details", selectedBooking.toString()); // Ghi lại toàn bộ chi tiết đơn hàng
                            historyData.put("timestamp", FieldValue.serverTimestamp());

                            db.collection("bookings").document(bookingId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Thêm vào history
                                        db.collection("users").document(currentUser.getUid())
                                                .collection("history")
                                                .document(bookingId)
                                                .set(historyData)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    bookingList.remove(position);
                                                    adapter.notifyItemRemoved(position);
                                                    adapter.notifyItemRangeChanged(position, bookingList.size());
                                                    Toast.makeText(UserCart.this, "Hủy đơn hàng thành công và đã lưu vào lịch sử", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(UserCart.this, "Lỗi khi lưu lịch sử", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(UserCart.this, "Có lỗi xảy ra khi hủy đơn hàng", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(UserCart.this, "Không thể hủy đơn hàng sau thời gian cho phép", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            Toast.makeText(UserCart.this, "Vui lòng chọn đơn hàng để hủy", Toast.LENGTH_SHORT).show();
        }
    }

    // kiểm tra người dùng hủy đơn trước 2h cùng ngày
    private boolean isCancellationAllowed(String bookingDate, String bookingTime) {
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
                long twoHoursInMillis = 2 * 60 * 60 * 1000; // 2 giờ

                return timeDifference > twoHoursInMillis;
            }

            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xác nhận đơn hàng
    private void confirmBooking(String bookingId, Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xác nhận đã sử dụng dịch vụ không?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Lưu thông tin xác nhận vào history
                    Map<String, Object> historyData = new HashMap<>();
                    historyData.put("action", "Đã xác nhận sử dụng dịch vụ");
                    historyData.put("details", booking.toString()); // Ghi lại toàn bộ chi tiết đơn hàng
                    historyData.put("timestamp", FieldValue.serverTimestamp());

                    db.collection("bookings").document(bookingId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                // Thêm vào history
                                db.collection("users").document(currentUser.getUid())
                                        .collection("history")
                                        .document(bookingId)
                                        .set(historyData)
                                        .addOnSuccessListener(aVoid1 -> {
                                            bookingList.remove(booking);
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(UserCart.this, "Xác nhận thành công và đã lưu vào lịch sử", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("UserCart", "Error adding document to history.", e);
                                            Toast.makeText(UserCart.this, "Lỗi khi lưu lịch sử", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UserCart", "Error deleting document.", e);
                                Toast.makeText(UserCart.this, "Có lỗi xảy ra khi xóa đơn hàng", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}