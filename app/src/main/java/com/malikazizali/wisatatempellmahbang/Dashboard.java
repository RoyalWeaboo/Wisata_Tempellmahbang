package com.malikazizali.wisatatempellmahbang;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.malikazizali.wisatatempellmahbang.api.ServerApi;

public class Dashboard extends AppCompatActivity implements RecycleViewAdapter.OnNoteListener {

    private static final int REQUEST_CALL = 1;

    RecyclerView recyclerView;
    ArrayList<Model> dataPaket = new ArrayList<>();
    RecycleViewAdapter recycleViewAdapter;

    int total_harga_int;
    LinearLayout checkout;

    Dialog dialog;
    TextView total_harga, textView_total, totalHarga_dialog, totalKembalian_dialog, textView_kembalian;
    EditText payment_dialog;
    Button btn_bayar, btn_cancel, btn_hitung;
    String biaya, logged_user, st;
    FloatingActionButton fab;
    SharedPreferences sp,sph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        checkout = findViewById(R.id.checkout);
        total_harga = findViewById(R.id.total_harga);
        recyclerView = findViewById(R.id.rv_main);
        recycleViewAdapter = new RecycleViewAdapter(dataPaket, Dashboard.this, this);
        recyclerView.setAdapter(recycleViewAdapter);
        textView_total = findViewById(R.id.tv_total);
        sp = getSharedPreferences("totalBiaya", MODE_PRIVATE);
        sph = getSharedPreferences("saveHistory", MODE_PRIVATE);
        st = sp.getString("savedTotal", "");
        total_harga.setText(st);
        fab = findViewById(R.id.fab_reset);
        GetData();

        checkout = findViewById(R.id.checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (total_harga_int == 0) {
                    Toast.makeText(Dashboard.this, "Anda belum memilih paket apapun", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();
                    totalHarga_dialog.setVisibility(View.VISIBLE);
                    totalHarga_dialog.setText("Rp. " + total_harga_int);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_total.setVisibility(View.VISIBLE);
                total_harga_int = 0;
                biaya = "0";
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("savedTotal","0");
                editor.apply();
                total_harga.setText("0");
            }
        });

