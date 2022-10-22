package com.malikazizali.wisatatempellmahbang;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import com.malikazizali.wisatatempellmahbang.api.ServerApi;

public class Register extends AppCompatActivity {
    private EditText etNamaLengkap, etUsername, etPassword, etConfirmPassword;
    private TextView tvStatus, tvLogin;
    private Button btnRegister;
    private String namaLengkap, username, password, confirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etNamaLengkap = findViewById(R.id.etNamaLengkap);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvStatus = findViewById(R.id.tvStatus);
        tvLogin = findViewById(R.id.login);
        btnRegister = findViewById(R.id.btn_register);
        namaLengkap = username = password = confirmPassword = "";
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namaLengkap = etNamaLengkap.getText().toString().trim();
                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                confirmPassword = etConfirmPassword.getText().toString().trim();
                if(!password.equals(confirmPassword)){
                    Toast.makeText(Register.this, "Password tidak sama !", Toast.LENGTH_SHORT).show();
                }
                else if(namaLengkap.equals("") && username.equals("") && password.equals("")) {
                    Toast.makeText(Register.this, "Data Tidak Boleh Kosong !", Toast.LENGTH_SHORT).show();
                }
                else if(!namaLengkap.equals("") && !username.equals("") && !password.equals("")) {
                    registerUser();
                }
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void registerUser(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerApi.URL_Register, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        tvStatus.setText("Berhasil Registrasi ! Silakan Login");
                        btnRegister.setClickable(false);
                    } else if (response.equals("failure")) {
                        tvStatus.setText("Gagal Registrasi");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("nama_lengkap", namaLengkap);
                    data.put("username", username);
                    data.put("password", password);
                    Log.i("sending ", data.toString());
                    return data;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Login.class));
    }
}