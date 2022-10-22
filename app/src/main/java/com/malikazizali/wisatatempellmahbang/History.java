package com.malikazizali.wisatatempellmahbang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malikazizali.wisatatempellmahbang.api.ServerApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class History extends AppCompatActivity implements RecycleViewAdapterHistory.OnNoteListener {

    RecyclerView recyclerView;
    ArrayList<ModelHistory> dataHistory = new ArrayList<>();
    RecycleViewAdapterHistory recycleViewAdapterHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.rv_history);
        recycleViewAdapterHistory = new RecycleViewAdapterHistory(dataHistory,History.this,this);
        recyclerView.setAdapter(recycleViewAdapterHistory);
        GetHistory();
    }

    private void GetHistory(){
        String URL = ServerApi.URL_Fetch_History;
        RequestQueue queue = Volley.newRequestQueue(History.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray hasil = response.getJSONArray("history");
                    for (int i = 0; i < hasil.length(); i++) {
                        JSONObject jsonObject = hasil.getJSONObject(i);
                        dataHistory.add(new ModelHistory(
                                jsonObject.getString("id"),
                                jsonObject.getString("total"),
                                jsonObject.getString("status")
                        ));
                        recyclerView.setLayoutManager(new GridLayoutManager(History.this,1));
                        recyclerView.setAdapter(recycleViewAdapterHistory);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(History.this, "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onNoteClick(int position) {
        String id = dataHistory.get(position).getId();
        String total = dataHistory.get(position).getTotal();
        String status = dataHistory.get(position).getStatus();
        Intent bukti = new Intent(this, BuktiPembayaran.class);
        bukti.putExtra("id", id);
        bukti.putExtra("status", status);
        bukti.putExtra("total_biaya", total);
        startActivity(bukti);
    }
}