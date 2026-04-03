package com.example.silti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<table_product> favoriteProducts;
    private List<table_faivorate> favorites = new ArrayList<>();
    private OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onProductClick(table_product product);
        void onRemoveFromFavorites(table_product product);
        void onAddToCartClick(table_product product);
    }

    public FavoritesAdapter(List<table_product> products, OnFavoriteClickListener listener) {
        this.favoriteProducts = products != null ? products : new ArrayList<>();
        this.listener = listener;
    }

    public void updateProducts(List<table_product> newProducts) {
        this.favoriteProducts = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateFavorites(List<table_faivorate> newFavorites) {
        this.favorites = newFavorites != null ? newFavorites : new ArrayList<>();
        notifyDataSetChanged();
    }

    private boolean isFavorite(long productId) {
        for (table_faivorate fav : favorites) {
            if (fav.getProductId() == productId) return true;
        }
        return false;
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
        private ImageButton like_product, addToCart;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ImgProduct = itemView.findViewById(R.id.ImgProduct);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            like_product = itemView.findViewById(R.id.like_product);
            addToCart = itemView.findViewById(R.id.addToCart);
        }

        void bind(table_product product) {
            name.setText(product.getName());
            price.setText(String.format("$%.2f", product.getPrice()));

            // تحميل الصورة
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImage())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ImgProduct);
            } else {
                ImgProduct.setImageResource(R.drawable.logo);
            }

            // زر الإعجاب أحمر (لأنه في المفضلة بالفعل)
            like_product.setImageResource(R.drawable.like_red);

            // النقر على المنتج
            itemView.setOnClickListener(v -> listener.onProductClick(product));

            // إزالة من المفضلة
            like_product.setOnClickListener(v -> {
                listener.onRemoveFromFavorites(product);
                Toast.makeText(itemView.getContext(), "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
            });

            // إضافة إلى السلة
            if (addToCart != null) {
                addToCart.setOnClickListener(v -> listener.onAddToCartClick(product));
            }
        }
    }
}
