package com.example.barbershop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminManageVoucher extends AppCompatActivity {

    private RecyclerView recyclerViewVoucher;
    private VoucherAdapter voucherAdapter;
    private List<Voucher> voucherList;
    private FirebaseFirestore db;
    private Button buttonAddVoucher, buttonEditVoucher, buttonDeleteVoucher;
    private EditText editTextVoucherCode, editTextDiscountAmount, editTextVoucherNote, editTextVoucherExDate;
    private Voucher selectedVoucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_voucher);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerViewVoucher = findViewById(R.id.recyclerViewVoucher);
        buttonAddVoucher = findViewById(R.id.buttonAddVoucher);
        buttonEditVoucher = findViewById(R.id.buttonEditVoucher);
        buttonDeleteVoucher = findViewById(R.id.buttonDeleteVoucher);
        editTextVoucherCode = findViewById(R.id.editTexVoucherCode);
        editTextDiscountAmount = findViewById(R.id.editTextVoucherPercent);
        editTextVoucherNote = findViewById(R.id.editTextVoucherNote);
        editTextVoucherExDate = findViewById(R.id.editTextVoucherExDate);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        voucherList = new ArrayList<>();
        voucherAdapter = new VoucherAdapter(voucherList, voucher -> {
            selectedVoucher = voucher;
            editTextVoucherCode.setText(selectedVoucher.getCode());
            editTextDiscountAmount.setText(String.valueOf(selectedVoucher.getPercent()));
            editTextVoucherNote.setText(selectedVoucher.getNote());
            editTextVoucherExDate.setText(selectedVoucher.getNote());
        });

        recyclerViewVoucher.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVoucher.setAdapter(voucherAdapter);

        loadVoucherList(); // Tải danh sách voucher

        loadVoucherList(); // Tải danh sách voucher

        buttonAddVoucher.setOnClickListener(v -> addVoucher());
        buttonEditVoucher.setOnClickListener(v -> {
            if (selectedVoucher != null) {
                updateVoucher(selectedVoucher);
            } else {
                Toast.makeText(this, "Vui lòng chọn voucher để cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
        buttonDeleteVoucher.setOnClickListener(v -> {
            if (selectedVoucher != null) {
                deleteVoucher(selectedVoucher);
            } else {
                Toast.makeText(this, "Vui lòng chọn voucher để xóa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVoucherList() {
        CollectionReference vouchersRef = db.collection("vouchers");
        vouchersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                voucherList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Voucher voucher = document.toObject(Voucher.class);
                    voucher.setId(document.getId());
                    voucherList.add(voucher);
                }
                voucherAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
            } else {
                Toast.makeText(this, "Lỗi tải danh sách voucher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addVoucher() {
        String code = editTextVoucherCode.getText().toString();
        String discountAmountStr = editTextDiscountAmount.getText().toString();
        String note = editTextVoucherNote.getText().toString();
        String exDateStr = editTextVoucherExDate.getText().toString();

        if (code.isEmpty() || discountAmountStr.isEmpty() || exDateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int discountAmount = Integer.parseInt(discountAmountStr);
        Date exDate = parseDate(exDateStr);
        Voucher voucher = new Voucher(null, code, discountAmount, note, exDate);

        db.collection("vouchers").add(voucher).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadVoucherList(); // Tải lại danh sách
                editTextVoucherCode.setText("");
                editTextDiscountAmount.setText("");
                editTextVoucherNote.setText("");
                editTextVoucherExDate.setText("");
                Toast.makeText(this, "Thêm voucher thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi thêm voucher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVoucher(Voucher voucher) {
        String code = editTextVoucherCode.getText().toString();
        String discountAmountStr = editTextDiscountAmount.getText().toString();
        String note = editTextVoucherNote.getText().toString();
        String exDateStr = editTextVoucherExDate.getText().toString();


        if (code.isEmpty() || discountAmountStr.isEmpty() || exDateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int discountAmount = Integer.parseInt(discountAmountStr);
        Date exDate = parseDate(exDateStr);
        voucher.setCode(code);
        voucher.setPercent(discountAmount);
        voucher.setNote(note);
        voucher.setExDate(exDate);

        db.collection("vouchers").document(voucher.getId()).set(voucher).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadVoucherList();
                editTextVoucherCode.setText("");
                editTextDiscountAmount.setText("");
                editTextVoucherNote.setText("");
                editTextVoucherExDate.setText("");
                Toast.makeText(this, "Cập nhật voucher thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi cập nhật voucher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteVoucher(Voucher voucher) {
        db.collection("vouchers").document(voucher.getId()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadVoucherList();
                editTextVoucherCode.setText("");
                editTextDiscountAmount.setText("");
                editTextVoucherNote.setText("");
                editTextVoucherExDate.setText("");
                Toast.makeText(this, "Xóa voucher thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi xóa voucher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
