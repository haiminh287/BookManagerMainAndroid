package com.example.bookmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.adapter.ItemTakedAdapter;
import com.example.bookmanager.adapter.ItemTakedAdapterFinal;
import com.example.bookmanager.model.ItemTaked;
import com.example.bookmanager.model.Order;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextEmail, editTextAddress;
    private RadioGroup radioGroupPaymentMethod, radioGroupDeliveryMethod;
    private TextView cart_total_amount;
    private RecyclerView orderRecyclerView;
    private ItemTakedAdapterFinal itemTakedAdapterFinal;
    private List<ItemTaked> itemsList =  new ArrayList<>(); ;
    private float total_amount=0;
    private ImageButton btnIncrease,btnDecrease;
    private Button btnOrder;
    private ImageView iv_back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
//        radioGroupPaymentMethod = findViewById(R.id.radioGroupPaymentMethod);
        orderRecyclerView = findViewById(R.id.order_recyclerview);
        cart_total_amount = findViewById(R.id.tv_total_price);
        iv_back_button=findViewById(R.id.back_button_order);
        btnOrder = findViewById(R.id.btn_order);

        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemTakedAdapterFinal = new ItemTakedAdapterFinal(this, itemsList);
        orderRecyclerView.setAdapter(itemTakedAdapterFinal);
        loadCardsFromApi();
        btnOrder.setOnClickListener(v -> submitOrder());
        iv_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void loadCardsFromApi() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        Log.i("UserIdLog",""+userId);
        if (userId != -1) {
            Call<List<ItemTaked>> call = apiService.getTakedBook(userId);
            call.enqueue(new Callback<List<ItemTaked>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<ItemTaked>> call, Response<List<ItemTaked>> response) {
                    if (response.isSuccessful() && response.body() != null) {

//                    String jsonData = new Gson().toJson(response.body());
                        Log.d("API_BodyNew", "Response data: " + itemsList);
                        itemsList.clear();
                        itemsList.addAll(response.body());
                        Log.d("API_Body", "Response data: " + itemsList);
                        itemTakedAdapterFinal.notifyDataSetChanged();

                        for (ItemTaked item : itemsList){
                            total_amount+=item.getPrice()*item.getQuantity();
                            Log.i("price",""+item.getPrice());
                        }
                        cart_total_amount.setText(String.format("%.2f",total_amount));
                    } else {
                        Log.e("API_CALL", "Response error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<ItemTaked>> call, Throwable t) {
                    // Xử lý lỗi khi call API
                    Log.e("API_CALL", "Failure: " + t.getMessage());
                }
            });
        }
    }
    private void submitOrder() {
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String email = editTextEmail.getText().toString();
        String address = editTextAddress.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ email hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Void> call = apiService.addOrder(userId,name,phone,email,address );
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrderActivity.this, HistoryOrderActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("API_CALL", "Failed to place order: " + response.code());
                    Toast.makeText(OrderActivity.this, "Đặt hàng không thành công", Toast.LENGTH_SHORT).show();
                }
            }
//
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_CALL", "Error: " + t.getMessage());
                Toast.makeText(OrderActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
