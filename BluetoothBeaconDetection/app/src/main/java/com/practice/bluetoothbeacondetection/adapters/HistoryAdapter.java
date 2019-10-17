package com.practice.bluetoothbeacondetection.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.bluetoothbeacondetection.R;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_history_item, parent, false);
        HistoryAdapter.ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, price, remove;
        ImageView itemImage;
        //ImageButton remove;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.fragment_cart_item_name);
            price = itemView.findViewById(R.id.fragment_cart_item_price);
            remove = itemView.findViewById(R.id.fragment_cart_delete);
            itemImage = itemView.findViewById(R.id.fragment_cart_item_image);
        }
    }
}
