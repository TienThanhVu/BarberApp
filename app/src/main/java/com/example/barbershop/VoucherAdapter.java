package com.example.barbershop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private List<Voucher> voucherList;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Voucher voucher);
    }

    public VoucherAdapter(List<Voucher> voucherList, OnItemClickListener onItemClickListener) {
        this.voucherList = voucherList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.bind(voucher, onItemClickListener);

        // Cập nhật màu nền dựa trên việc chọn item
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
            notifyItemChanged(selectedPosition); // Cập nhật item hiện tại

            // Thông báo cho listener
            if (onItemClickListener != null) {
                Voucher selectedVoucher = voucherList.get(selectedPosition);
                onItemClickListener.onItemClick(selectedVoucher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCode;
        private TextView textViewDiscount;
        private TextView textViewNote;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCode = itemView.findViewById(R.id.textViewCode);
            textViewDiscount = itemView.findViewById(R.id.textViewPercent);
            textViewNote = itemView.findViewById(R.id.textViewNote);
        }

        public void bind(Voucher voucher, OnItemClickListener onItemClickListener) {
            textViewCode.setText(voucher.getCode());
            textViewDiscount.setText(String.valueOf(voucher.getPercent()));
            textViewNote.setText(voucher.getNote());
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(voucher));
        }
    }
}