        dialog = new Dialog(Dashboard.this);
        dialog.setContentView(R.layout.checkout_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        btn_bayar = dialog.findViewById(R.id.dialog_button_bayar);
        btn_cancel = dialog.findViewById(R.id.dialog_button_cancel);
        totalHarga_dialog = dialog.findViewById(R.id.total_harga_dialog);
        payment_dialog = dialog.findViewById(R.id.et_pembayaran);
        totalKembalian_dialog = dialog.findViewById(R.id.totalKembalian);
        btn_hitung = dialog.findViewById(R.id.button_hitung);
        textView_kembalian = dialog.findViewById(R.id.kembalian);
        btn_hitung.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                int uang_pembayaran_integer = Integer.parseInt(payment_dialog.getText().toString());
                int tampung_uang_dialog = uang_pembayaran_integer - total_harga_int;
                int uang_kurang = total_harga_int - uang_pembayaran_integer;
                if (uang_pembayaran_integer >= total_harga_int) {
                    textView_kembalian.setEnabled(true);
                    totalKembalian_dialog.setVisibility(View.VISIBLE);
                    totalKembalian_dialog.setText("Rp. " + Integer.toString(tampung_uang_dialog));

                    totalKembalian_dialog.setTextColor(Color.parseColor("#1FA324"));
                    btn_bayar.setEnabled(true);
                    totalKembalian_dialog.setTextSize(24);
                    textView_kembalian.setVisibility(View.VISIBLE);
                } else {
                    textView_kembalian.setEnabled(true);
                    totalKembalian_dialog.setVisibility(View.VISIBLE);
                    totalKembalian_dialog.setText("Pembayaran anda kurang Rp. " + Integer.toString(uang_kurang));

                    totalKembalian_dialog.setTextColor(Color.parseColor("#D50A0A"));
                    btn_bayar.setEnabled(false);
                    totalKembalian_dialog.setTextSize(24);
                    textView_kembalian.setVisibility(View.GONE);
                }
            }
        });
        btn_bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total_harga_int = 0;
                total_harga.setText("Silakan Upload Bukti Pembayaran");
                totalKembalian_dialog.setText("");
                totalKembalian_dialog.setVisibility(View.GONE);
                textView_kembalian.setVisibility(View.GONE);
                totalHarga_dialog.setVisibility(View.GONE);
                textView_total.setVisibility(View.GONE);
                btn_bayar.setEnabled(false);
                payment_dialog.setText("");
                dialog.dismiss();
                saveHistory();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Dashboard.this, "Pembayaran dibatalkan", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }


    private void GetData() {
        String URL = ServerApi.URL_Fetch;
        RequestQueue queue = Volley.newRequestQueue(Dashboard.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray hasil = response.getJSONArray("result");
                    for (int i = 0; i < hasil.length(); i++) {
                        JSONObject jsonObject = hasil.getJSONObject(i);
                        dataPaket.add(new Model(
                                jsonObject.getString("kode_paket"),
                                jsonObject.getString("nama_paket"),
                                jsonObject.getString("harga_paket"),
                                jsonObject.getString("gambar_paket"),
                                jsonObject.getString("deskripsi"),
                                jsonObject.getString("deskripsi_lengkap")
                        ));
                        recyclerView.setLayoutManager(new GridLayoutManager(Dashboard.this, 1));
                        recyclerView.setAdapter(recycleViewAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Dashboard.this, "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.telepon:
                makephonecall();
                return true;
            case R.id.sms:
                String num = "smsto:081234567890";
                Uri sendto = Uri.parse(num);
                Intent sms = new Intent(Intent.ACTION_SENDTO, sendto);
                sms.setData(Uri.parse(num));
                sms.putExtra("sms_body", "Tulis Pesan Anda Kepada Kami");
                startActivity(sms);
                return true;
            case R.id.loc:
                Uri addressUri =
                        Uri.parse("https://www.google.com/maps/place/Universitas+Dian+Nuswantoro,+J l.+Imam+Bonjol+No.207,+Pendrikan+Kidul,+Kec.+Semarang+Tengah,+Kota+Semarang,+Jawa+Tengah+50131/@- 6.9828663,110.4090967,17z/data=!4m2!3m1!1s0x2e708b4ec52229d7:0xc791d6abc923 6c7");
                Intent goToMap = new Intent(Intent.ACTION_VIEW,
                        addressUri);
                startActivity(goToMap);
                return true;
            case R.id.updateLogin:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if(user!=null){
                    Toast.makeText(this, "Anda Login Aplikasi Menggunakan Google/Facebook", Toast.LENGTH_SHORT).show();
                }
                else{
                    logged_user = getIntent().getStringExtra("username");
                    Intent update = new Intent(this, UpdatePassword.class);
                    update.putExtra("username", logged_user);
                    startActivity(update);
                }
                return true;
            case R.id.history:
                startActivity(new Intent(this, History.class));
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent so = new Intent(Dashboard.this, Login.class);
                startActivity(so);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makephonecall() {
        if (ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:081234567890")));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makephonecall();
            }
        } else {
            Toast.makeText(this, "Permission Denied",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                GetData();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                GetData();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveHistory() {
        String biaya_akhir = sp.getString("savedTotal", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerApi.URL_Save_History, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    Toast.makeText(Dashboard.this, "Transaksi Tersimpan di History, Silahkan Upload Bukti Pembayaran", Toast.LENGTH_SHORT).show();
                    Toast.makeText(Dashboard.this, "Klik Transaksi di History Untuk Mengupload Bukti Pembayaran", Toast.LENGTH_LONG).show();
                } else if (response.equals("failure")) {
                    Toast.makeText(Dashboard.this, "Gagal Menyimpan Transaksi", Toast.LENGTH_SHORT).show();
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
                data.put("total_history", biaya_akhir);
                data.put("status_history", "Belum Lunas");
                Log.i("sending ", data.toString());
                return data;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onNoteClick(int position) {
        int i = Integer.parseInt(String.valueOf(dataPaket.get(position).getHarga_paket()));
        if (st.equals("0")) {
            total_harga_int = total_harga_int + i;
            String biaya_temp = String.valueOf(total_harga_int);
            total_harga.setText(biaya_temp);

            SharedPreferences.Editor editor = sp.edit();

            editor.putString("savedTotal", biaya_temp);
            editor.apply();
        }
        else {
            String temp = sp.getString("savedTotal", "");
            total_harga_int = Integer.parseInt(temp);
            total_harga_int = total_harga_int + i;
            String biaya_temp = String.valueOf(total_harga_int);
            total_harga.setText(biaya_temp);

            SharedPreferences.Editor editor = sp.edit();

            editor.putString("savedTotal", biaya_temp);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
    }
}