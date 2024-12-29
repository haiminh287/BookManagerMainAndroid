package com.example.bookmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmanager.model.CardItem;
import com.example.bookmanager.DetailActivity;
import com.example.bookmanager.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context context;
    private List<CardItem> cardItemList;

    public CardAdapter(Context context, List<CardItem> cardItemList) {
        this.context = context;
        this.cardItemList = cardItemList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem cardItem = cardItemList.get(position);
        holder.cardTitle.setText(cardItem.getTitle());
        holder.cardDescription.setText(cardItem.getDescription());
        holder.cardPrice.setText(String.format("%.2f", cardItem.getPrice()) +"đ");
        Glide.with(holder.itemView.getContext())
                .load(cardItem.getUrlImage())
                .into(holder.cardImage);
        holder.cardView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("book_id", cardItem.getId());
            editor.apply();

            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle;
        TextView cardDescription;
        CardView cardView;
        TextView cardPrice;

        ImageView cardImage;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.card_title);
            cardImage = itemView.findViewById(R.id.card_imageView);
            cardDescription = itemView.findViewById(R.id.card_description);
            cardPrice = itemView.findViewById(R.id.card_price);;
            cardView = itemView.findViewById(R.id.card_view);

        }
    }
}
