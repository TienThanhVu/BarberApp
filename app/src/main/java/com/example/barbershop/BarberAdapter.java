package com.example.barbershop;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.ViewHolder>{
    private final List<Barber> barberList;
    private final OnItemClickListener onItemClickListener;
    private int selectedPosition = -1; // Vị trí của item đã chọn

    public BarberAdapter(List<Barber> barberList, OnItemClickListener onItemClickListener) {
        this.barberList = barberList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_barber, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barber barber = barberList.get(position);
        holder.textViewBarberName.setText(barber.getName());
        holder.textViewBarberPhone.setText(barber.getPhoneNumber());

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
            notifyItemChanged(oldPosition); // Cập nhật item trước đó
            notifyItemChanged(selectedPosition); // Cập nhật item được chọn

            // Gọi sự kiện click
            if (onItemClickListener != null) {
                Barber selectedBarber = barberList.get(selectedPosition);
                onItemClickListener.onItemClick(selectedBarber);
            }
        });
    }


    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBarberName;
        TextView textViewBarberPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBarberName = itemView.findViewById(R.id.textViewBarberName);
            textViewBarberPhone = itemView.findViewById(R.id.textViewBarberPhone);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Barber barber);
    }
}