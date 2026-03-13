package com.example.silti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.silti.databinding.FragmentProfileBinding;

public class Profile extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentProfileBinding binding;
    private String mParam1;
    private String mParam2;

    // ViewModels
    private UserViewModel userViewModel;
    private FavoriteViewModel favoriteViewModel;
    private long currentUserId;
    private User currentUser;

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModels();
        getCurrentUser();
        loadUserData();
        setupClickListeners();
    }

    private void initViewModels() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        favoriteViewModel = new ViewModelProvider(requireActivity()).get(FavoriteViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyCartPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);

        if (currentUserId == -1) {
            Toast.makeText(requireContext(), "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        }
    }

    private void loadUserData() {
        userViewModel.getUserById(currentUserId).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;

                // عرض اسم المستخدم
                binding.name.setText(user.getUsername());

                // عرض البريد الإلكتروني
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    binding.link.setText(user.getEmail());
                } else {
                    binding.link.setText("إضافة البريد الإلكتروني");
                }

                // تحميل صورة الملف الشخصي
                if (user.getImageProfile() != null && !user.getImageProfile().isEmpty()) {
                    Glide.with(requireContext())
                            .load(user.getImageProfile())
                            .placeholder(R.drawable.profile_defult)
                            .error(R.drawable.profile_defult)
                            .circleCrop()
                            .into(binding.imgProfile);
                } else {
                    binding.imgProfile.setImageResource(R.drawable.profile_defult);
                }
            }
        });

        // تحميل عدد المنتجات في المفضلة
        favoriteViewModel.getFavoritesCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                // يمكن إضافة عداد بجانب أيقونة المفضلة إذا أردت
                // binding.favoriteBadge.setText(String.valueOf(count));
                // binding.favoriteBadge.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupClickListeners() {
        // طلباتي - الانتقال إلى شاشة الطلبات
        binding.talabty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), Talabaty.class);
                startActivity(intent);
            }
        });

        // الإعدادات - الانتقال إلى شاشة الإعدادات
        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), Setting.class);
                startActivity(intent);
            }
        });

        // أعجبني - الانتقال إلى شاشة المفضلة
        binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), Likey.class);
                startActivity(intent);
            }
        });

        // طريقة الدفع - الانتقال إلى شاشة بيانات الدفع
        binding.payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), PaymentMethod.class);
                startActivity(intent);
            }
        });

        // تقييم التطبيق
        binding.rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        // تسجيل الخروج
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });
    }

    private void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("تقييم التطبيق")
                .setMessage("شكراً لاستخدامك تطبيق سلتي! هل تريد تقييم التطبيق؟")
                .setPositiveButton("تقييم الآن", (dialog, which) -> {
                    // فتح رابط التقييم على متجر Google Play
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + requireContext().getPackageName())));
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + requireContext().getPackageName())));
                    }
                })
                .setNegativeButton("لاحقاً", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("تسجيل الخروج")
                .setMessage("هل أنت متأكد من تسجيل الخروج؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("لا", null)
                .show();
    }

    private void logout() {
        // مسح بيانات الجلسة
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyCartPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // العودة إلى شاشة تسجيل الدخول
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}