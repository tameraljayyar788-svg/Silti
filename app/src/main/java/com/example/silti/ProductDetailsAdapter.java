package com.example.silti;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.DetailViewHolder> {

    private List<ProductDetailItem> details;

    public ProductDetailsAdapter() {
        this.details = new ArrayList<>();
    }

    public void setDetails(table_product product) {
        details.clear();

        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            details.add(new ProductDetailItem("الوصف", product.getDescription(), DetailType.DESCRIPTION));
        }

        StringBuilder info = new StringBuilder();
        info.append("العلامة التجارية: ").append(product.getBrand() != null ? product.getBrand() : "غير محدد").append("\n");
        info.append("الكمية المتوفرة: ").append(product.getQuantity()).append("\n");
        info.append("عدد المبيعات: ").append(product.getSoldCount());

        details.add(new ProductDetailItem("معلومات إضافية", info.toString(), DetailType.INFO));

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_detail, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        ProductDetailItem item = details.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvContent;

        DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDetailTitle);
            tvContent = itemView.findViewById(R.id.tvDetailContent);
        }

        void bind(ProductDetailItem item) {
            tvTitle.setText(item.title);
            tvContent.setText(item.content);
        }
    }

    public enum DetailType {
        DESCRIPTION, INFO, SIZES
    }

    public static class ProductDetailItem {
        String title;
        String content;
        DetailType type;

        public ProductDetailItem(String title, String content, DetailType type) {
            this.title = title;
            this.content = content;
            this.type = type;
        }
    }
}
