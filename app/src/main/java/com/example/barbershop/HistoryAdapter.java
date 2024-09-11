package com.example.barbershop;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History entry = historyList.get(position);
        holder.actionTextView.setText(entry.getAction());
        holder.barberTextView.setText(entry.getBarber());
        holder.branchTextView.setText(entry.getBranch());
        holder.dateTextView.setText(entry.getDate());
        holder.timeTextView.setText(entry.getTime());
        holder.serviceTextView.setText(entry.getService());
        holder.voucherTextView.setText(entry.getVoucher().isEmpty() ? "Không có voucher" : entry.getVoucher());
        holder.addressTextView.setText(entry.getAddress());
        holder.timestampTextView.setText(entry.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView actionTextView, barberTextView, branchTextView, dateTextView, timeTextView, serviceTextView, voucherTextView, addressTextView, timestampTextView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            actionTextView = itemView.findViewById(R.id.txtViewAction);
            barberTextView = itemView.findViewById(R.id.txtViewBarber);
            branchTextView = itemView.findViewById(R.id.txtViewBranch);
            dateTextView = itemView.findViewById(R.id.txtViewDate);
            timeTextView = itemView.findViewById(R.id.txtViewTime);
            serviceTextView = itemView.findViewById(R.id.txtViewService);
            voucherTextView = itemView.findViewById(R.id.txtViewVoucher);
            addressTextView = itemView.findViewById(R.id.txtViewAddress);
            timestampTextView = itemView.findViewById(R.id.txtViewTimestamp);
        }
    }
}
