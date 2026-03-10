package com.example.silti;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.silti.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private UserViewModel userViewModel;
    private CartViewModel cartViewModel;
    private FavoriteViewModel favoriteViewModel;

    private long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModels();
        getCurrentUser();
        setupBottomNavigation();

        // تحميل الصفحة الرئيسية افتراضياً
        loadFragment(new HomeFragment());
    }

    private void initViewModels() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = getSharedPreferences("MyCartPrefs", MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);

        if (currentUserId != -1) {
            userViewModel.setCurrentUser(currentUserId);
            cartViewModel.setCurrentUserId(currentUserId);
            favoriteViewModel.setCurrentUserId(currentUserId);
        }
    }

    private void setupBottomNavigation() {
        binding.navigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.offers) {
                selectedFragment = new Offers();
            } else if (itemId == R.id.cart) {
                selectedFragment = new Cart();
            } else if (itemId == R.id.ai) {
                selectedFragment = new Ai();
            } else if (itemId == R.id.profile) {
                selectedFragment = new Profile();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameNavi, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}