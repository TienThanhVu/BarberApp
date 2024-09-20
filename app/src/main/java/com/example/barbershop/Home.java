package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class Home extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView txtUsername;
    private String username;
    private AutoCompleteTextView searchEditText; // Đảm bảo sử dụng đúng kiểu dữ liệu
    private ImageView searchImageView;
    private List<String> suggestions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.home, container, false);

        // Ẩn ActionBar (tùy chọn)
        if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        txtUsername = rootView.findViewById(R.id.txtUsername);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username = document.getString("fullName");
                        txtUsername.setText(username);
                        ImageView imgList = rootView.findViewById(R.id.imgList);
                        imgList.setOnClickListener(v -> showPopupMenu(v));
                        // Gọi setupInteraction khi người dùng đã đăng nhập (cho phép thực hiện các thao tác)
                        setupInteraction(rootView, true);
                    } else {
                        Toast.makeText(getActivity(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Thiết lập các phần tử tương tác với trạng thái chưa đăng nhập
            setupInteraction(rootView, false);
        }

        // Tìm kiếm và xử lý sự kiện tìm kiếm
        searchEditText = rootView.findViewById(R.id.searchEditText); // Đảm bảo sử dụng đúng ID của AutoCompleteTextView
        searchImageView = rootView.findViewById(R.id.imgSearch); // Sử dụng đúng ID của ImageView

        // Cài đặt gợi ý tìm kiếm
        suggestions = Arrays.asList("Đặt lịch", "Xem giỏ hàng", "Xem store", "Tìm tên barber");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, suggestions);
        searchEditText.setAdapter(adapter); // Đảm bảo gọi setAdapter trên AutoCompleteTextView

        searchImageView.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                handleSearch(query);
            }
        });

        return rootView;
    }

    private void setupInteraction(View rootView, boolean isLoggedIn) {
        LinearLayout calendarLayout = rootView.findViewById(R.id.calendar);
        Button buttonBook30s = rootView.findViewById(R.id.buttonBook30s);
        LinearLayout cartLayout = rootView.findViewById(R.id.cart);
        LinearLayout historyLayout = rootView.findViewById(R.id.history);

        if (isLoggedIn) {
            // Nếu đã đăng nhập, thực hiện các thao tác như đặt lịch
            calendarLayout.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), UserBookBarber.class);
                startActivity(intent);
            });

            buttonBook30s.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), UserBookBarber.class);
                startActivity(intent);
            });

            cartLayout.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), UserCart.class);
                startActivity(intent);
            });

            historyLayout.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), UserHistory.class);
                startActivity(intent);
            });

        } else {
            // Nếu chưa đăng nhập, yêu cầu đăng nhập trước
            View.OnClickListener onClickListener = v -> {
                showLoginAlertDialog();
            };

            calendarLayout.setOnClickListener(onClickListener);
            buttonBook30s.setOnClickListener(onClickListener);
            cartLayout.setOnClickListener(onClickListener);
            historyLayout.setOnClickListener(onClickListener);
        }
    }

    private void showLoginAlertDialog() {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Cần Đăng Nhập")
                .setMessage("Bạn cần đăng nhập để sử dụng các tính năng này. Bạn có muốn đăng nhập không?")
                .setPositiveButton("Đăng Nhập", (dialog, which) -> {
                    // Chuyển đến SignInActivity
                    Intent intent = new Intent(requireActivity(), SignIn.class);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popupMenu.getMenu());

        MenuItem usernameItem = popupMenu.getMenu().findItem(R.id.action_userName);
        if (usernameItem != null && username != null) {
            usernameItem.setTitle(username);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logOut) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), SignIn.class));
                getActivity().finish();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void handleSearch(String query) {
        // Xử lý tìm kiếm dựa trên từ khóa
        if (query.toLowerCase().contains("đặt lịch")) {
            // Mở activity hoặc fragment cho việc đặt lịch
            Intent intent = new Intent(getActivity(), UserBookBarber.class);
            startActivity(intent);
        } else if (query.toLowerCase().contains("xem giỏ hàng")) {
            // Mở activity hoặc fragment cho việc xem giỏ hàng
            Intent intent = new Intent(getActivity(), UserCart.class);
            startActivity(intent);
//        } else if (query.toLowerCase().contains("xem store")) {
//            // Mở activity hoặc fragment cho việc xem store
//            Intent intent = new Intent(getActivity(), Store.class);
//            startActivity(intent);
        } else if (query.toLowerCase().contains("tìm tên barber")) {
            // Mở activity hoặc fragment cho việc tìm tên barber
            Intent intent = new Intent(getActivity(), Barber.class);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Không tìm thấy kết quả phù hợp", Toast.LENGTH_SHORT).show();
        }
    }
}