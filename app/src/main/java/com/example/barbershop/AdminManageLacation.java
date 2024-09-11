package com.example.barbershop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminManageLacation extends AppCompatActivity implements LocationAdapter.OnItemClickListener{

    private RecyclerView recyclerViewBarbers;
    private LocationAdapter storeAdapter;
    private List<Location> storeList;
    private FirebaseFirestore db;
    private Button buttonAddBarber, buttonEditBarber, buttonDeleteBarber, buttonConfirmDelete;
    private EditText editTextBarberAddress, editTextBarberPhone;
    private LinearLayout layoutBarberForm;
    private String selectedStoreId;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_location);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerViewBarbers = findViewById(R.id.recyclerViewStore);
        buttonAddBarber = findViewById(R.id.buttonAddBarber);
        buttonEditBarber = findViewById(R.id.buttonEditBarber);
        buttonDeleteBarber = findViewById(R.id.buttonDeleteBarber);
        buttonConfirmDelete = findViewById(R.id.buttonConfirmDelete);
        editTextBarberAddress = findViewById(R.id.editTextBarberAddress);
        editTextBarberPhone = findViewById(R.id.editTextBarberPhone);
        layoutBarberForm = findViewById(R.id.layoutBarberForm);

        db = FirebaseFirestore.getInstance();
        storeList = new ArrayList<>();
        storeAdapter = new LocationAdapter(storeList, this);

        recyclerViewBarbers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBarbers.setAdapter(storeAdapter);

        loadStoreList();

        buttonAddBarber.setOnClickListener(v -> {
            addStore();
        });

        buttonEditBarber.setOnClickListener(v -> {
            if (selectedStoreId != null && isEditing) {
                editStore(selectedStoreId);
            }
        });

        buttonDeleteBarber.setOnClickListener(v -> {
            if (selectedStoreId != null) {
                confirmDeleteStore(selectedStoreId);
            }
        });

        buttonConfirmDelete.setOnClickListener(v -> {
            if (selectedStoreId != null) {
                deleteStore(selectedStoreId);
            }
        });
    }

    // Phương thức onItemClick từ StoreAdapter.OnItemClickListener
    @Override
    public void onItemClick(Location store) {
        // Khi một item được chọn, điền thông tin của store vào form để chỉnh sửa
        selectedStoreId = store.getId();
        editTextBarberAddress.setText(store.getAddress());
        editTextBarberPhone.setText(store.getPhoneNumber());
        isEditing = true;
    }

    private void loadStoreList() {
        CollectionReference storesRef = db.collection("stores");
        storesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    storeList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Location store = document.toObject(Location.class);
                        store.setId(document.getId());
                        storeList.add(store);
                    }
                    storeAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addStore() {
        String address = editTextBarberAddress.getText().toString();
        String phoneNumber = editTextBarberPhone.getText().toString();

        Location store = new Location(null, address, phoneNumber, null, null);
        db.collection("stores").add(store).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Lấy ID của tài liệu vừa được tạo
                String storeId = task.getResult().getId();

                // Cập nhật lại ID cho cửa hàng
                db.collection("stores").document(storeId).update("id", storeId)
                        .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                loadStoreList();  // Tải lại danh sách cửa hàng
                                clearForm();  // Xóa form
                                Toast.makeText(this, "Thêm cửa hàng thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void editStore(String storeId) {
        String address = editTextBarberAddress.getText().toString();
        String phoneNumber = editTextBarberPhone.getText().toString();
        db.collection("stores").document(storeId).update("address", address, "phoneNumber", phoneNumber)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadStoreList();
                        clearForm();
                        isEditing = false;
                        Toast.makeText(this, "Sửa cửa hàng thành công", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteStore(String storeId) {
        db.collection("stores").document(storeId).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadStoreList();
                clearForm();
                buttonConfirmDelete.setVisibility(View.GONE);
                Toast.makeText(this, "Xóa cửa hàng thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteStore(String storeId) {
        buttonConfirmDelete.setVisibility(View.VISIBLE);
    }

    private void clearForm() {
        editTextBarberAddress.setText("");
        editTextBarberPhone.setText("");
        selectedStoreId = null;
        isEditing = false;
    }
}