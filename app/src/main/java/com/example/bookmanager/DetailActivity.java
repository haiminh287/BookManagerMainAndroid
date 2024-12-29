package com.example.bookmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmanager.adapter.CardReviewItemAdapter;
import com.example.bookmanager.adapter.OrderAdapter;
import com.example.bookmanager.model.CardItem;
import com.example.bookmanager.model.CardReviewItem;
import com.example.bookmanager.model.Category;
import com.example.bookmanager.model.Order;
import com.example.bookmanager.model.ReviewStatusResponse;
import com.example.bookmanager.model.User;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;
import com.example.bookmanager.ui.CartFragment;
import com.example.bookmanager.ui.RegisterFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private CardReviewItemAdapter cardReviewItemAdapter;
    private List<CardReviewItem> cardReviewItemList;
    private RecyclerView reviewRecyclerView;
    private Button btnReview;
    private EditText editTextReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView title = findViewById(R.id.detail_title);
        TextView description = findViewById(R.id.detail_description);
        TextView author = findViewById(R.id.detail_author);
        TextView price = findViewById(R.id.detail_price);
        TextView quantity = findViewById(R.id.detail_quantity);
        ImageView image = findViewById(R.id.detail_image);
        ImageView backButton = findViewById(R.id.back_button);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        Button btnViewCart = findViewById(R.id.btnViewCart);
        btnReview = findViewById(R.id.btnReview);
        editTextReview = findViewById(R.id.reviewText);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int bookId = sharedPreferences.getInt("book_id", -1);
        int userId = sharedPreferences.getInt("user_id",-1);
        cardReviewItemList = new ArrayList<>();
        reviewRecyclerView = findViewById(R.id.review_recyclerview);
        cardReviewItemAdapter = new CardReviewItemAdapter(this, cardReviewItemList);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(cardReviewItemAdapter);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> call = apiService.addBookToCart(userId, bookId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(DetailActivity.this, "Đã thêm sách vào giỏ", Toast.LENGTH_SHORT).show();
                            Log.d("API_CALL", "Book added to cart successfully.");
                        } else {
                            Log.e("API_CALL", "Failed to add book to cart: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("API_CALL", "Error: " + t.getMessage());
                    }
                });
            }
        });
        btnViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("show_cart", true);
                editor.apply();

                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextReview.getText().toString();
                Log.d("Content",""+content);
                Call<Void> call =  apiService.addReview(bookId,content,userId);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(DetailActivity.this, "Đánh giá đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                            editTextReview.setVisibility(View.GONE);
                            btnReview.setVisibility(View.GONE);
                            loadReviews();
                        }else {
                            Log.e("API_CALL", "Failed to Review  " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("API_Error", "Lỗi khi gọi API: " + t.getMessage(), t);
                        Toast.makeText(DetailActivity.this, "Không thể kết nối đến máy chủ. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Call<CardItem> call = apiService.getBookDetail(bookId);
        call.enqueue(new Callback<CardItem>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<CardItem> call, Response<CardItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = new Gson().toJson(response.body());
                    Log.d("API_Body2", "Response data: " + jsonData);
                    CardItem cardItem = response.body();

                    title.setText(cardItem.getTitle());
                    description.setText(cardItem.getDescription());
                    author.setText(cardItem.getAuthor());
                    price.setText(String.format("%.2f", cardItem.getPrice()) +"đ");
                    quantity.setText("Số lượng : "+String.valueOf(cardItem.getQuantity()) );
                    Glide.with(DetailActivity.this)
                            .load(cardItem.getUrlImage())
                            .into(image);
                    loadReviews();
                } else {
                    Log.e("API_CALL", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CardItem> call, Throwable t) {
                Log.e("API_CALL", "Failure: " + t.getMessage());
            }
        });

//

    }

    private void loadStatusReviews(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        int bookId = sharedPreferences.getInt("book_id", -1);
        int userId = sharedPreferences.getInt("user_id",-1);
        Call<ReviewStatusResponse> call = apiService.getReviewStatus(userId,bookId);
        call.enqueue(new Callback<ReviewStatusResponse>() {
            @Override
            public void onResponse(Call<ReviewStatusResponse> call, Response<ReviewStatusResponse> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("active")){
                        btnReview.setVisibility(View.VISIBLE);
                        editTextReview.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(DetailActivity.this, "Failed to get status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewStatusResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadReviews() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        int bookId = sharedPreferences.getInt("book_id", -1);
        Call<List<CardReviewItem>> call = apiService.getReviews(bookId);
        call.enqueue(new Callback<List<CardReviewItem>>() {
            @Override
            public void onResponse(Call<List<CardReviewItem>> call, Response<List<CardReviewItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadStatusReviews();
                    cardReviewItemList.clear();
                    cardReviewItemList.addAll(response.body());
                    cardReviewItemAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DetailActivity.this, "Không thể tải danh sách đánh giá.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CardReviewItem>> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Lỗi kết nối. Không thể tải đánh giá.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}