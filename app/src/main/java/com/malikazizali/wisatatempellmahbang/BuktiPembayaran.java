package com.malikazizali.wisatatempellmahbang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class BuktiPembayaran extends AppCompatActivity {
    TextView tv_id, tv_total, tv_status;
    Button btn_upload, btn_send;
    ImageView gambar;
    Uri selectedImageUri;
    String mId, mStatus, mTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bukti_pembayaran);
        tv_id = findViewById(R.id.id_pesanan);
        tv_status = findViewById(R.id.status);
        tv_total = findViewById(R.id.total_pembayaran);
        mId = getIntent().getStringExtra("id");
        mStatus = getIntent().getStringExtra("status");
        mTotal = getIntent().getStringExtra("total_biaya");
        tv_id.setText(mId);
        tv_status.setText(mStatus);
        if(mStatus.equals("Lunas")){
            tv_status.setTextColor(Color.GREEN);
        }
        else{
            tv_status.setTextColor(Color.RED);
        }
        tv_total.setText(mTotal);
        gambar = findViewById(R.id.gambar_upload);
        btn_upload = findViewById(R.id.btn_gallery);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent upload = new Intent();
                upload.setType("image/*");
                upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(upload, "Select Picture"), 200);
            }
        });
        btn_send = findViewById(R.id.btn_kirim);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImageUri == null) {
                    Toast.makeText(getApplicationContext(), "Anda Belum Mengupload Bukti Pembayaran", Toast.LENGTH_LONG).show();
                } else {
                    btn_upload.setVisibility(View.GONE);
                    btn_send.setVisibility(View.GONE);
                    String lunas = "Lunas";
                    tv_status.setText(lunas);
                    tv_status.setTextColor(Color.GREEN);
                    updateStatus(lunas);
                    startActivity(new Intent(BuktiPembayaran.this, History.class));
                }
            }
        });
    }

    private void updateStatus(String string) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerApi.URL_Update_Status, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(BuktiPembayaran.this, "Berhasil Melakukan Pembayaran", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BuktiPembayaran.this, History.class));
                } else if (response.equals("failure")) {
                    Toast.makeText(BuktiPembayaran.this, "Gagal Melakukan Pembayaran", Toast.LENGTH_SHORT).show();
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
                data.put("id", mId);
                data.put("total", mTotal);
                data.put("status", string);
                return data;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                gambar.setImageURI(selectedImageUri);
            }
        }
    }
}