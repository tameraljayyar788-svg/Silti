package com.example.silti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<table_product> favoriteProducts;
    private OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onProductClick(table_product product);
        void onRemoveFromFavorites(table_product product);
    }

    public FavoritesAdapter(List<table_product> products, OnFavoriteClickListener listener) {
        this.favoriteProducts = products != null ? products : new ArrayList<>();
        this.listener = listener;
    }

    public void updateProducts(List<table_product> newProducts) {
        this.favoriteProducts = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_background_like, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        table_product product = favoriteProducts.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return favoriteProducts.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView ImgProduct;
        private TextView name, price;
        private ImageButton like_product; // في هذا التصميم الزر أحمر
        private TextView tvCurrency;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ImgProduct = itemView.findViewById(R.id.ImgProduct);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            like_product = itemView.findViewById(R.id.like_product); // هذا أحمر في هذا التصميم
        }

        void bind(table_product product) {
            name.setText(product.getName());
            price.setText(String.valueOf((int)product.getPrice()));

            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImage())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(ImgProduct);
            } else {
                ImgProduct.setImageResource(R.drawable.logo);
            }

            // في هذا التصميم الزر أحمر بشكل دائم
            like_product.setImageResource(R.drawable.like_red);

            itemView.setOnClickListener(v -> listener.onProductClick(product));

            like_product.setOnClickListener(v -> listener.onRemoveFromFavorites(product));
        }
    }
}
