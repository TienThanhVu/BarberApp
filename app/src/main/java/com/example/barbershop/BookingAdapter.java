package com.example.barbershop;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookingList;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = -1;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    public interface OnItemClickListener {
        void onItemClick(Booking booking);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.txtBranch.setText("Chi nhánh: " + booking.getAddress());
        holder.txtBarber.setText("Thợ cắt tóc: " + booking.getBarberName());
        holder.txtDate.setText("Ngày: " + booking.getDate());
        holder.txtTime.setText("Giờ: " + booking.getTime());
        holder.txtService.setText("Dịch vụ: " + booking.getService());
        holder.txtUserName.setText("Người dùng: " + booking.getUserName());

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged(); // Cập nhật giao diện để hiển thị trạng thái chọn
        });

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.cloudwhite));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(bookingList.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView txtBranch, txtBarber, txtDate, txtTime, txtService, txtUserName;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBranch = itemView.findViewById(R.id.txtBranch);
            txtBarber = itemView.findViewById(R.id.txtBarber);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtService = itemView.findViewById(R.id.txtService);
            txtUserName = itemView.findViewById(R.id.txtUserName);
        }
    }
}

