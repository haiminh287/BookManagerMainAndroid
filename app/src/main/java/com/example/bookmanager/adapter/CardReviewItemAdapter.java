package com.example.bookmanager.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.model.CardReviewItem;

import java.util.List;

public class CardReviewItemAdapter extends RecyclerView.Adapter<CardReviewItemAdapter.CardReviewViewHolder> {

    private Context context;
    private List<CardReviewItem> reviewList;

    // Constructor
    public CardReviewItemAdapter(Context context, List<CardReviewItem> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public CardReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_review, parent, false);
        return new CardReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardReviewViewHolder holder, int position) {
        CardReviewItem reviewItem = reviewList.get(position);
        holder.tvContent.setText(reviewItem.getContent());
        holder.tvCreatedAt.setText(reviewItem.getCreated_at());
        holder.tvName.setText(reviewItem.getName());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class CardReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvCreatedAt, tvName;

        public CardReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.card_content_review);
            tvCreatedAt = itemView.findViewById(R.id.card_created_at_review);
            tvName = itemView.findViewById(R.id.card_nameUser_review);
        }
    }
}

