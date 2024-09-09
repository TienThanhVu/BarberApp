package com.example.barbershop;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminManageUser extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore firestore;
    private User selectedUser;
    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Spinner spinnerRole;

    private List<String> roleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_user);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();

        // Khởi tạo RecyclerView và Adapter
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter((ArrayList<User>) userList, this::onUserSelected); // Khởi tạo UserAdapter với ArrayList
        recyclerViewUsers.setAdapter(userAdapter);

        // Khởi tạo EditTexts và Spinner
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerRole = findViewById(R.id.spinnerRole);

        // Gán OnItemClickListener cho UserAdapter
        userAdapter.setOnItemClickListener(user -> {
            selectedUser = user;
            Toast.makeText(AdminManageUser.this, "Chọn user: " + user.getEmail(), Toast.LENGTH_SHORT).show();
        });

        // Khởi tạo Button Add
        Button buttonAddUser = findViewById(R.id.buttonAddUser);
        buttonAddUser.setOnClickListener(v -> {
            addUser();
        });

        // Khởi tạo Button Delete
        Button buttonDeleteUser = findViewById(R.id.buttonDeleteUser);
        buttonDeleteUser.setOnClickListener(v -> {
            if (selectedUser != null) {
                deleteUser(selectedUser);
            } else {
                Toast.makeText(AdminManageUser.this, "Hãy chọn user để xóa", Toast.LENGTH_SHORT).show();
            }
        });


        // Khởi tạo Button Update
        Button buttonUpdateUser = findViewById(R.id.buttonEditUser);
        buttonUpdateUser.setOnClickListener(v -> {
            if (selectedUser != null) {
                updateUser(selectedUser);
            } else {
                Toast.makeText(AdminManageUser.this, "Hãy chọn user để cập nhật", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập danh sách vai trò cho Spinner
        setupRoleSpinner();

        // Lấy dữ liệu từ Firestore
        loadUsersFromFirestore();
    }

    private void onUserSelected(User user) {
        selectedUser = user;
        editTextUserName.setText(user.getFullname()); // Hiển thị tên người dùng
        editTextEmail.setText(user.getEmail());       // Hiển thị email người dùng
        editTextPassword = findViewById(R.id.editTextPassword);// Khởi tạo editTextPassword

        // Cập nhật Spinner với vai trò của người dùng
        if (user.getRole() != null) {
            int position = roleList.indexOf(user.getRole());
            if (position >= 0) {
                spinnerRole.setSelection(position);
            }
        }
    }

    private void setupRoleSpinner() {
        //các vai trò có sẵn
        roleList.add("admin");
        roleList.add("user");
        roleList.add("barber");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private void loadUsersFromFirestore() {
        firestore.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    user.setId(document.getId());
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    private void deleteUser(User user) {
        firestore.collection("users")
                .document(user.getId()) // Thay `getId()` bằng phương thức lấy ID của user trong model User
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminManageUser.this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                    loadUsersFromFirestore(); // Tải lại danh sách user sau khi xóa
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminManageUser.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUser(User user) {
        String updatedName = editTextUserName.getText().toString();
        String updatedEmail = editTextEmail.getText().toString();
        String updatedRole = spinnerRole.getSelectedItem().toString();

        // Tạo một Map để lưu các trường cần cập nhật
        Map<String, Object> updates = new HashMap<>();

        // Thêm vào Map các trường có giá trị không rỗng
        if (!updatedName.isEmpty()) {
            updates.put("fullname", updatedName);
        }
        if (!updatedEmail.isEmpty()) {
            updates.put("email", updatedEmail);
        }
        if (!updatedRole.isEmpty()) {
            updates.put("role", updatedRole);
        }

        // Kiểm tra dữ liệu đầu vào
        if (updates.isEmpty()) {
            Toast.makeText(AdminManageUser.this, "Không có thông tin nào để cập nhật", Toast.LENGTH_SHORT).show();
            return;
        }


        // Cập nhật thông tin người dùng trên Firestore
        firestore.collection("users").document(user.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminManageUser.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    // Xóa thông tin người dùng khỏi các trường
                    clearUserInfo();
                    // Tải lại danh sách người dùng
                    loadUsersFromFirestore();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminManageUser.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void addUser() {
        String name = editTextUserName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        // Kiểm tra dữ liệu đầu vào
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(AdminManageUser.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

// Tạo người dùng mới với Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Lấy user ID từ Firebase Authentication
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Tạo đối tượng người dùng
                            User newUser = new User();
                            newUser.setFullname(name);
                            newUser.setEmail(email);
                            newUser.setRole(role);

                            // Thêm người dùng vào Firestore với ID từ Firebase Authentication
                            firestore.collection("users").document(userId).set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AdminManageUser.this, "Thêm người dùng thành công", Toast.LENGTH_SHORT).show();
                                        clearUserInfo(); // Xóa thông tin người dùng khỏi các trường
                                        loadUsersFromFirestore(); // Tải lại danh sách người dùng
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AdminManageUser.this, "Thêm người dùng vào Firestore thất bại", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Thông báo nếu tạo user thất bại
                        Toast.makeText(AdminManageUser.this, "Tạo người dùng thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Phương thức clearUserInfo không thay đổi
    private void clearUserInfo() {
        editTextUserName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        spinnerRole.setSelection(0); // Chọn giá trị mặc định
        selectedUser = null; // Đặt selectedUser thành null
    }
}