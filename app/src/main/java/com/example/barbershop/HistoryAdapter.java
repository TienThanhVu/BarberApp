package com.example.barbershop;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.textAction.setText(history.getAction());
        holder.textDetails.setText(history.getDetails());
        holder.textTimestamp.setText(history.getFormattedTimestamp()); // Hiển thị định dạng ngày giờ
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textAction;
        TextView textDetails;
        TextView textTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textAction = itemView.findViewById(R.id.textAction);
            textDetails = itemView.findViewById(R.id.textDetails);
            textTimestamp = itemView.findViewById(R.id.textTimestamp);
        }
    }
}