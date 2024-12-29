package com.example.bookmanager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookmanager.R;
import com.example.bookmanager.model.User;
import com.example.bookmanager.network.ApiService;
import com.example.bookmanager.network.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextUsername = view.findViewById(R.id.eUsername);
        editTextPassword = view.findViewById(R.id.ePassword);
        buttonLogin = view.findViewById(R.id.btnLogin);
        buttonRegister = view.findViewById(R.id.btnRegister);

//        buttonLogin.setOnClickListener(v -> {
//            String username = editTextUsername.getText().toString();
//            String password = editTextPassword.getText().toString();
//            User user = FileHelper.login(getActivity(), username, password);
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
//            if (user!=null) {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean("is_logged_in", true);
//                editor.putInt("user_id", user.getId());
//                editor.commit();
//                boolean updatedTest = sharedPreferences.getBoolean("is_logged_in", false);
//                if (updatedTest) {
//                    Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
////                    Bundle bundle = new Bundle();
////                    bundle.putString("fullName", fullName);
//                    AccountFragment accountFragment = new AccountFragment();
////                    accountFragment.setArguments(bundle);
//                    getActivity().getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.frameLayout, accountFragment)
//                            .commit();
//                }
//            } else {
//                Toast.makeText(getActivity(), "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
//            }
//        });
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<User> call = apiService.login(username, password);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        String jsonData = new Gson().toJson(response.body());
                        Log.d("API_Body_User", "Response data: " + jsonData);
                        Log.i("TAG_USER",""+user);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("is_logged_in", true);
                        Log.i("TagUserId",""+user.getId());
                        editor.putInt("user_id", user.getId());
                        editor.commit();
                        int userId = sharedPreferences.getInt("user_id", -1);
                        Log.i("TagUser",""+userId);
                        Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        AccountFragment accountFragment = new AccountFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameLayout, accountFragment)
                                .commit();
                    } else {
                        Log.i("TAG_USER","Fail");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    System.out.println("Error: " + t.getMessage());
                }
            });

        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, new RegisterFragment())
                        .commit();
            }
        });
        return view;
    }
}