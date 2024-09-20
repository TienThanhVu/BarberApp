package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView txtUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        txtUsername = view.findViewById(R.id.txtUsername);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Thực hiện các thao tác với userId
        } else {
            // Xử lý khi không có người dùng đăng nhập, chẳng hạn chuyển đến trang đăng nhập
            startActivity(new Intent(getActivity(), SignIn.class));
            getActivity().finish();
        }

        // Lấy thông tin người dùng
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullname = documentSnapshot.getString("fullname");
                        txtUsername.setText(fullname);
                    } else {
                        Toast.makeText(getContext(), "Thông tin người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                    Toast.makeText(getContext(), "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                });
    }
}
