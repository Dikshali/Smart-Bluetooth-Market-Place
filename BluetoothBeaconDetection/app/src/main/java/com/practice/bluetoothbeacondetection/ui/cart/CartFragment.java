package com.practice.bluetoothbeacondetection.ui.cart;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.practice.bluetoothbeacondetection.CardList;
import com.practice.bluetoothbeacondetection.MainActivity;
import com.practice.bluetoothbeacondetection.R;
import com.practice.bluetoothbeacondetection.adapters.CartAdapter;
import com.practice.bluetoothbeacondetection.models.CartItems;
import com.practice.bluetoothbeacondetection.models.User;
import com.practice.bluetoothbeacondetection.utilities.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment implements CartAdapter.RemoveItem {

    private View view;
    private String TAG = "CartFragment";
    private Button stripePayment;
    private OkHttpClient client = new OkHttpClient();
    private SharedPreferences preferences;
    private String token, userToken;
    private RecyclerView recyclerView;
    private Double total;
    private TextView price;
    private User user;
    ArrayList<CartItems> cartItems = new ArrayList<>();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    String message;

    private CartAdapter cartAdapter;
    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        userToken = getArguments().getString(Parameters.TOKEN);
        total = getArguments().getDouble(Parameters.PRICE);

        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        getData(Parameters.API_URL + "/user/profile");
        stripePayment = view.findViewById(R.id.cart_makePayment_stripe);
        recyclerView = view.findViewById(R.id.fragment_cart_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())) ;

        price = view.findViewById(R.id.fragment_cart_total_price);

        if(total>0){
            stripePayment.setEnabled(true);
            stripePayment.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            stripePayment.setClickable(true);
        }else{
            stripePayment.setEnabled(false);
            stripePayment.setBackgroundColor(Color.GRAY);
            stripePayment.setClickable(false);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preferences.getString(Parameters.TOKEN, "");

        getClientToken(Parameters.API_URL + "/user/client_token");

        stripePayment.setOnClickListener(view1 -> {
            //Intent i = new Intent(getActivity(), PaymentActivity.class);
            Intent i = new Intent(getActivity(), CardList.class);
            i.putExtra(Parameters.TOKEN, token);
            i.putExtra(Parameters.USER_ID, user);

            startActivity(i);
        });
        return view;
    }

    public void getClientToken(String url) {
        if (token != null) {
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", Parameters.BEARER + " " + token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        String responseString = responseBody.string();
                        try {
                            JSONObject json = new JSONObject(responseString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        } else {
            logout();
        }
    }

    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        onDestroy();
    }


    public void post(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization",Parameters.BEARER + " " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.v(TAG,responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    int responseStatus = response.code();
                    String responseString = responseBody.string();
                    Log.v(TAG,String.valueOf(responseStatus));
                    try {
                        JSONObject json = new JSONObject(responseString);

                        message = (String) json.get(Parameters.MESSAGE);
                        Log.d("PAYMENT-RESPONSE", message);
                        if (responseStatus == 500){
                            getActivity().runOnUiThread(()->{
                                Toast.makeText(getContext(), "Payment Failed", Toast.LENGTH_LONG).show();
                            });
                        }
                        else if(message.equals(Parameters.PAYMENT_SUCCESSFUL)){
                            cartItems.clear();
                            getActivity().runOnUiThread(()->{
                                cartAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                price.setText("Total : $ " + 0.0);
                                stripePayment.setEnabled(false);
                                stripePayment.setBackgroundColor(Color.GRAY);
                                stripePayment.setClickable(false);
                            });


                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    public void getData(String url){

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization",Parameters.BEARER + " " + userToken)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        String responseString = responseBody.string();
                        Log.v(TAG, responseString);

                        try {
                            JSONObject json = new JSONObject(responseString);
                            JSONObject currentItems = json.getJSONObject(Parameters.CURRENT_TRANSACTION);
//                            Log.d("JSON", currentItems.toString());
                            total = currentItems.getDouble("totalAmount");
                            cartItems.clear();
                            JSONArray jsonArray = currentItems.getJSONArray(Parameters.CART_ITEMS);
                            for (int i =0;i<jsonArray.length();i++){
                                JSONObject single_item = jsonArray.getJSONObject(i);
                                Log.d("JSON", single_item.toString());
                                CartItems items = new CartItems(
                                        single_item.getString(Parameters.ITEMS_ITEM_NAME),
                                        single_item.getString(Parameters.ITEMS_ITEM_REGION),
                                        single_item.getString(Parameters.ITEMS_ITEM_ID),
                                        single_item.getString(Parameters.ITEMS_ITEM_PHOTO),
                                        single_item.getDouble(Parameters.ITEMS_ITEM_PRICE),
                                        single_item.getDouble(Parameters.ITEMS_ITEM_DISCOUNT),
                                        single_item.getInt(Parameters.DISCOUNT_PRICE)
                                );
                                cartItems.add(items);
                            }
                            getActivity().runOnUiThread(() ->{
                                cartAdapter = new CartAdapter(getContext(), cartItems, CartFragment.this::removeItem);
                                recyclerView.setAdapter(cartAdapter);
                                price.setText("Total : $ " + String.valueOf(total));
                                if(total>0){
                                    stripePayment.setEnabled(true);
                                    stripePayment.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    stripePayment.setClickable(true);
                                }else{
                                    stripePayment.setEnabled(false);
                                    stripePayment.setBackgroundColor(Color.GRAY);
                                    stripePayment.setClickable(false);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }


    @Override
    public void removeItem(CartItems item, int position) {
        String url = Parameters.API_URL + "/user/deleteItem";
        JSONObject jsonObject = new JSONObject();

        cartItems.remove(item);
        total -=item.getPrice();
        price.setText("Total : $ " + String.valueOf(total));
        cartAdapter.notifyDataSetChanged();
        try {
            jsonObject.put("id", item.get_id());
            jsonObject.put(Parameters.DISCOUNT_PRICE, item.getPrice());
            post(url, jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
