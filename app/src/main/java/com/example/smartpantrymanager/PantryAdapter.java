package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<PantryItem> pantryItems;
    private final OnPantryItemClickListener listener;

    public interface OnPantryItemClickListener {
        void onEditClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    public PantryAdapter(List<PantryItem> pantryItems, OnPantryItemClickListener listener) {

        this.pantryItems = pantryItems;
        this.listener = listener;
    }


    @Override
    public PantryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PantryViewHolder holder, int position) {

        PantryItem item = pantryItems.get(position);

        holder.txtIngredientName.setText(item.getName());

        holder.txtQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

        // Load the expiry-date display setting
        SharedPreferences preferences =
                holder.itemView.getContext().getSharedPreferences("SmartPantryPreferences", Context.MODE_PRIVATE);

        boolean showExpiryDates = preferences.getBoolean("showExpiryDates", true);

        if (showExpiryDates) {

            holder.txtExpiryDate.setVisibility(View.VISIBLE);

            if (item.getExpiryDate() == null || item.getExpiryDate().trim().isEmpty()) {

                holder.txtExpiryDate.setText("No expiry date");

            } else {

                holder.txtExpiryDate.setText("Expires: " + item.getExpiryDate());
            }

        } else {

            holder.txtExpiryDate.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(item));

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    private String formatQuantity(double quantity) {

        if (quantity == (long) quantity) {
            return String.valueOf((long) quantity);
        }

        return String.valueOf(quantity);
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView txtIngredientName;
        TextView txtQuantity;
        TextView txtExpiryDate;

        Button btnEdit;
        Button btnDelete;

        public PantryViewHolder(View itemView) {
            super(itemView);

            txtIngredientName = itemView.findViewById(R.id.txtIngredientName);

            txtQuantity = itemView.findViewById(R.id.txtQuantity);

            txtExpiryDate = itemView.findViewById(R.id.txtExpiryDate);

            btnEdit = itemView.findViewById(R.id.btnEdit);

            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}