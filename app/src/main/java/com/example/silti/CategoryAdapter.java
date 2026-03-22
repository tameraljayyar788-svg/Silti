package com.example.silti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<String> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category, int position);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories != null ? categories : new ArrayList<>();
        this.listener = listener;
    }

    public void updateCategories(List<String> newCategories) {
        this.categories = newCategories != null ? newCategories : new ArrayList<>();
        selectedPosition = 0;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_horizontal, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.bind(category, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvCategory;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }

        void bind(String category, boolean isSelected) {
            tvCategory.setText(category);

            // تغيير المظهر حسب التحديد
            if (isSelected) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.primary_dark));
                tvCategory.setTextColor(itemView.getContext().getColor(R.color.black));
                cardView.setCardElevation(4f);
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.light_gray));
                tvCategory.setTextColor(itemView.getContext().getColor(R.color.white));
                cardView.setCardElevation(2f);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int previousPosition = selectedPosition;
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(previousPosition);
                    notifyItemChanged(selectedPosition);
                    listener.onCategoryClick(category, getAdapterPosition());
                }
            });
        }
    }
}
