package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PantryViewHolder holder, int position) {

        PantryItem item = pantryItems.get(position);

        holder.txtIngredientName.setText(item.getName());
        holder.txtQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

        SharedPreferences preferences = holder.itemView.getContext()
                .getSharedPreferences("SmartPantryPreferences", Context.MODE_PRIVATE);

        boolean showExpiryDates = preferences.getBoolean("showExpiryDates", true);

        if (showExpiryDates) {

            holder.txtExpiryDate.setVisibility(View.VISIBLE);

            if (item.getExpiryDate() == null || item.getExpiryDate().trim().isEmpty()) {

                holder.txtExpiryDate.setText("No expiry date");
                holder.txtExpiryDate.setTextColor(0xFF757575);

            } else {

                String expiryDate = item.getExpiryDate();
                holder.txtExpiryDate.setText("Expires: " + expiryDate);
                holder.txtExpiryDate.setTextColor(0xFF757575);

                // Get the selected warning period.
                int warningOption = preferences.getInt("expiryWarning", 1);
                int[] warningDays = {1, 3, 5, 7};
                int selectedDays = warningDays[Math.max(0, Math.min(warningOption, 3))];

                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    format.setLenient(false);

                    Date expiry = format.parse(expiryDate);
                    Date today = format.parse(format.format(new Date()));

                    if (expiry != null && today != null) {

                        long daysRemaining = TimeUnit.MILLISECONDS.toDays(
                                expiry.getTime() - today.getTime()
                        );

                        if (daysRemaining < 0) {
                            holder.txtExpiryDate.setText("Expired: " + expiryDate);
                            holder.txtExpiryDate.setTextColor(0xFFC62828);

                        } else if (daysRemaining <= selectedDays) {
                            holder.txtExpiryDate.setText("Expiring soon: " + expiryDate);
                            holder.txtExpiryDate.setTextColor(0xFFE65100);
                        }
                    }

                } catch (Exception e) {
                    // Keep the normal expiry date if parsing fails.
                }
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
