package com.example.barbershop;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private final List<Store> barberShops;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = -1;

    public StoreAdapter(List<Store> barberShops, OnItemClickListener onItemClickListener) {
        this.barberShops = barberShops;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Store barberShop = barberShops.get(position);
        holder.textShopAddress.setText(barberShop.getAddress());
        holder.textPhoneNumber.setText(barberShop.getPhoneNumber());

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
            notifyItemChanged(oldPosition); // Cập nhật lại item trước đó
            notifyItemChanged(selectedPosition); // Cập nhật lại item được chọn

            // Gọi sự kiện click
            if (onItemClickListener != null) {
                Store selectedStore = barberShops.get(selectedPosition);
                onItemClickListener.onItemClick(selectedStore);
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberShops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageShop;
        TextView textShopAddress;
        TextView textPhoneNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textShopAddress = itemView.findViewById(R.id.textShopAddress);
            textPhoneNumber = itemView.findViewById(R.id.textPhoneNumber);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Store store);
    }
}