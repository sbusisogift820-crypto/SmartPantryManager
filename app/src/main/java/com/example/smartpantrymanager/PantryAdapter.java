package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Context context;
    private List<PantryItem> pantryList;
    private List<PantryItem> fullList; // Master copy for live filtering
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int id);
        void onItemClick(PantryItem item);
    }

    public PantryAdapter(Context context, List<PantryItem> pantryList, OnItemClickListener listener) {
        this.context = context;
        this.pantryList = pantryList;
        this.fullList = new ArrayList<>(pantryList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = pantryList.get(position);
        if (item.getName() != null && !item.getName().isEmpty()) {
            holder.tvName.setText(item.getName().substring(0, 1).toUpperCase() + item.getName().substring(1));
        }
        holder.tvQty.setText("Qty: " + item.getQuantity() + " " + item.getUnit());

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item.getId()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return pantryList.size();
    }

    // Refresh master dataset on reload
    public void updateData(List<PantryItem> newList) {
        this.pantryList = new ArrayList<>(newList);
        this.fullList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // Live search & chip filter method
    public void filter(String query, int chipId) {
        List<PantryItem> filteredList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        // Set baseline threshold to 3 days from today
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.add(Calendar.DAY_OF_YEAR, 3);
        Date threeDaysFromNow = targetCalendar.getTime();

        for (PantryItem item : fullList) {
            boolean matchesSearch = item.getName().toLowerCase().contains(query.toLowerCase().trim());
            boolean matchesChip = true;

            if (chipId == R.id.chipExpiring) {
                String expiryStr = item.getExpiryDate();
                if (expiryStr != null && !expiryStr.trim().isEmpty()) {
                    try {
                        Date expiryDate = sdf.parse(expiryStr.trim());
                        // Includes items expiring within the next 3 days or already expired
                        matchesChip = expiryDate != null && !expiryDate.after(threeDaysFromNow);
                    } catch (ParseException e) {
                        matchesChip = false; // Exclude items with invalid date format
                    }
                } else {
                    matchesChip = false;
                }
            } else if (chipId == R.id.chipLowStock) {
                matchesChip = item.getQuantity() < 2.0;
            }

            if (matchesSearch && matchesChip) {
                filteredList.add(item);
            }
        }

        this.pantryList = filteredList;
        notifyDataSetChanged();
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty;
        ImageButton btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQty = itemView.findViewById(R.id.tvQuantity);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}