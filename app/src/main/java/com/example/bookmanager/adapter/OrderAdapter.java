package com.example.bookmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanager.R;
import com.example.bookmanager.model.ItemTaked;
import com.example.bookmanager.model.Order;
import com.example.bookmanager.model.ResponsePayment;
import com.example.bookmanager.model.ReviewStatusResponse;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private Context context;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderCode.setText(String.valueOf(order.getId()));
        holder.orderName.setText(order.getName());
        holder.orderState.setText(order.getState());
        holder.orderAddress.setText(order.getAddress());
        holder.orderDate.setText(order.getCreatedAt());
        fetchOrderStatus(order.getId(),holder);
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
                Call<ReviewStatusResponse> call = apiService.updateOrderStatus(order.getId());

                call.enqueue(new Callback<ReviewStatusResponse>() {
                    @Override
                    public void onResponse(Call<ReviewStatusResponse> call, Response<ReviewStatusResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            holder.btnCancel.setVisibility(View.GONE);
                            holder.btnPay.setVisibility(View.GONE);
                            if (context instanceof Activity) {
                                ((Activity) context).recreate(); // Reload the current activity
                            }
                            Log.i("orderStatus", "Fetched data: " + response.body().getStatus());
                        } else {
                            Log.e("OrderAdapter", "Error fetching order details: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewStatusResponse> call, Throwable t) {
                        Log.e("OrderAdapter", "Failure: " + t.getMessage());
                    }
                });
            }
        });
        holder.btnOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.orderDetailRecyclerview.getVisibility() == View.GONE) {
                    holder.orderDetailRecyclerview.setVisibility(View.VISIBLE);
                    fetchOrderDetails(order.getId(), holder);
                } else {
                    holder.orderDetailRecyclerview.setVisibility(View.GONE);
                }
            }
        });
        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
                Call<ResponsePayment> call = apiService.payOrder(order.getId());
                call.enqueue(new Callback<ResponsePayment>() {
                    @Override
                    public void onResponse(Call<ResponsePayment> call, Response<ResponsePayment> response) {
                        if(response.isSuccessful()){
                            try {
                                String momoDeepLink = response.body().getMomo_deep_link();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(momoDeepLink));
                                if (intent.resolveActivity(context.getPackageManager()) != null) {
                                    context.startActivity(intent);
                                } else {
                                    Intent playStoreIntent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=com.mservice.momotransfer"));
                                    context.startActivity(playStoreIntent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePayment> call, Throwable t) {

                    }
                });
            }
        });

    }

    private void fetchOrderDetails(int orderId, OrderViewHolder holder) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<ItemTaked>> call = apiService.getOrderDetail(orderId);

        call.enqueue(new Callback<List<ItemTaked>>() {
            @Override
            public void onResponse(Call<List<ItemTaked>> call, Response<List<ItemTaked>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i("OrderAdapter", "Fetched data: " + response.body());
                    List<ItemTaked> items = new ArrayList<>();
                    items.clear();
                    items.addAll(response.body());
                    Log.i("OrderAdapter", "Fetched data: " + items);
                    ItemTakedAdapterFinal itemTakedAdapterFinal = new ItemTakedAdapterFinal(context, items);
                    holder.orderDetailRecyclerview.setLayoutManager(new LinearLayoutManager(context));
                    Log.i("OrderAdapter", "Fetched data: " + itemTakedAdapterFinal);
                    holder.orderDetailRecyclerview.setAdapter(itemTakedAdapterFinal);
                    itemTakedAdapterFinal.notifyDataSetChanged();
                } else {
                    Log.e("OrderAdapter", "Error fetching order details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<ItemTaked>> call, Throwable t) {
                Log.e("OrderAdapter", "Failure: " + t.getMessage());
            }
        });
    }

    private void fetchOrderStatus(int orderId, OrderViewHolder holder) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ReviewStatusResponse> call = apiService.getOrderStatus(orderId);

        call.enqueue(new Callback<ReviewStatusResponse>() {
            @Override
            public void onResponse(Call<ReviewStatusResponse> call, Response<ReviewStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i("orderStatus", "Fetched data: " + response.body().getStatus());
                    String orderStatus = response.body().getStatus();
                    if(orderStatus.equals("confirmed")){
                        holder.btnPay.setVisibility(View.GONE);
                    }
                    if(orderStatus.equals("cancelled")){
                        holder.btnCancel.setVisibility(View.GONE);
                        holder.btnPay.setVisibility(View.GONE);
                    }
                    Log.i("orderStatus", "Fetched data: " + orderStatus);
                } else {
                    Log.e("OrderAdapter", "Error fetching order details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReviewStatusResponse> call, Throwable t) {
                Log.e("OrderAdapter", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderCode, orderDate, orderName, orderAddress, orderState;
        Button btnOrderDetails,btnPay,btnCancel;
        RecyclerView orderDetailRecyclerview;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderCode = itemView.findViewById(R.id.order_code);
            orderDate = itemView.findViewById(R.id.order_date);
            orderAddress = itemView.findViewById(R.id.order_address);
            orderState = itemView.findViewById(R.id.order_state);
            orderName = itemView.findViewById(R.id.order_nameuser);
            btnOrderDetails = itemView.findViewById(R.id.btn_order_details);
            orderDetailRecyclerview = itemView.findViewById(R.id.order_detail_recyclerview);
            btnPay = itemView.findViewById(R.id.btn_pay);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
