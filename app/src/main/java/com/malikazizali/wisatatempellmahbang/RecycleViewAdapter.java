package com.malikazizali.wisatatempellmahbang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private ArrayList<Model> dataPaket;
    Context context;
    OnNoteListener mOnNoteListener;


    RecycleViewAdapter(ArrayList<Model> dataPaket, Context context, OnNoteListener onNoteListener){
        this.dataPaket = dataPaket;
        this.context = context;
        this.mOnNoteListener = onNoteListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_rv,parent,false);
        return new ViewHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nama.setText(dataPaket.get(position).getNama_paket());
        holder.harga.setText(dataPaket.get(position).getHarga_paket());
        holder.deskripsi.setText(dataPaket.get(position).getDeskripsi());
        Glide.with(holder.itemView.getContext()).load(dataPaket.get(position).getGambar_paket()).into(holder.gambar);

        holder.nama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.nama.getContext(),Detail.class);
                intent.putExtra("gambar_paket",dataPaket.get(position).getGambar_paket());
                intent.putExtra("nama_paket",dataPaket.get(position).getNama_paket());
                intent.putExtra("harga_paket",dataPaket.get(position).getHarga_paket());
                intent.putExtra("deskripsi",dataPaket.get(position).getDeskripsi());
                intent.putExtra("deskripsi_lengkap", dataPaket.get(position).getDeskripsi_lengkap());
                holder.nama.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataPaket.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nama,harga,deskripsi;
        ImageView gambar;
        OnNoteListener honNoteListener;
        CardView cv;

        private OnNoteListener mOnNoteListener;
        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv_main);
            nama = itemView.findViewById(R.id.tv_namaPaket);
            harga =  itemView.findViewById(R.id.tv_hargaPaket);
            gambar =  itemView.findViewById(R.id.iv_gambarPaket);
            deskripsi = itemView.findViewById(R.id.tv_deskPaket);

            this.honNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            honNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
