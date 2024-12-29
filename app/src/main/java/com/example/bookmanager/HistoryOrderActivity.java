package com.example.bookmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.adapter.ItemTakedAdapterFinal;
import com.example.bookmanager.adapter.OrderAdapter;
import com.example.bookmanager.model.ItemTaked;
import com.example.bookmanager.model.Order;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryOrderActivity extends AppCompatActivity {

    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private RecyclerView orderRecyclerView;
    private ImageView iv_back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_order);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iv_back_button = findViewById(R.id.back_button_history_order);
        orderList = new ArrayList<>();
        orderRecyclerView = findViewById(R.id.history_order_recyclerview);
        orderAdapter = new OrderAdapter(this, orderList);
        Log.i("adapter",""+orderAdapter);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderRecyclerView.setAdapter(orderAdapter);

        // Lấy user_id từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy user_id. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        iv_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<Order>> call = apiService.getHistoryOrder(userId);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String jsonData = new Gson().toJson(response.body());
                    Log.d("API_Response", "Dữ liệu nhận được: " + jsonData);
                    orderList.clear();
                    orderList.addAll(response.body());
                    for(Order item : orderList){
                        Log.d("API_Response", "Dữ liệu nhận được: " + item.getId());
                    }
                    orderAdapter.notifyDataSetChanged();
                    Log.d("OrderList_AfterUpdate", "Size: " + orderList.size());
                } else {
                    Toast.makeText(HistoryOrderActivity.this, "Danh sách đơn hàng trống", Toast.LENGTH_SHORT).show();
                    Log.w("API_Response", "Không có dữ liệu trả về.");
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("API_Error", "Lỗi khi gọi API: " + t.getMessage(), t);
                Toast.makeText(HistoryOrderActivity.this, "Không thể kết nối đến máy chủ. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
