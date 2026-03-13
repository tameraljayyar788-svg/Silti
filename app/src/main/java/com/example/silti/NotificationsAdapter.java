package com.example.silti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.silti.databinding.RvNotificationsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<table_notifications> notifications;
    private OnNotificationClickListener listener;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    public interface OnNotificationClickListener {
        void onNotificationClick(table_notifications notification);
        void onNotificationLongClick(table_notifications notification);
    }

    public NotificationsAdapter(List<table_notifications> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications != null ? notifications : new ArrayList<>();
        this.listener = listener;
    }

    public void updateNotifications(List<table_notifications> newNotifications) {
        this.notifications = newNotifications != null ? newNotifications : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_notifications, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        table_notifications notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView title, massage, time;
        private ImageView imageNotification;
        private CardView cardView;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            massage = itemView.findViewById(R.id.massage);
            time = itemView.findViewById(R.id.time);
            imageNotification = itemView.findViewById(R.id.imageNotification);
            cardView = (CardView) itemView;
        }

        void bind(table_notifications notification) {
            title.setText(notification.getTitle());
            massage.setText(notification.getMessage());

            // تنسيق الوقت
            time.setText(timeFormat.format(new Date(notification.getTimestamp())));

            // تغيير لون الخلفية إذا كان غير مقروء
            if (!notification.isRead()) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.unread_background));
                title.setTextColor(itemView.getContext().getColor(R.color.black));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.white));
                title.setTextColor(itemView.getContext().getColor(R.color.gray));
            }

            // اختيار الأيقونة المناسبة حسب نوع الإشعار
            int iconRes = getIconForType(notification.getType(), notification.getIcon());

            if (iconRes != 0) {
                imageNotification.setImageResource(iconRes);
            } else if (notification.getIcon() != null && !notification.getIcon().isEmpty()) {
                // محاولة تحميل الصورة من الرابط
                Glide.with(itemView.getContext())
                        .load(notification.getIcon())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(imageNotification);
            } else {
                imageNotification.setImageResource(R.drawable.logo);
            }

            itemView.setOnClickListener(v -> listener.onNotificationClick(notification));
            itemView.setOnLongClickListener(v -> {
                listener.onNotificationLongClick(notification);
                return true;
            });
        }

        private int getIconForType(String type, String iconName) {
            if (type == null) return 0;

            switch (type) {
                case "ai":
                    return R.drawable.robot;
                case "order":
                    if ("delivery".equals(iconName)) {
                        return R.drawable.product;
                    } else if ("delivered".equals(iconName)) {
                        return R.drawable.bus;
                    } else {
                        return R.drawable.box;
                    }
                case "promo":
                    return R.drawable.newoffer;
                case "system":
                    return R.drawable.logo;
                default:
                    return 0;
            }
        }
    }
}