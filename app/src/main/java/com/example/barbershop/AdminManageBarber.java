package com.example.barbershop;

import android.os.Bundle;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageBarber extends AppCompatActivity {

    private Spinner spinnerStores;
    private RecyclerView recyclerViewBarbers;
    private EditText editTextBarberName;
    private Button btnAddBarber;
    private Button btnDeleteBarber;
    private FirebaseFirestore db;
    private List<String> storeList = new ArrayList<>();
    private List<Barber> barberList = new ArrayList<>();
    private ArrayAdapter<String> storeAdapter;
    private BarberAdapter barberAdapter;
    private String selectedStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_barber);

        spinnerStores = findViewById(R.id.spinnerStores);
        recyclerViewBarbers = findViewById(R.id.recyclerViewBarbers);
        editTextBarberName = findViewById(R.id.editTextBarberAddress);
        btnAddBarber = findViewById(R.id.btnAddBarber);
        btnDeleteBarber = findViewById(R.id.btnDeleteBarber);

        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView for barbers
        barberAdapter = new BarberAdapter(barberList, new BarberAdapter.OnBarberClickListener() {
            @Override
            public void onBarberClick(Barber barber) {
                // Handle barber click
            }
        });
        recyclerViewBarbers.setAdapter(barberAdapter);
        recyclerViewBarbers.setLayoutManager(new LinearLayoutManager(this));

        // Load stores into spinner
        loadStores();

        // Listener for store selection
        spinnerStores.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStore = storeList.get(position);
                loadBarbersForStore(selectedStore);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

        // Add barber button click listener
        btnAddBarber.setOnClickListener(v -> {
            String barberName = editTextBarberName.getText().toString();
            addBarberToStore(barberName, selectedStore);
        });

        // Delete barber button click listener
        btnDeleteBarber.setOnClickListener(v -> {
            Barber selectedBarber = barberAdapter.getSelectedBarber();
            if (selectedBarber != null) {
                deleteBarberFromStore(selectedStore, selectedBarber);
            }
        });
    }

    private void loadStores() {
        db.collection("stores")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String storeName = document.getString("name");
                            storeList.add(storeName);
                        }
                        storeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeList);
                        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerStores.setAdapter(storeAdapter);
                    } else {
                        // Handle error
                    }
                });
    }

    private void loadBarbersForStore(String storeName) {
        db.collection("stores").whereEqualTo("name", storeName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        barberList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            for (DocumentReference barberRef : (List<DocumentReference>) document.get("barbers")) {
                                barberRef.get().addOnSuccessListener(barberDoc -> {
                                    Barber barber = barberDoc.toObject(Barber.class);
                                    barberList.add(barber);
                                    barberAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                    } else {
                        // Handle error
                    }
                });
    }

    private void addBarberToStore(String barberName, String storeName) {
        db.collection("barbers").whereEqualTo("name", barberName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Barber does not exist
                            // Handle barber not found case
                        } else {
                            Barber barber = task.getResult().getDocuments().get(0).toObject(Barber.class);
                            db.collection("stores").whereEqualTo("name", storeName)
                                    .get()
                                    .addOnCompleteListener(storeTask -> {
                                        if (storeTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : storeTask.getResult()) {
                                                DocumentReference storeRef = document.getReference();
                                                storeRef.collection("barbers").document(barber.getId()).set(barber)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(this, "Thêm barber thành công", Toast.LENGTH_SHORT).show();
                                                            // Barber added successfully
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Handle error
                                                        });
                                            }
                                        } else {
                                            // Handle error
                                        }
                                    });
                        }
                    } else {
                        // Handle error
                    }
                });
    }

    private void deleteBarberFromStore(String storeName, Barber barber) {
        db.collection("stores").whereEqualTo("name", storeName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference storeRef = document.getReference();
                            storeRef.collection("barbers").document(barber.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Barber deleted successfully
                                        Toast.makeText(this, "Xóa barber thành công", Toast.LENGTH_SHORT).show();
                                        loadBarbersForStore(storeName); // Refresh the list
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle error
                                    });
                        }
                    } else {
                        // Handle error
                    }
                });
    }
}
