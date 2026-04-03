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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<table_product> products;
    private List<table_faivorate> favorites = new ArrayList<>();
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(table_product product);
        void onFavoriteClick(table_product product, boolean isFavorite);
        void onAddToCartClick(table_product product);
    }

    public ProductAdapter(List<table_product> products, OnProductClickListener listener) {
        this.products = products != null ? products : new ArrayList<>();
        this.listener = listener;
    }

    public void updateProducts(List<table_product> newProducts) {
        this.products = newProducts != null ? newProducts : new ArrayList<>();
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
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_background, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        table_product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ImgProduct;
        private TextView name, price;
        private ImageButton like_product, like_red;
        private ImageButton addToCart; // ✅ إضافة متغير زر السلة

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ImgProduct = itemView.findViewById(R.id.ImgProduct);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            like_product = itemView.findViewById(R.id.like_product);
            like_red = itemView.findViewById(R.id.like_red);

            // ✅ محاولة العثور على زر السلة (قد لا يكون موجوداً في كل التصاميم)
            View cartBtn = itemView.findViewById(R.id.addToCart);
            if (cartBtn instanceof ImageButton) {
                addToCart = (ImageButton) cartBtn;
            }
        }

        void bind(table_product product) {
            name.setText(product.getName());
            price.setText(String.format("%.0f", product.getPrice()));

            // تحميل الصورة
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImage())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.logo)
                                .error(R.drawable.logo)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(ImgProduct);
            } else {
                ImgProduct.setImageResource(R.drawable.logo);
            }

            // تحديث حالة الإعجاب
            boolean isFav = isFavorite(product.getId());

            // ✅ التحقق من وجود الـ Views قبل محاولة تغيير visibility
            if (like_product != null && like_red != null) {
                like_product.setVisibility(isFav ? View.GONE : View.VISIBLE);
                like_red.setVisibility(isFav ? View.VISIBLE : View.GONE);

                like_product.setOnClickListener(v -> {
                    listener.onFavoriteClick(product, false);
                    like_product.setVisibility(View.GONE);
                    like_red.setVisibility(View.VISIBLE);
                });

                like_red.setOnClickListener(v -> {
                    listener.onFavoriteClick(product, true);
                    like_red.setVisibility(View.GONE);
                    like_product.setVisibility(View.VISIBLE);
                });
            }

            // ✅ التحقق من وجود زر السلة قبل إضافة المستمع
            if (addToCart != null) {
                addToCart.setOnClickListener(v -> listener.onAddToCartClick(product));
            }

            itemView.setOnClickListener(v -> listener.onProductClick(product));
        }
    }
}