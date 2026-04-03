package com.example.silti;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

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
        private ImageView ImgProduct, menu;
        private TextView name, size, price, quantity;
        private Button minus, plus;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ImgProduct = itemView.findViewById(R.id.ImgProduct);
            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            menu = itemView.findViewById(R.id.menu);
        }

        void bind(table_cart item) {
            name.setText(item.getName());
            size.setText("المقاس: " + item.getSize());
            price.setText(String.format("$%.2f", item.getPrice()));
            quantity.setText(String.valueOf(item.getQuantity()));

            // تحميل الصورة
            if (item.getImage() != null && !item.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImage())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.logo)
                                .error(R.drawable.logo)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(ImgProduct);
            } else {
                ImgProduct.setImageResource(R.drawable.logo);
            }

            // ✅ أزرار التحكم في الكمية
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

            // ✅ أيقونة القائمة (ثلاث نقاط) - إظهار PopupMenu عند الضغط
            menu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), menu);
                popupMenu.getMenuInflater().inflate(R.menu.cart_item_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.action_remove) {
                            // إظهار تأكيد الحذف
                            new android.app.AlertDialog.Builder(itemView.getContext())
                                    .setTitle("حذف المنتج")
                                    .setMessage("هل أنت متأكد من حذف " + item.getName() + " من السلة؟")
                                    .setPositiveButton("نعم", (dialog, which) -> {
                                        listener.onRemoveClick(item);
                                    })
                                    .setNegativeButton("لا", null)
                                    .show();
                            return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            });

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
