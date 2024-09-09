package com.example.barbershop;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.BarberViewHolder>{
    private List<Barber> barberList;
    private OnBarberClickListener onBarberClickListener;
    private Barber selectedBarber;


    public interface OnBarberClickListener {
        void onBarberClick(Barber barber);
    }

    public BarberAdapter(List<Barber> barberList, OnBarberClickListener onBarberClickListener) {
        this.barberList = barberList;
        this.onBarberClickListener = onBarberClickListener;
    }

    @NonNull
    @Override
    public BarberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barber, parent, false);
        return new BarberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberViewHolder holder, int position) {
        Barber barber = barberList.get(position);
        holder.textViewName.setText(barber.getFullname());
        holder.itemView.setOnClickListener(v -> onBarberClickListener.onBarberClick(barber));
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public Barber getSelectedBarber() {
        return selectedBarber;
    }

    public static class BarberViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public BarberViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewBarberName);
        }
    }
}
