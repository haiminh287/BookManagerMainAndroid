package com.example.bookmanager.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bookmanager.adapter.CategoryAdapter;
import com.example.bookmanager.model.CardItem;
import com.example.bookmanager.R;
import com.example.bookmanager.adapter.CardAdapter;
import com.example.bookmanager.model.Category;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewCategory;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private List<CardItem> cardItemList;
    private Button btnViewAll ;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewCategory = view.findViewById(R.id.categoryRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerViewCategory.setLayoutManager(gridLayoutManager);
        btnViewAll = view.findViewById(R.id.btnViewAll);
        categoryList = new ArrayList<>();
        cardItemList = new ArrayList<>();
        cardAdapter = new CardAdapter(getContext(), cardItemList);
        categoryAdapter = new CategoryAdapter(categoryList, cardAdapter, cardItemList);
        recyclerViewCategory.setAdapter(categoryAdapter);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cardAdapter);

        loadCategoryFromApi();
        loadCardsFromApi();

        return view;
    }

    private void loadCardsFromApi() {
        SharedPreferences sharedPreferences =  getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int categoryId = sharedPreferences.getInt("category_id", -1);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<CardItem>> call;
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("category_id", -1);
                editor.apply();
                loadCardsFromApi();
            }
        });
        if(categoryId==-1){
            call = apiService.getBooks();
        }
        else{
            call = apiService.getBooksByCategory(categoryId);
        }


        call.enqueue(new Callback<List<CardItem>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<CardItem>> call, Response<List<CardItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = new Gson().toJson(response.body());
                    Log.d("API_BodyBok", "Response data: " + jsonData);

                    cardItemList.clear();
                    cardItemList.addAll(response.body());
                    Log.d("API_Body", "Updated cardItemList size: " + cardItemList.size());
                    cardAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API_CALL", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CardItem>> call, Throwable t) {
                Log.e("API_CALL", "Failure: " + t.getMessage());
            }
        });
    }

    private void loadCategoryFromApi() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<Category>> call = apiService.getCategory();

        call.enqueue(new Callback<List<Category>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = new Gson().toJson(response.body());
                    Log.d("API_Body2", "Response data: " + jsonData);

                    categoryList.clear(); // Clear current list
                    categoryList.addAll(response.body()); // Add new categories
                    Log.d("API_Body", "Updated categoryList size: " + categoryList.size());
                    categoryAdapter.notifyDataSetChanged(); // Notify adapter
                } else {
                    Log.e("API_CALL", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("API_CALL", "Failure: " + t.getMessage());
            }
        });
    }
}
