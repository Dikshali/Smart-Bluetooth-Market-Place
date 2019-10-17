package com.practice.bluetoothbeacondetection.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.bluetoothbeacondetection.R;
import com.practice.bluetoothbeacondetection.models.Items;
import com.practice.bluetoothbeacondetection.utilities.Parameters;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private static final String TAG = "ItemsAdapter";
    Context context;
    ArrayList<Items> items;
    ItemsCartInterface itemsCartInterface;

    public ItemsAdapter(Context context, ArrayList<Items> items, ItemsCartInterface itemsCartInterface){
        this.context = context;
        this.items= items;
        this.itemsCartInterface = itemsCartInterface;
    }
    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_items_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {

        holder.itemName.setText(items.get(position).getItemName());
        holder.price.setText("$ " + String.valueOf(items.get(position).getPrice()));
        holder.price.setPaintFlags(holder.price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        holder.discountPrice.setText("$ "+String.valueOf(items.get(position).getDiscount()));
        Picasso.get()
                .load(Parameters.API_URL+"/"+items.get(position).getPhoto())
                .into(holder.itemImage);
        holder.addToCart.setOnClickListener(view -> {
            itemsCartInterface.addToCart(items.get(position));
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, price, discountPrice;
        ImageView itemImage;
        Button addToCart;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.fragment_items_item_name);
            price = itemView.findViewById(R.id.fragment_items_item_price);
            addToCart = itemView.findViewById(R.id.fragment_items_item_add_to_cart);
            itemImage = itemView.findViewById(R.id.fragment_items_item_image);
            discountPrice = itemView.findViewById(R.id.fragment_items_item_discount_price);
        }
    }

    public interface ItemsCartInterface{
        void addToCart(Items items);
    }
}
