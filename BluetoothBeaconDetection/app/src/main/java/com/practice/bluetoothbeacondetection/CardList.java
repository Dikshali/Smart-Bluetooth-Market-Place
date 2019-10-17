package com.practice.bluetoothbeacondetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.practice.bluetoothbeacondetection.models.User;
import com.practice.bluetoothbeacondetection.utilities.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CardList extends AppCompatActivity {

    private String TAG = "CardList";
    private ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private String token;
    private User user;
    private ListView cardList;
    private Button addNewCard;
    private OkHttpClient client = new OkHttpClient();
    private JSONArray jsonArray;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        setTitle("Cards");
        cardList = findViewById(R.id.card_list_pma);
        addNewCard = findViewById(R.id.card_list_new_card);
        token = getIntent().getExtras().getString(Parameters.TOKEN);
        user = (User) getIntent().getExtras().getSerializable(Parameters.USER_ID);
        getAllCards(Parameters.API_URL + "/user/listAllCards");
        String[] from={"last4digit","tokenId"};//string array
        int[] to={R.id.last4,R.id.tokenId};//int array of views id's
        simpleAdapter=new SimpleAdapter(CardList.this,arrayList,R.layout.list_item_layout,from,to);//Create object and set the parameters for simpleAdapter
        cardList.setAdapter(simpleAdapter);
        addNewCard.setOnClickListener(view -> {
            Intent i = new Intent(CardList.this, PaymentActivity.class);
            i.putExtra(Parameters.TOKEN, token);
            i.putExtra(Parameters.USER_ID, user);
            startActivity(i);
        });
        cardList.setOnItemClickListener((adapterView, view, i, l) -> {
            JSONObject jo = new JSONObject();
            try {
                JSONObject jobj = jsonArray.getJSONObject(i);
                jo.put(Parameters.PAYMENT_METHOD_NONCE, jobj.getString("id"));
                //jsonObject.put(Parameters.PRICE, total);
                post(Parameters.API_URL+"/user/checkout", jo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

    }

    public void getAllCards(String url ){
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", Parameters.BEARER + " " + token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {

                    Log.d(TAG, String.valueOf(response.code()));
                    JSONArray j = new JSONArray();
                    if(response.code() == 500){
                        Intent i = new Intent(CardList.this, PaymentActivity.class);
                        i.putExtra(Parameters.TOKEN, token);
                        i.putExtra(Parameters.USER_ID, user);
                        startActivity(i);
                    }else if(response.code() == 200){
                        JSONObject json = new JSONObject(responseBody.string()).getJSONObject("cards");
                        jsonArray = json.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair
                            hashMap.put("last4digit","**** **** **** " + obj.getString("last4"));
                            hashMap.put("tokenId",obj.getString("brand"));
                            arrayList.add(hashMap);
                        }
                        runOnUiThread(()->{
                            simpleAdapter.notifyDataSetChanged();
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    Log.v(TAG,responseString);
                    try {
                        JSONObject json = new JSONObject(responseString);
                        //String token = (String) json.get(Parameters.TOKEN);
                        String message = (String) json.get(Parameters.MESSAGE);
                        String invoiceUrl = (String) json.get("receipt_url");
                        //Log.d("RESPONSE", message);
                        Intent i = new Intent(CardList.this, InvoiceActivity.class);
                        i.putExtra("url", invoiceUrl);
                        i.putExtra(Parameters.TOKEN, token);
                        i.putExtra(Parameters.USER_ID, user);
                        startActivity(i);
                        finish();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
