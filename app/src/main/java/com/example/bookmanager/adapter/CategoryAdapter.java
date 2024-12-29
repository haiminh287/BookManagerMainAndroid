package com.example.bookmanager.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.model.CardItem;
import com.example.bookmanager.model.Category;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<Category> categoryList;
    private CardAdapter cardAdapter;
    private List<CardItem> cardItemList; // Danh sách sách

    public CategoryAdapter(List<Category> categoryList, CardAdapter cardAdapter, List<CardItem> cardItemList) {
        this.categoryList = categoryList;
        this.cardAdapter = cardAdapter;
        this.cardItemList = cardItemList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.textViewCategoryName.setText(category.getName());
        holder.linearLayoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("category_id", category.getId());
                editor.apply();
                Log.i("TagNew", "Selected Category ID: " + category.getId());

                // Tạo instance của ApiService
                ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
                Call<List<CardItem>> call = apiService.getBooksByCategory(category.getId());
                call.enqueue(new Callback<List<CardItem>>() {
                    @Override
                    public void onResponse(Call<List<CardItem>> call, Response<List<CardItem>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonData = new Gson().toJson(response.body());
                            Log.d("API_Body2", "Response data: " + jsonData);

                            if (cardItemList != null) {
                                cardItemList.clear();
                                cardItemList.addAll(response.body());
                                Log.d("API_BodyNew", "Updated cardItemList size: " + cardItemList.size());
                                cardAdapter.notifyDataSetChanged();
                                Log.d("API_BodyNew2", "Updated cardItemList size: " + cardItemList.size());
                            } else {
                                Log.e("API_CALL", "cardItemList is null");
                            }
                        } else {
                            Log.e("API_CALL", "Response error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CardItem>> call, Throwable t) {
                        Log.e("API_CALL", "Failed to call API: " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategoryName;
        LinearLayout linearLayoutCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.category_name);
            linearLayoutCategory = itemView.findViewById(R.id.linearCategory);
        }
    }
}
