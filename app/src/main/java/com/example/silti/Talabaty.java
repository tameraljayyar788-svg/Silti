package com.example.silti;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.silti.databinding.ActivityTalabatyBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Talabaty extends AppCompatActivity {

    ActivityTalabatyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTalabatyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // زر الرجوع
        binding.back.setOnClickListener(v -> {
            startActivity(new Intent(Talabaty.this, MainProduct.class));
            finish();
        });

        // منع السحب (مثل التصميم)
        binding.viewpagerTalabaty.setUserInputEnabled(false);

        // Adapter للـ ViewPager
        binding.viewpagerTalabaty.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new PendingFragment();
                    case 1:
                        return new DeliveredFragment();
                    case 2:
                        return new CancelledFragment();
                    default:
                        return new PendingFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        // ربط Tabs مع ViewPager
        new TabLayoutMediator(
                binding.tabTalabaty,
                binding.viewpagerTalabaty,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("قيد الطلب");
                            break;
                        case 1:
                            tab.setText("تم التوصيل");
                            break;
                        case 2:
                            tab.setText("ملغي");
                            break;
                    }
                }
        ).attach();

        // تغيير شكل التاب عند التحديد (بعد attach فقط)
        binding.tabTalabaty.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null && tab.view != null) {
                    tab.view.setBackgroundResource(R.drawable.tab_selected);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab != null && tab.view != null) {
                    tab.view.setBackgroundResource(R.drawable.tab_unselected);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // تفعيل أول Tab بأمان
        binding.tabTalabaty.post(() -> {
            TabLayout.Tab tab = binding.tabTalabaty.getTabAt(0);
            if (tab != null && tab.view != null) {
                tab.view.setBackgroundResource(R.drawable.tab_selected);
            }
        });
    }
}
