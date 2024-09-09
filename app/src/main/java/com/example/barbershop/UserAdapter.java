package com.example.barbershop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> userList;
    private int selectedPosition = -1;
    private OnItemClickListener onItemClickListener;


    public UserAdapter(ArrayList<User> userList,  OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewUserInfo.setText(user.getEmail() + " - " + user.getRole());

        // Kiểm tra vị trí được chọn
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.cloudwhite));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);// Cập nhật lại item trước đó
            notifyItemChanged(selectedPosition);// Cập nhật lại item được chọn

            // Gọi sự kiện click
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(userList.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUser;
        TextView textViewUserInfo;
        private TextView userNameTextView;
        private TextView userEmailTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUser = itemView.findViewById(R.id.imageUser);
            textViewUserInfo = itemView.findViewById(R.id.textUserInfo);
        }

        public void bind(final User user, final OnItemClickListener listener) {
            userNameTextView.setText(user.getFullname());
            userEmailTextView.setText(user.getEmail());
            itemView.setOnClickListener(v -> listener.onItemClick(user));
        }
    }
}
