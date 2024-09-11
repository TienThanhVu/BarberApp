package com.example.barbershop;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserBookBarber extends AppCompatActivity {
    Button buttonDate, buttonTime, buttonAddress, buttonBook, buttonBarber, buttonVoucher, buttonService;
    List<String> branchesList = new ArrayList<>(); // Danh sách chi nhánh từ Firestore
    List<String> barberList = new ArrayList<>(); // Danh sách barber từ Firestore
    private List<Voucher> voucherList = new ArrayList<>();
    private List<String> addressList = new ArrayList<>(); // Danh sách địa chỉ cửa hàng
    private static final String TAG = "UserBookBarber"; // Tag để debug
    FirebaseFirestore db;
    private List<String> branchIds = new ArrayList<>();
    private String selectedBranch, selectedBarber, selectedDate, selectedTime, selectedService, selectedAddress;
    private Voucher selectedVoucher;
    private TextView textViewFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_book_barber);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Khai báo biến Button
        buttonAddress = findViewById(R.id.buttonAddress);
        buttonBarber = findViewById(R.id.buttonBarber);
        buttonBook = findViewById(R.id.buttonBook);
        buttonVoucher = findViewById(R.id.buttonVocher);
        buttonService = findViewById(R.id.buttonService);
        buttonDate = findViewById(R.id.buttonChooseDate);
        buttonTime = findViewById(R.id.buttonChooseTime);

        db = FirebaseFirestore.getInstance();

        buttonService.setOnClickListener(v -> {
            // Danh sách dịch vụ
            final String[] servicesArray = {"Cắt tóc", "Uốn tóc", "Nhuộm tóc"};

            // Tạo AlertDialog để hiển thị các dịch vụ
            AlertDialog.Builder builder = new AlertDialog.Builder(UserBookBarber.this);
            builder.setTitle("Chọn dịch vụ");

            // Thiết lập danh sách dịch vụ trong AlertDialog
            builder.setItems(servicesArray, (dialog, which) -> {
                // Cập nhật dịch vụ đã chọn lên Button
                selectedService = servicesArray[which];
                buttonService.setText(selectedService);
            });

            // Hiển thị AlertDialog
            builder.show();
        });

        // Chọn chi nhánh
        buttonAddress.setOnClickListener(v -> loadStores());

        // Chọn thợ cắt tóc
        buttonBarber.setOnClickListener(v -> {
            if (selectedBranch != null) {
                loadBarbers(selectedBranch); // Truyền ID cửa hàng
            } else {
                Toast.makeText(UserBookBarber.this, "Vui lòng chọn chi nhánh trước", Toast.LENGTH_SHORT).show();
            }
        });

        // chọn ngày
        buttonDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    UserBookBarber.this,
                    (view, year1, month1, dayOfMonth) -> {
                        selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        buttonDate.setText(selectedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });

        buttonTime.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    UserBookBarber.this,
                    (view, hourOfDay, minute1) -> {
                        selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        buttonTime.setText(selectedTime);
                    },
                    hour, minute, true);

            timePickerDialog.show();
        });

        // Chọn voucher
        buttonVoucher.setOnClickListener(v -> loadVouchers());

        // Đặt lịch hẹn
        buttonBook.setOnClickListener(v -> {
            if (validateBookingDetails()) {
                bookBarber();
            } else {
                Toast.makeText(UserBookBarber.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tải danh sách cửa hàng từ Firestore
    private void loadStores() {
        Log.d(TAG, "Đang tải: đang tải danh sách");

        db.collection("stores")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        branchesList.clear();
                        addressList.clear();
                        branchIds.clear(); // Danh sách lưu trữ ID của các cửa hàng

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Sử dụng trường đúng tên và storeId
                            String address = document.getString("address");
                            String Id = document.getString("id"); // Lấy storeId từ tài liệu

                            if (address != null && Id != null) {
                                // Thay đổi branchesList để lưu storeId và addressList để lưu địa chỉ
                                branchesList.add(address); // Lưu ID cửa hàng để sử dụng sau
                                branchIds.add(Id);
                            }
                        }

                        if (!branchesList.isEmpty()) {
                            showStoreSelection();
                        } else {
                            Toast.makeText(UserBookBarber.this, "Không có cửa hàng nào.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UserBookBarber.this, "Lỗi khi lấy danh sách cửa hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showStoreSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn cửa hàng");

        // Chuyển danh sách địa chỉ thành mảng để hiển thị trong hộp thoại
        String[] branchesArray = branchesList.toArray(new String[0]);

        builder.setItems(branchesArray, (dialog, which) -> {
            // Lưu ID của cửa hàng và địa chỉ được chọn
            selectedBranch = branchIds.get(which);
            selectedAddress = branchesList.get(which); // Lưu địa chỉ được chọn
            // Hiển thị địa chỉ cửa hàng được chọn trong buttonAddress
            buttonAddress.setText(selectedAddress);
            // Tải danh sách thợ cắt tóc của cửa hàng được chọn
            loadBarbers(selectedBranch);
        });

        builder.show();
    }

    private void loadBarbers(String storeId) {
        db.collection("barbers")
                .whereEqualTo("storeId", storeId) // Lọc thợ cắt tóc theo storeId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        barberList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String barberName = document.getString("name");

                            if (barberName != null) {
                                barberList.add(barberName);
                            }
                        }

                        if (!barberList.isEmpty()) {
                            showBarberSelection();
                        } else {
                            Toast.makeText(UserBookBarber.this, "Không có thợ cắt tóc nào.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UserBookBarber.this, "Lỗi khi lấy danh sách thợ cắt tóc", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBarberSelection() {
        String[] barbersArray = barberList.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserBookBarber.this);
        builder.setTitle("Chọn thợ cắt tóc");

        builder.setItems(barbersArray, (dialog, which) -> {
            selectedBarber = barbersArray[which];
            buttonBarber.setText(selectedBarber);
        });

        builder.show();
    }

    // Tải danh sách voucher từ Firestore
    private void loadVouchers() {
        db.collection("vouchers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        voucherList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Integer percent = document.getLong("percent") != null ? document.getLong("percent").intValue() : 0;
                            String note = document.getString("note");
                            Voucher voucher = new Voucher();
                            voucher.setPercent(percent);
                            voucher.setNote(note);
                            voucherList.add(voucher);
                        }
                        showVoucherSelection();
                    } else {
                        Toast.makeText(UserBookBarber.this, "Lỗi khi lấy danh sách voucher", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hiển thị danh sách voucher
    private void showVoucherSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn voucher");

        // Chuyển danh sách voucher thành mảng để hiển thị trong hộp thoại
        String[] voucherArray = voucherList.stream().map(Voucher::toString).toArray(String[]::new);

        builder.setItems(voucherArray, (dialog, which) -> {
            // Lấy voucher được chọn từ danh sách voucher
            selectedVoucher = voucherList.get(which); // Lưu đối tượng Voucher vào biến selectedVoucher

            // Hiển thị thông tin voucher trên button
            buttonVoucher.setText(selectedVoucher.toString());
        });

        builder.show();
    }

    // Interface cho callback
    public interface OnGetUserNameListener {
        void onCallback(String userName);
    }

    // Phương thức để lấy userName
    private void getUserName(OnGetUserNameListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUID = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userUID);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String fullName = document.getString("fullName");
                        if (fullName != null && !fullName.isEmpty()) {
                            listener.onCallback(fullName);
                        } else {
                            listener.onCallback(currentUser.getEmail());
                        }
                    } else {
                        listener.onCallback(currentUser.getEmail());
                    }
                } else {
                    listener.onCallback(currentUser.getEmail());
                }
            });
        } else {
            listener.onCallback("User");
        }
    }

    // Phương thức để đặt lịch
    private void bookBarber() {
        getUserName(userName -> {
            // Tạo ID ngẫu nhiên cho đơn hàng
            String bookingId = UUID.randomUUID().toString();

            // Tạo đối tượng Booking với ID mới
            Booking booking = new Booking(
                    bookingId,                   // ID của đơn hàng
                    selectedBranch,
                    selectedBarber,
                    selectedDate,
                    selectedTime,
                    selectedService,
                    selectedVoucher != null ? selectedVoucher.toString() : "",
                    selectedAddress,
                    userName
            );

            // Hiển thị thông tin trong Toast
            String bookingInfo = String.format("Người dùng: %s\nĐịa chỉ: %s\nChi nhánh: %s\nThợ cắt tóc: %s\nNgày: %s\nGiờ: %s\nDịch vụ: %s\nVoucher: %s\nID: %s",
                    userName, selectedAddress, selectedBranch, selectedBarber, selectedDate, selectedTime, selectedService, selectedVoucher != null ? selectedVoucher.toString() : "Không có", bookingId);
            Toast.makeText(UserBookBarber.this, bookingInfo, Toast.LENGTH_LONG).show();

            // Tiếp tục thực hiện đặt lịch
            db.collection("bookings")
                    .document(bookingId) // Đặt ID cho tài liệu
                    .set(booking) // Sử dụng phương thức set để thêm tài liệu với ID tùy chỉnh
                    .addOnSuccessListener(aVoid -> {
                        // Lưu thông tin vào lịch sử
                        saveToHistory(bookingId, "Đặt lịch", bookingInfo);

                        Toast.makeText(UserBookBarber.this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                        resetBookingDetails();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserBookBarber.this, "Lỗi khi đặt lịch!", Toast.LENGTH_SHORT).show();
                        Log.e("BookingError", "Error booking barber: ", e);
                    });
        });
    }

    private void saveToHistory(String bookingId, String action, String details) {
        // Lưu thông tin vào lịch sử
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("action", action);
        historyEntry.put("details", details);
        historyEntry.put("timestamp", FieldValue.serverTimestamp());

        db.collection("bookings").document(bookingId).collection("history").add(historyEntry)
                .addOnSuccessListener(documentReference -> Log.d("History", "History entry added successfully"))
                .addOnFailureListener(e -> Log.e("History", "Error adding history entry", e));
    }


    // Xác thực thông tin đặt lịch
    private boolean validateBookingDetails() {
        return selectedBranch != null && selectedBarber != null && selectedDate != null && selectedTime != null && selectedService != null;
    }

    private void resetBookingDetails() {
        selectedBranch = null;
        selectedBarber = null;
        selectedDate = null;
        selectedTime = null;
        selectedService = null;
        selectedVoucher = null;

        buttonAddress.setText("Chọn chi nhánh");
        buttonBarber.setText("Chọn thợ cắt tóc");
        buttonDate.setText("Chọn ngày");
        buttonTime.setText("Chọn giờ");
        buttonService.setText("Chọn dịch vụ");
        buttonVoucher.setText("Chọn voucher");
    }

}