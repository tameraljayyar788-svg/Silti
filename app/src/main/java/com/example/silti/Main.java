package com.example.silti;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.silti.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class Main extends AppCompatActivity {

    ActivityMainBinding binding;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        manager = getSupportFragmentManager();
        binding.navigation.setSelectedItemId(R.id.Home);
        addFragment(new HomeFragment());


        binding.navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.Home){
                    addFragment(new HomeFragment() );
                } else if (item.getItemId() == R.id.cart) {
                    addFragment(new Cart());
                } else if (item.getItemId() == R.id.ai) {
                    addFragment(new Ai());
                }else if (item.getItemId() == R.id.offers) {
                    addFragment(new Offers());
                }
                else if (item.getItemId() == R.id.profile) {
                    addFragment(new Profile());
                }


                return true;
            }
        });




    }
    private void addFragment(Fragment fragment){
        manager.beginTransaction().replace(R.id.frameNavi , fragment).addToBackStack("").commit();
    }


}
