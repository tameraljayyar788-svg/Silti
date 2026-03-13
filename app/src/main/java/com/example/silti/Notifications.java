package com.example.silti;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silti.databinding.ActivityNotificationsBinding;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {
    private ActivityNotificationsBinding binding;
    private NotificationViewModel notificationViewModel;
    private NotificationsAdapter notificationsAdapter;
    private long currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        initViewModel();
        getCurrentUser();
        setupRecyclerView();
        setupClickListeners();
        loadNotifications();

        // إضافة بعض الإشعارات التجريبية (يمكن إزالتها لاحقاً)
        addSampleNotifications();
    }

    private void initViewModel() {
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);

        if (currentUserId != -1) {
            notificationViewModel.setCurrentUserId(currentUserId);
        }
    }

    private void setupRecyclerView() {
        notificationsAdapter = new NotificationsAdapter(new ArrayList<>(),
                new NotificationsAdapter.OnNotificationClickListener() {
                    @Override
                    public void onNotificationClick(table_notifications notification) {
                        handleNotificationClick(notification);
                    }

                    @Override
                    public void onNotificationLongClick(table_notifications notification) {
                        showNotificationOptions(notification);
                    }
                });

        binding.recyclerNotification.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerNotification.setAdapter(notificationsAdapter);
    }

    private void setupClickListeners() {
        binding.back.setOnClickListener(v -> finish());
    }

    private void loadNotifications() {
        if (currentUserId != -1) {
            notificationViewModel.getAllNotifications().observe(this, notifications -> {
                if (notifications != null && !notifications.isEmpty()) {
                    notificationsAdapter.updateNotifications(notifications);
                    binding.recyclerNotification.setVisibility(View.VISIBLE);
                } else {
                    // عرض رسالة لا توجد إشعارات
                    notificationsAdapter.updateNotifications(new ArrayList<>());
                    Toast.makeText(this, "لا توجد إشعارات", Toast.LENGTH_SHORT).show();
                }
            });

            // تحديث عداد الإشعارات غير المقروءة (يمكن استخدامه في شاشة الرئيسية)
            notificationViewModel.getUnreadCount().observe(this, count -> {
                // يمكن تحديث badge في شاشة الرئيسية
            });
        }
    }

    private void handleNotificationClick(table_notifications notification) {
        // تحديث حالة الإشعار إلى مقروء
        if (!notification.isRead()) {
            notificationViewModel.markAsRead(notification.getId());
        }

        // التوجيه بناءً على نوع الإشعار
        switch (notification.getType()) {
            case "order":
                // فتح تفاصيل الطلب
                Intent orderIntent = new Intent(this, Talabaty.class);
                orderIntent.putExtra("order_id", notification.getRelatedId());
                startActivity(orderIntent);
                break;

            case "promo":
                // فتح صفحة العروض
                Intent promoIntent = new Intent(this, Offers.class);
                startActivity(promoIntent);
                break;

            case "ai":
                // فتح صفحة المساعد الذكي
                // يمكن فتح الحوار مع المساعد مباشرة
                Toast.makeText(this, "رسالة من المساعد الذكي", Toast.LENGTH_SHORT).show();
                break;

            case "system":
                // إشعار نظام - فقط يعرض التفاصيل
                showNotificationDetails(notification);
                break;

            default:
                // نوع غير معروف
                Toast.makeText(this, notification.getMessage(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showNotificationOptions(table_notifications notification) {
        // إظهار خيارات للإشعار (حذف، تحديد كمقروء، إلخ)
        String[] options;
        if (notification.isRead()) {
            options = new String[]{"حذف", "تحديد كغير مقروء"};
        } else {
            options = new String[]{"حذف", "تحديد كمقروء"};
        }

        new AlertDialog.Builder(this)
                .setTitle(notification.getTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // حذف
                        notificationViewModel.deleteById(notification.getId());
                        Toast.makeText(this, "تم حذف الإشعار", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        // تبديل حالة القراءة
                        if (notification.isRead()) {
                            notification.setRead(false);
                            notificationViewModel.update(notification);
                        } else {
                            notificationViewModel.markAsRead(notification.getId());
                        }
                    }
                })
                .show();
    }

    private void showNotificationDetails(table_notifications notification) {
        new AlertDialog.Builder(this)
                .setTitle(notification.getTitle())
                .setMessage(notification.getMessage())
                .setPositiveButton("موافق", null)
                .show();
    }

    // ========== إضافة إشعارات تجريبية ==========
    private void addSampleNotifications() {
        if (currentUserId == -1) return;

        // إشعار من المساعد الذكي
        notificationViewModel.insertNotification(
                "المساعد الذكي",
                "مرحباً! كيف يمكنني مساعدتك اليوم؟",
                "ai",
                "robot",
                null
        );

        // إشعار طلب قيد التوصيل
        notificationViewModel.insertNotification(
                "طلبك قيد التوصيل",
                "طلب رقم #ORD-12345 في طريقه إليك",
                "order",
                "delivery",
                12345L
        );

        // إشعار تم التوصيل
        notificationViewModel.insertNotification(
                "تم توصيل طلبك",
                "طلب رقم #ORD-12344 تم توصيله بنجاح",
                "order",
                "delivered",
                12344L
        );

        // إشعار عرض جديد
        notificationViewModel.insertNotification(
                "عرض خاص!",
                "خصم 30% على جميع المنتجات لفترة محدودة",
                "promo",
                "offer",
                null
        );

        // إشعار نظام
        notificationViewModel.insertNotification(
                "تحديث التطبيق",
                "نسخة جديدة من التطبيق متاحة الآن",
                "system",
                "update",
                null
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}