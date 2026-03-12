package com.example.silti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<table_cart> cartItems;
    private OnCartItemClickListener listener;

    public interface OnCartItemClickListener {
        void onQuantityChanged(table_cart item, int newQuantity);
        void onRemoveClick(table_cart item);
        void onItemClick(table_cart item);
    }

    public CartAdapter(List<table_cart> items, OnCartItemClickListener listener) {
        this.cartItems = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    public void updateItems(List<table_cart> newItems) {
        this.cartItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_product_incart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        table_cart item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ImgProduct, menu; // menu يستخدم كزر حذف
        private TextView name, size, price, quantity;
        private Button minus, plus;
        private TextView tvCurrency;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ImgProduct = itemView.findViewById(R.id.ImgProduct);
            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            menu = itemView.findViewById(R.id.menu); // زر الحذف
        }

        void bind(table_cart item) {
            name.setText(item.getName());
            size.setText(item.getSize());
            price.setText(String.valueOf((int)item.getPrice()));
            quantity.setText(String.valueOf(item.getQuantity()));

            if (item.getImage() != null && !item.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImage())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(ImgProduct);
            } else {
                ImgProduct.setImageResource(R.drawable.logo);
            }

            // Quantity controls
            minus.setOnClickListener(v -> {
                int newQty = item.getQuantity() - 1;
                if (newQty >= 1) {
                    quantity.setText(String.valueOf(newQty));
                    listener.onQuantityChanged(item, newQty);
                }
            });

            plus.setOnClickListener(v -> {
                int newQty = item.getQuantity() + 1;
                quantity.setText(String.valueOf(newQty));
                listener.onQuantityChanged(item, newQty);
            });

            menu.setOnClickListener(v -> listener.onRemoveClick(item));

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
