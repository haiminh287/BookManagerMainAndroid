package com.example.bookmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanager.HistoryOrderActivity;
import com.example.bookmanager.R;
import com.example.bookmanager.adapter.ItemTakedAdapter;
import com.example.bookmanager.adapter.OrderAdapter;
import com.example.bookmanager.model.ItemTaked;
import com.example.bookmanager.model.Order;
import com.example.bookmanager.model.User;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btnLogout,btnViewHistoryOrder;
    private TextView tFullName,tEmail;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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

        View view= inflater.inflate(R.layout.fragment_account, container, false);
        btnViewHistoryOrder = view.findViewById(R.id.btn_view_order_history);
        tFullName = view.findViewById(R.id.profile_name);
        tEmail = view.findViewById(R.id.profile_email);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        Log.i("TagUser",""+userId);
        if (userId != -1) {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<User> call = apiService.getUser(userId);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        Log.i("InfoUser",""+user.getEmail());
                        tFullName.setText("Xin chào, " + user.getFullname());
                        tEmail.setText("Email : "+user.getEmail());
                    } else {
                        Toast.makeText(getActivity(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getActivity(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        btnViewHistoryOrder.setOnClickListener(v ->{
            Intent intent = new Intent(getContext(), HistoryOrderActivity.class);
            getContext().startActivity(intent);
        });
        return view;
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.commit();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new LoginFragment())
                .commit();
    }

}