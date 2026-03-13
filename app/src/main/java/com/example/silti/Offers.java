package com.example.silti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silti.databinding.FragmentOffersBinding;
import java.util.ArrayList;
import java.util.List;

public class Offers extends Fragment {

    private FragmentOffersBinding binding;
    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private OffersAdapter offersAdapter;
    private long currentUserId;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Offers() {
        // Required empty public constructor
    }

    public static Offers newInstance(String param1, String param2) {
        Offers fragment = new Offers();
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
        binding = FragmentOffersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModels();
        getCurrentUser();
        setupRecyclerView();
        loadDiscountedProducts();
    }

    private void initViewModels() {
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
    }

    private void getCurrentUser() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyCartPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getLong("userId", -1);

        if (currentUserId != -1) {
            cartViewModel.setCurrentUserId(currentUserId);
        }
    }

    private void setupRecyclerView() {
        offersAdapter = new OffersAdapter(new ArrayList<>(), new OffersAdapter.OnOfferClickListener() {
            @Override
            public void onOfferClick(table_product product) {
                Intent intent = new Intent(requireContext(), MainProduct.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onBuyClick(table_product product) {
                if (currentUserId != -1) {
                    // حساب السعر بعد الخصم
                    double price = product.getDiscount() > 0 ?
                            product.getPrice() * (1 - product.getDiscount() / 100) :
                            product.getPrice();

                    cartViewModel.addToCart(
                            product.getId(),
                            product.getName(),
                            price,
                            product.getImage(),
                            1,
                            "M"
                    );

                    Toast.makeText(requireContext(), "تمت الإضافة إلى السلة", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.rvOffer.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOffer.setAdapter(offersAdapter);
    }

    private void loadDiscountedProducts() {
        productViewModel.getDiscountedProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                offersAdapter.updateProducts(products);
                binding.title.setText("الخصومات");
                binding.title.setVisibility(View.VISIBLE);
            } else {
                binding.title.setText("لا توجد عروض حالياً");
                offersAdapter.updateProducts(new ArrayList<>());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
