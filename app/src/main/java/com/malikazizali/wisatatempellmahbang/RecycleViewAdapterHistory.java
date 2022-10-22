package com.malikazizali.wisatatempellmahbang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleViewAdapterHistory extends RecyclerView.Adapter<RecycleViewAdapterHistory.ViewHolder> {
    Context context;
    RecycleViewAdapterHistory.OnNoteListener mOnNoteListener;
    private ArrayList<ModelHistory> dataHistory;

    RecycleViewAdapterHistory(ArrayList<ModelHistory> dataHistory, Context context, RecycleViewAdapterHistory.OnNoteListener onNoteListener) {
        this.dataHistory = dataHistory;
        this.context = context;
        this.mOnNoteListener = onNoteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_rv_history, parent, false);
        return new RecycleViewAdapterHistory.ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapterHistory.ViewHolder holder, int position) {
        holder.kode.setText(dataHistory.get(position).getId());
        holder.total.setText(dataHistory.get(position).getTotal());
        holder.status.setText(dataHistory.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return dataHistory.size();
    }

    public interface OnNoteListener {
        void onNoteClick(int position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnNoteListener onNoteListener;
        TextView kode,total,status;

        public ViewHolder(@NonNull View itemView, RecycleViewAdapterHistory.OnNoteListener onNoteListener) {
            super(itemView);
            kode = itemView.findViewById(R.id.kode_history);
            total = itemView.findViewById(R.id.total_history);
            status = itemView.findViewById(R.id.status);

            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }
}
