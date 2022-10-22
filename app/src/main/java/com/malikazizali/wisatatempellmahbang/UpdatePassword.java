package com.malikazizali.wisatatempellmahbang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.malikazizali.wisatatempellmahbang.api.ServerApi;

import java.util.HashMap;
import java.util.Map;

public class UpdatePassword extends AppCompatActivity {

    Button update;
    TextView username;
    EditText newPass;
    String passBaru, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        username = findViewById(R.id.tvUsernameUpdate);
        user = getIntent().getStringExtra("username");
        username.setText(user);

        newPass = findViewById(R.id.etNewPassword);

        update = findViewById(R.id.btn_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passBaru = newPass.getText().toString().trim();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerApi.URL_Update_Pass, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            Toast.makeText(UpdatePassword.this, "Berhasil update Password", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UpdatePassword.this, Dashboard.class));
                        } else if (response.equals("failure")) {
                            Toast.makeText(UpdatePassword.this, "Gagal Update", Toast.LENGTH_SHORT).show();
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
                        data.put("username", user);
                        data.put("password", passBaru);
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        });
    }
}