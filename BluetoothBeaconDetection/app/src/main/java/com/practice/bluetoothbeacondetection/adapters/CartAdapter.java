package com.practice.bluetoothbeacondetection.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.bluetoothbeacondetection.R;
import com.practice.bluetoothbeacondetection.models.CartItems;
import com.practice.bluetoothbeacondetection.utilities.Parameters;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{

    Context context;
    RemoveItem removeItem;

    ArrayList<CartItems> items;
    public CartAdapter(Context context, ArrayList<CartItems> items, RemoveItem removeItem){
        this.context = context;
        this.items= items;
        this.removeItem = removeItem;
//        this.cartAdapterInterface = cartAdapterInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cart_item, parent, false);
        ViewHolder viewHolder = new CartAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {

        holder.itemName.setText(items.get(position).getItemName());
        holder.price.setText("$ " + String.valueOf(items.get(position).getPrice()));
        Picasso.get()
                .load(Parameters.API_URL+"/"+items.get(position).getPhoto())
                .into(holder.itemImage);
        holder.remove.setOnClickListener(view -> {
            Log.d("Remove", "Selected "+ items.get(position).get_id());
            removeItem.removeItem(items.get(position), position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    public interface RemoveItem{
        void removeItem(CartItems cartItems, int position);
    }

}
