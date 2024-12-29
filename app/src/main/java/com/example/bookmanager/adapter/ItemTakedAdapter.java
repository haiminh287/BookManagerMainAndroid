package com.example.bookmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmanager.model.CardItem;
import com.example.bookmanager.DetailActivity;
import com.example.bookmanager.R;
import com.example.bookmanager.model.ItemTaked;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemTakedAdapter extends RecyclerView.Adapter<ItemTakedAdapter.CardViewHolder> {
    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }
    public interface OnDeleteListener {
        void onDelete();
    }
    private Context context;
    private List<ItemTaked> itemTakedList;

    private OnQuantityChangeListener quantityChangeListener;

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }


    public ItemTakedAdapter(Context context, List<ItemTaked> itemTakedList) {
        this.context = context;
        this.itemTakedList = itemTakedList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_taked, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ItemTaked itemTaked = itemTakedList.get(position);
        holder.itemTakedTitle.setText(itemTaked.getTitle());
        holder.itemTakedPrice.setText(String.format("%.2f", itemTaked.getPrice())+"đ");
        holder.itemTakedQuatity.setText(String.valueOf(itemTaked.getQuantity()));
        holder.itemTakedIncrease.setOnClickListener(v -> updateQuantity(itemTaked, holder, true));
        holder.itemTakedDecrease.setOnClickListener(v -> updateQuantity(itemTaked, holder, false));
        holder.itemTakedBtnDelete.setOnClickListener(v -> deleteItem(itemTaked, position));

        Glide.with(holder.itemView.getContext())
                .load(itemTaked.getUrlImage())
                .into(holder.itemTakedImage);

    }

    @Override
    public int getItemCount() {
        return itemTakedList.size();
    }
    private void deleteItem(ItemTaked itemTaked, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Log.e("API_CALL", "User ID not found in SharedPreferences");
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Void> call = apiService.deleteTakedBookQuantity(userId, itemTaked.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    itemTakedList.remove(position);
                    notifyItemRemoved(position);
                    Log.d("API_CALL", "Deleted successfully");
                } else {
                    Log.e("API_CALL", "Failed to delete item: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_CALL", "Error: " + t.getMessage());
            }
        });
    }
    private void updateQuantity(ItemTaked itemTaked, CardViewHolder holder, boolean isIncrease) {
        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Log.e("API_CALL", "User ID not found in SharedPreferences");
            return;
        }

        int currentQuantity = itemTaked.getQuantity();
        int newQuantity = isIncrease ? currentQuantity + 1 : currentQuantity - 1;

        if (newQuantity < 1) {
            Log.e("API_CALL", "Quantity cannot be less than 1");
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Log.i("API_CALL", ""+itemTaked.getId()+" "+newQuantity);
        Call<Void> call = apiService.updateTakedBookQuantity(userId, itemTaked.getId(), newQuantity);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    itemTaked.setQuantity(newQuantity);
                    holder.itemTakedQuatity.setText(String.valueOf(newQuantity));

                    Log.d("API_CALL", "Quantity updated successfully to: " + newQuantity);
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onQuantityChanged();
                    }
                } else {
                    Log.e("API_CALL", "Failed to update quantity: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_CALL", "Error: " + t.getMessage());
            }
        });
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView itemTakedTitle;
        TextView itemTakedPrice;
        CardView cardView;
        TextView itemTakedQuatity;
        ImageButton itemTakedIncrease;
        ImageButton itemTakedDecrease;
        ImageView itemTakedImage;
        ImageButton itemTakedBtnDelete;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTakedTitle = itemView.findViewById(R.id.itemTaked_title);
            itemTakedPrice = itemView.findViewById(R.id.itemTaked_price);
            itemTakedQuatity = itemView.findViewById(R.id.itemTaked_quantity);
            itemTakedImage = itemView.findViewById(R.id.itemTaked_imageView);
            itemTakedIncrease = itemView.findViewById(R.id.btn_increase);
            itemTakedDecrease = itemView.findViewById(R.id.btn_decrease);
            itemTakedBtnDelete = itemView.findViewById(R.id.btn_delete);
            cardView = itemView.findViewById(R.id.item_taked_view);
        }
    }
}
