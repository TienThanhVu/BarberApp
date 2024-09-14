package com.example.barbershop;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class UserHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<History> historyList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_history);

        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        String bookingId = getIntent().getStringExtra("id");

        loadHistory(bookingId);
    }

    private void loadHistory(String bookingId) {
        Log.d("UserHistory", "Loading history for booking ID: " + bookingId);

        // Truy cập vào stores, sau đó là documents của bookings
        db.collection("stores").document(bookingId).collection("cancellation_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("UserHistory", "No history found for booking ID: " + bookingId);
                    } else {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            History entry = document.toObject(History.class);
                            if (entry != null) {
                                historyList.add(entry);
                            }
                        }
                        Log.d("UserHistory", "History loaded successfully.");
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("UserHistory", "Error getting history", e));
    }
}
