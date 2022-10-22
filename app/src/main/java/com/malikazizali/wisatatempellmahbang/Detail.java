package com.malikazizali.wisatatempellmahbang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Detail extends AppCompatActivity {

    ImageView gambarPaket;
    TextView namaPaket, hargaPaket, deskPaket, deskPaketLengkap;
    String idPaket, url, namaPkt, hargaPkt, deskPkt, deskPktLngkp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        gambarPaket = findViewById(R.id.gambarPaket);
        namaPaket = findViewById(R.id.namaPaket);
        hargaPaket = findViewById(R.id.hargaPaket);
        deskPaket = findViewById(R.id.deskPaket);
        deskPaketLengkap = findViewById(R.id.deskPaketLengkap);

        getIntentData();
    }

    void getIntentData() {
        url = getIntent().getStringExtra("gambar_paket");
        namaPkt = getIntent().getStringExtra("nama_paket");
        hargaPkt = getIntent().getStringExtra("harga_paket");
        deskPkt = getIntent().getStringExtra("deskripsi");
        deskPktLngkp = getIntent().getStringExtra("deskripsi_lengkap");

        Glide.with(this).load(url).into(gambarPaket);
        namaPaket.setText(namaPkt);
        hargaPaket.setText(hargaPkt);
        deskPaket.setText(deskPkt);
        deskPaketLengkap.setText(deskPktLngkp);
    }
}