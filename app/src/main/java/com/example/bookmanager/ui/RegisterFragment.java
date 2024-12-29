package com.example.bookmanager.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.model.User;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btnRegister;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirm;
    private EditText etFullname;
    private EditText etEmail;
    private TextView tvErrorMessage;
    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        etUsername= view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirm = view.findViewById(R.id.etConfirmPassword);
        etFullname = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnRegister =view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullname.getText().toString();
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirm.getText().toString();

                if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    tvErrorMessage.setText("Vui lòng điền đầy đủ thông tin.");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    tvErrorMessage.setText("Mật khẩu xác nhận không khớp.");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }
                ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
                Call<User> call = apiService.register(fullName,username, password,email,confirmPassword);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(view.getContext(), "Đăng Ký Thành Công", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(view.getContext(), "Đăng Ký Thất Bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, new LoginFragment())
                        .commit();
                tvErrorMessage.setVisibility(View.GONE);
            }
        });


        return view;
    }
}