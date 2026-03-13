package com.example.silti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.DetailViewHolder> {

    private List<String> descriptions;
    private float rating;

    public ProductDetailsAdapter(float rating, String description) {
        this.rating = rating;
        this.descriptions = new ArrayList<>();
        this.descriptions.add(description);
    }

    public void updateDescription(String description) {
        this.descriptions.clear();
        this.descriptions.add(description);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_data_product, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        holder.bind(descriptions.get(position));
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {
        private TextView text_description, description;
        private RatingBar rating;

        DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            text_description = itemView.findViewById(R.id.text_description);
            description = itemView.findViewById(R.id.description);
            rating = itemView.findViewById(R.id.rating);
        }

        void bind(String desc) {
            description.setText(desc);
            rating.setRating(ProductDetailsAdapter.this.rating);
        }
    }
}
