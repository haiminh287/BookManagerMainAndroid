package com.example.bookmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;


import com.example.bookmanager.ui.AccountFragment;
import com.example.bookmanager.ui.CartFragment;
import com.example.bookmanager.ui.HomeFragment;
import com.example.bookmanager.ui.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private TextView title;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        //EdgeToEdge.enable(this);
        boolean showCart = sharedPreferences.getBoolean("show_cart", false);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        title = findViewById(R.id.title);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
            Fragment selectedFragment = null;

            if (item.getItemId() ==R.id.nav_home ){
                selectedFragment = new HomeFragment();
                title.setText("Trang Chủ");
                Log.i("Tag","Home");
            }
            else if (item.getItemId()==R.id.nav_account){
                isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
                if(isLoggedIn){
                    Log.e("Tag",""+isLoggedIn);
                    selectedFragment = new AccountFragment();
                    title.setText("Tài Khoản");
                }

                else{
                    selectedFragment = new LoginFragment();
                    title.setText("Đăng Nhập");
                }


                Log.i("Tag","Account");
            }
            if (item.getItemId()==R.id.nav_cart){
                selectedFragment = new CartFragment();
                title.setText("Giỏ Hàng");
                Log.i("Tag","Cart");
            }
            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, selectedFragment)
                        .commit();
            }
            return true;
        });
        Log.i("Tag",""+savedInstanceState);
        if (savedInstanceState == null) {
            if(showCart){
                bottomNavigationView.setSelectedItemId(R.id.nav_cart);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("show_cart", false);
                editor.apply();
            }
            else
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
}