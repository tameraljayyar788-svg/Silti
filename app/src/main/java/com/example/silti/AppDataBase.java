package com.example.silti;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                // User Related
                User.class,
                Address.class,

                // Categories - ✅ جميع الأسماء صحيحة الآن
                table_firstCategory.class,
                table_secondCategory.class,
                table_CategorisInside.class,
                table_category.class,

                // Products - ✅ جميع الأسماء صحيحة الآن
                table_product.class,
                table_sizes.class,
                table_productSizes.class,

                // Cart & Favorite
                table_cart.class,
                table_faivorate.class,

                // Orders
                table_order.class,
                order_item.class,

                // Payment
                table_paymentMethode.class,
                table_visa.class,

                // Other
                table_search.class,
                table_notifications.class
        },
        version = 2,  // ارفع الإصدار لأننا عدلنا في العلاقات
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDataBase extends RoomDatabase {

    // User DAOs
    public abstract UserDao userDao();
    public abstract AddressDao addressDao();

    // Category DAOs
    public abstract FirstCategoryDao firstCategoryDao();  // ✅ تأكد من أن الـ Dao بنفس الاسم
    public abstract SecondCategoryDao secondCategoryDao();
    public abstract CategorisInsideDao categorisInsideDao();
    public abstract CategoryDao categoryDao();

    // Product DAOs
    public abstract ProductDao productDao();
    public abstract SizeDao sizeDao();
    public abstract ProductSizeDao productSizeDao();

    // Cart & Favorite
    public abstract CartDao cartDao();
    public abstract FavoriteDao favoriteDao();

    // Order DAOs
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();

    // Payment DAOs
    public abstract PaymentMethodDao paymentMethodDao();
    public abstract VisaDao visaDao();

    // Other DAOs
    public abstract SearchDao searchDao();
    public abstract NotificationDao notificationDao();

    private static volatile AppDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDataBase.class,
                                    "my_cart_database")
                            .fallbackToDestructiveMigration()  // للتطوير فقط
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}