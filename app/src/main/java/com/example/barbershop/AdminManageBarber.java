package com.example.barbershop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageBarber extends AppCompatActivity{

    private RecyclerView recyclerViewBarbers;
    private BarberAdapter barberAdapter;
    private List<Barber> barberList;
    private FirebaseFirestore db;
    private Button buttonAddBarber, buttonDeleteBarber;;
    private EditText editTextBarberName, editTextBarberPhone;
    private Spinner spinnerStore;
    private String selectedStoreId;
    private Barber selectedBarber;
    private List<String> storeIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_barber);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerViewBarbers = findViewById(R.id.recyclerViewBarbers);
        buttonAddBarber = findViewById(R.id.buttonAddBarber);
        buttonDeleteBarber = findViewById(R.id.buttonDeleteBarber);
        editTextBarberName = findViewById(R.id.editTextBarberName);
        editTextBarberPhone = findViewById(R.id.editTextBarberPhone);
        spinnerStore = findViewById(R.id.spinnerStore);

        db = FirebaseFirestore.getInstance();
        barberList = new ArrayList<>();

        // Khởi tạo barberAdapter với OnItemClickListener
        barberAdapter = new BarberAdapter(barberList, barber -> {
            selectedBarber = barber;
            // Hiển thị thông tin của barber đã chọn
            editTextBarberName.setText(selectedBarber.getName());
            editTextBarberPhone.setText(selectedBarber.getPhoneNumber());
            // Cập nhật spinner để chọn cửa hàng tương ứng với barber
            if (selectedBarber.getStoreId() != null) {
                int storeIndex = getStoreIndex(selectedBarber.getStoreId());
                if (storeIndex != -1) {
                    spinnerStore.setSelection(storeIndex); // Cập nhật spinner
                }
            }
        });

        recyclerViewBarbers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBarbers.setAdapter(barberAdapter); // Gán adapter cho RecyclerView

        loadStoreList(); // Load các store vào Spinner
        loadBarberList(); // Load danh sách barber

        buttonAddBarber.setOnClickListener(v -> addBarber());
        buttonDeleteBarber.setOnClickListener(v -> {
            if (selectedBarber != null) {
                deleteBarber(selectedBarber);
            } else {
                Toast.makeText(this, "Vui lòng chọn thợ cắt tóc để xóa khỏi cửa hàng", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void loadStoreList() {
        CollectionReference storesRef = db.collection("stores");
        storesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> storeNames = new ArrayList<>();
                storeIds = new ArrayList<>(); // Đảm bảo lưu danh sách ID cửa hàng

                for (QueryDocumentSnapshot document : task.getResult()) {
                    storeNames.add(document.getString("address"));
                    storeIds.add(document.getId()); // Lưu ID cửa hàng
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStore.setAdapter(adapter);

                // Spinner đã được cập nhật
                //storeIds đã được khởi tạo đầy đủ
                if (selectedBarber != null && selectedBarber.getStoreId() != null) {
                    int storeIndex = getStoreIndex(selectedBarber.getStoreId());
                    if (storeIndex != -1) {
                        spinnerStore.setSelection(storeIndex);
                    }
                }

                spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedStoreId = storeIds.get(position); // Lưu ID của cửa hàng được chọn
                        loadBarberList(); // Tải danh sách barber theo store đã chọn
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedStoreId = null;
                    }
                });
            }
        });
    }


    private void loadBarberList() {
        CollectionReference barbersRef = db.collection("barbers");
        barbersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    barberList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Barber barber = document.toObject(Barber.class);
                        barber.setId(document.getId());
                        barberList.add(barber);
                    }
                    if (barberAdapter != null) {
                        barberAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void addBarber() {
        String name = editTextBarberName.getText().toString();
        String phoneNumber = editTextBarberPhone.getText().toString();
        String storeId = selectedStoreId; // Lấy ID store đã chọn từ Spinner

        if (storeId == null) {
            Toast.makeText(this, "Vui lòng chọn cửa hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if(name == null &&  phoneNumber == null){
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Barber barber = new Barber(null, name, phoneNumber, storeId);
        db.collection("barbers").add(barber).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadBarberList();
                // Làm sạch các EditText sau khi thêm barber thành công
                editTextBarberName.setText("");
                editTextBarberPhone.setText("");
                Toast.makeText(this, "Thêm barber thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi thêm barber", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteBarber(Barber barber) {
        String barberId = barber.getId(); // ID của barber cần xóa

        db.collection("barbers").document(barberId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadBarberList();
                // Làm sạch các EditText sau khi thêm barber thành công
                editTextBarberName.setText("");
                editTextBarberPhone.setText("");
                Toast.makeText(this, "Xóa barber thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi xóa barber", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Phương thức này sẽ tìm chỉ số của cửa hàng trong danh sách các cửa hàng dựa trên storeId
    private int getStoreIndex(String storeId) {
        if (storeIds != null) {
            for (int i = 0; i < storeIds.size(); i++) {
                if (storeIds.get(i).equals(storeId)) {
                    return i;
                }
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }
}