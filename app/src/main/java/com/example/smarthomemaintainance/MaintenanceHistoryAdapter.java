package com.example.smarthomemaintainance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MaintenanceHistoryAdapter extends RecyclerView.Adapter<MaintenanceHistoryAdapter.ViewHolder> {

    private List<MaintenanceHistoryEntity> historyList;

    public MaintenanceHistoryAdapter(List<MaintenanceHistoryEntity> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maintenance_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaintenanceHistoryEntity item = historyList.get(position);
        holder.tvDate.setText(item.getDate());
        holder.tvType.setText(item.getType());
        holder.tvNotes.setText(item.getNotes() != null && !item.getNotes().isEmpty() ? item.getNotes() : "No notes provided");
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvType, tvNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvNotes = itemView.findViewById(R.id.tvNotes);
        }
    }
}
