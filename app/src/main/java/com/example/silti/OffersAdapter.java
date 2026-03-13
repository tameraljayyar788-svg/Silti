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

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {

    private List<table_product> discountedProducts;
    private OnOfferClickListener listener;

    public interface OnOfferClickListener {
        void onOfferClick(table_product product);
        void onBuyClick(table_product product);
    }

    public OffersAdapter(List<table_product> products, OnOfferClickListener listener) {
        this.discountedProducts = products != null ? products : new ArrayList<>();
        this.listener = listener;
    }

    public void updateProducts(List<table_product> newProducts) {
        this.discountedProducts = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offers_rv_item, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        table_product product = discountedProducts.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return discountedProducts.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name_product, textTime, SallDiscount, newPrice, oldPrice, textButton;
        private View DiscountBackground;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name_product = itemView.findViewById(R.id.name_product);
            textTime = itemView.findViewById(R.id.textTime);
            SallDiscount = itemView.findViewById(R.id.SallDiscount);
            newPrice = itemView.findViewById(R.id.newPrice);
            oldPrice = itemView.findViewById(R.id.oldPrice);
            textButton = itemView.findViewById(R.id.textButton);
            DiscountBackground = itemView.findViewById(R.id.DiscountBackground);
        }

        void bind(table_product product) {
            name_product.setText(product.getName());

            // حساب السعر بعد الخصم
            double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100);

            oldPrice.setText(String.valueOf((int)product.getPrice()));
            newPrice.setText(String.valueOf((int)discountedPrice));
            SallDiscount.setText(String.valueOf((int)product.getDiscount()));
            textTime.setText("عرض لفترة محدودة");
            textButton.setText("اشتري الان $");

            // تحميل الصورة
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImage())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(image);
            } else {
                image.setImageResource(R.drawable.logo);
            }

            // Click listeners
            itemView.setOnClickListener(v -> listener.onOfferClick(product));
            textButton.setOnClickListener(v -> listener.onBuyClick(product));
        }
    }
}