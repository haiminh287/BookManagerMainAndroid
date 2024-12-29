package com.example.bookmanager.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bookmanager.DetailActivity;
import com.example.bookmanager.OrderActivity;
import com.example.bookmanager.adapter.ItemTakedAdapter;
import com.example.bookmanager.model.ItemTaked;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.R;
import com.example.bookmanager.network.RetrofitClient;
import com.example.bookmanager.adapter.CardAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements ItemTakedAdapter.OnQuantityChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView cartRecyclerView;
    private ItemTakedAdapter itemTakedAdapter;
    private List<ItemTaked> itemsList =  new ArrayList<>(); ;
    private TextView cart_total_amount;
    private TextView cartCheckout;
    private float total_amount=0;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart,container,false);
        cartRecyclerView = view.findViewById(R.id.cart_recyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemTakedAdapter = new ItemTakedAdapter(getContext(), itemsList);
        itemTakedAdapter.setOnQuantityChangeListener(this);
        cartRecyclerView.setAdapter(itemTakedAdapter);
        loadCardsFromApi();
        cart_total_amount = view.findViewById(R.id.tv_total_price);
        cartCheckout = view.findViewById(R.id.btn_checkout);
        cartCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
//                holder.itemView.getContext().startActivity(intent);
                Intent intent = new Intent(view.getContext(), OrderActivity.class);
                view.getContext().startActivity(intent);
            }
        });
        return view;
    }

    private void loadCardsFromApi() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
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
                        Log.d("API_BodyNew", "Response data: " + response.body());
                        itemsList.clear();
                        itemsList.addAll(response.body());
                        Log.d("API_Body", "Response data: " + itemsList);
                        itemTakedAdapter.notifyDataSetChanged();

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
    }private void updateTotalAmount() {
        float totalAmount = 0;
        for (ItemTaked item : itemsList) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        cart_total_amount.setText(String.format("%.2f", totalAmount));
        Log.d("CartFragment", "Updated Total Amount: " + totalAmount);
    }

    @Override
    public void onQuantityChanged() {
        updateTotalAmount();
    }
}