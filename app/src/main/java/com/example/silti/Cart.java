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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.silti.databinding.FragmentCartBinding;

import java.util.ArrayList;

public class Cart extends Fragment {

    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;
    private long currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        getCurrentUser();
        setupRecyclerView();
        setupClickListeners();
        observeCartData();

    }

    private void initViewModel() {
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
        cartAdapter = new CartAdapter(new ArrayList<>(), new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onQuantityChanged(table_cart item, int newQuantity) {
                if (newQuantity <= 0) {
                    cartViewModel.removeFromCart(item.getProductId());
                } else {
                    cartViewModel.updateQuantity(item.getId(), newQuantity);
                }
            }

            @Override
            public void onRemoveClick(table_cart item) {
                cartViewModel.removeFromCart(item.getProductId());
                Toast.makeText(requireContext(), "تم حذف " + item.getName() + " من السلة", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(table_cart item) {
                Intent intent = new Intent(requireContext(), MainProduct.class);
                intent.putExtra("product_id", item.getProductId());
                startActivity(intent);
            }
        });

        binding.productinCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.productinCart.setAdapter(cartAdapter);
    }
    private void setupClickListeners() {
        binding.clear.setOnClickListener(v -> {
            if (currentUserId != -1) {
                cartViewModel.clearCart();
                Toast.makeText(requireContext(), "تم إفراغ السلة", Toast.LENGTH_SHORT).show();
            }
        });

        binding.shoppingNow.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameNavi, new HomeFragment())
                    .commit();
        });

        // زر إتمام الشراء
        binding.btnCheckout.setOnClickListener(v -> {
            Double total = cartViewModel.getCartTotal().getValue();
            if (total != null && total > 0) {
                Intent intent = new Intent(requireContext(), PaymentMethod.class);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "السلة فارغة", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeCartData() {
        if (currentUserId == -1) return;

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null && !items.isEmpty()) {
                binding.imgCart.setVisibility(View.GONE);
                binding.shoppingNow.setVisibility(View.GONE);
                binding.productinCart.setVisibility(View.VISIBLE);
                binding.bottomLayout.setVisibility(View.VISIBLE);
                cartAdapter.updateItems(items);
                updateTotalPrice();
            } else {
                binding.imgCart.setVisibility(View.VISIBLE);
                binding.shoppingNow.setVisibility(View.VISIBLE);
                binding.productinCart.setVisibility(View.GONE);
                binding.bottomLayout.setVisibility(View.GONE);
            }
        });

        cartViewModel.getCartTotal().observe(getViewLifecycleOwner(), total -> {
            updateTotalPrice();
        });
    }

    private void updateTotalPrice() {
        Double total = cartViewModel.getCartTotal().getValue();
        if (total != null) {
            binding.tvTotalPrice.setText(String.format("$%.2f", total));
        } else {
            binding.tvTotalPrice.setText("$0.00");
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}