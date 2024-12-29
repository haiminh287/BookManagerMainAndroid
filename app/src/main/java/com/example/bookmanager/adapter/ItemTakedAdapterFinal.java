package com.example.bookmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmanager.R;
import com.example.bookmanager.model.ItemTaked;

import java.util.List;

public class ItemTakedAdapterFinal extends RecyclerView.Adapter<ItemTakedAdapterFinal.CardViewHolder> {
    private Context context;
    private List<ItemTaked> itemTakedList;

    public ItemTakedAdapterFinal(Context context, List<ItemTaked> itemTakedList) {
        this.context = context;
        this.itemTakedList = itemTakedList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_taked_final, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ItemTaked itemTaked = itemTakedList.get(position);
        holder.itemTakedTitle.setText(itemTaked.getTitle());
        holder.itemTakedPrice.setText(String.format("%.2f", itemTaked.getPrice())+"đ");
        holder.itemTakedQuatity.setText("Số lượng : " +String.valueOf(itemTaked.getQuantity()));

        Glide.with(holder.itemView.getContext())
                .load(itemTaked.getUrlImage())
                .into(holder.itemTakedImage);

    }

    @Override
    public int getItemCount() {
        return itemTakedList.size();
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView itemTakedTitle;
        TextView itemTakedPrice;
        TextView itemTakedQuatity;
        ImageView itemTakedImage;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTakedTitle = itemView.findViewById(R.id.itemTaked_title);
            itemTakedPrice = itemView.findViewById(R.id.itemTaked_price);
            itemTakedQuatity = itemView.findViewById(R.id.itemTaked_quantity);
            itemTakedImage = itemView.findViewById(R.id.itemTaked_imageView);
        }
    }
}
