package com.example.silti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class TalabatyAdapter extends RecyclerView.Adapter<TalabatyAdapter.TalabatyViewHolder> {

    private List<order_item> orderItems;
    private OnOrderItemClickListener listener;

    public interface OnOrderItemClickListener {
        void onReorderClick(order_item item);
        void onItemClick(order_item item);
    }

    public TalabatyAdapter(List<order_item> items, OnOrderItemClickListener listener) {
        this.orderItems = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    public void updateItems(List<order_item> newItems) {
        this.orderItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TalabatyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_talabaty, parent, false);
        return new TalabatyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TalabatyViewHolder holder, int position) {
        order_item item = orderItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    class TalabatyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ImgProduct, reorder_product;
        private TextView price, name;
        private TextView tvCurrency;

        TalabatyViewHolder(@NonNull View itemView) {
            super(itemView);
            ImgProduct = itemView.findViewById(R.id.ImgProduct);
            reorder_product = itemView.findViewById(R.id.reorder_product);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name); // لاحظ: في الـ XML ليس له id لكننا نستخدمه
        }

        void bind(order_item item) {
            // لا يوجد TextView للاسم في هذا التصميم، يمكن إضافته أو استخدام مكان آخر
            if (name != null) {
                name.setText(item.getName());
            }

            price.setText(String.valueOf((int)item.getPrice()));

            if (item.getImage() != null && !item.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImage())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(ImgProduct);
            } else {
                ImgProduct.setImageResource(R.drawable.logo);
            }

            reorder_product.setOnClickListener(v -> listener.onReorderClick(item));
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
