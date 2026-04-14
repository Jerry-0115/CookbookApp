package com.group4.cookbook.ui.collections;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.cookbook.R;
import com.group4.cookbook.data.models.ShoppingList;
import com.group4.cookbook.data.models.ShoppingListItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {
    private List<ShoppingList> shoppingLists;
    private OnShoppingListClickListener listener;

    public interface OnShoppingListClickListener {
        void onShoppingListClick(ShoppingList shoppingList);
    }

    public ShoppingListAdapter(List<ShoppingList> shoppingLists, OnShoppingListClickListener listener) {
        this.shoppingLists = shoppingLists;
        this.listener = listener;
    }

    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        ShoppingList shoppingList = shoppingLists.get(position);
        holder.bind(shoppingList);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShoppingListClick(shoppingList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingLists != null ? shoppingLists.size() : 0;
    }

    static class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        TextView textListName;
        TextView textItemCount;
        TextView textDate;
        ImageView imageListIcon;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
            textListName = itemView.findViewById(R.id.textListName);
            textItemCount = itemView.findViewById(R.id.textItemCount);
            textDate = itemView.findViewById(R.id.textDate);
            imageListIcon = itemView.findViewById(R.id.imageListIcon);
        }

        public void bind(ShoppingList shoppingList) {
            textListName.setText(shoppingList.getName());

            // Calculate item count
            int totalItems = 0;
            int purchasedItems = 0;
            if (shoppingList.getItems() != null) {
                totalItems = shoppingList.getItems().size();
                for (ShoppingListItem item : shoppingList.getItems()) {
                    if (item.isPurchased()) {
                        purchasedItems++;
                    }
                }
            }
            textItemCount.setText(purchasedItems + "/" + totalItems + " items");

            // Format date
            if (shoppingList.getUpdatedAt() != null) {
                Date date = shoppingList.getUpdatedAt().toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                textDate.setText("Updated: " + sdf.format(date));
            } else {
                textDate.setText("");
            }

            // Set icon based on completion
            if (purchasedItems == totalItems && totalItems > 0) {
                imageListIcon.setImageResource(R.drawable.ic_check_circle);
            } else {
                imageListIcon.setImageResource(R.drawable.ic_shopping_cart);
            }
        }
    }
}