package com.practice.bluetoothbeacondetection.ui.items;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;
import com.practice.bluetoothbeacondetection.R;
import com.practice.bluetoothbeacondetection.adapters.ItemsAdapter;
import com.practice.bluetoothbeacondetection.models.Items;
import com.practice.bluetoothbeacondetection.models.User;
import com.practice.bluetoothbeacondetection.ui.cart.CartFragment;
import com.practice.bluetoothbeacondetection.utilities.Parameters;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment implements ItemsAdapter.ItemsCartInterface {

    RecyclerView recyclerView;
    Map<String,ArrayList<Items>> hm = new HashMap<>();
    ArrayList<Items> itemsAdded = new ArrayList<>();
    /*ArrayList<Items> fullItemsArrayList = new ArrayList();
    ArrayList<Items> produceItemsArrayList = new ArrayList();
    ArrayList<Items> groceryItemsArrayList = new ArrayList();
    ArrayList<Items> lifestyleItemsArrayList = new ArrayList();*/

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    String message;
    String token;
    Button checkoutButton;
    private BeaconManager beaconManager;
    private BeaconRegion region;
    private static final String TAG = "ItemsFragment";
    HashMap<String, Integer> beaconCount = new HashMap<>();
    int count = 0;

    private User user;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    ItemsAdapter itemsAdapter;
    private Double total = 0.0;
    String itemRegion;
    public ItemsFragment() {
        // Required empty public constructor
    }

    private static final Map<String, String> PLACES_BY_BEACONS;

    // TODO: replace "<major>:<minor>" strings to match your own beacons.
    static {
        Map<String, String> placesByBeacons = new HashMap<>();
        placesByBeacons.put("15326:56751","lifestyle");
        placesByBeacons.put("41072:44931", "produce");
        placesByBeacons.put("47152:61548","grocery");
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        beaconManager = new BeaconManager(getContext());
        region = new BeaconRegion("ranged region",
                null, null, null);
        beaconCount.put("lifestyle", 0);
        beaconCount.put("produce", 0);
        beaconCount.put("grocery", 0);

        beaconManager.setRangingListener((BeaconManager.BeaconRangingListener) (region, list) -> {
            Log.d("SDA","SDA");
            if (!list.isEmpty()) {
                Log.d("Beacons ", list.toString());

                Beacon nearestBeacon=null;

                for(Beacon nBeacon:list){
                    if (PLACES_BY_BEACONS.containsKey(nBeacon.getMajor()+":"+nBeacon.getMinor())){
                        nearestBeacon=nBeacon;
                        break;
                    }
                }
//                Log.d("MEA", nearestBeacon.getProximityUUID().toString());
                String tempRegion;
                if(count<3 && nearestBeacon!=null){
                    count++;
                    tempRegion = placesNearBeacon(nearestBeacon);
                    beaconCount.put(tempRegion, beaconCount.get(tempRegion)+1);
                    Log.d("COUNT", String.valueOf(count)+ " REGION : "+tempRegion);
                }
                else if(nearestBeacon!=null){
//                    tempRegion = placesNearBeacon(nearestBeacon);
                    count = 0;
                    Map.Entry<String, Integer> maxEntry = null;
                    int compareVariable = 0;
                    String maxString = "";
                    for (Map.Entry<String, Integer> entry : beaconCount.entrySet()) {
                        Log.d("COUNNT", entry.getValue().toString() + " -- " + entry.getKey());
                        if(compareVariable == 0){
                            maxString = entry.getKey();
                            compareVariable = entry.getValue();
                        }
                        else
                        {
                            if(compareVariable < entry.getValue()){
                                maxString = entry.getKey();
                                compareVariable =entry.getValue();
                            }

                        }

                        entry.setValue(0);
                    }
                    Log.d("MaxEntry",maxString + " == " + itemRegion);

                    if (maxString!="" && !maxString.equals(itemRegion)){

                        itemRegion = maxString;

                        getActivity().runOnUiThread(() -> {
                            itemsAdapter = new ItemsAdapter(getContext(), hm.get(itemRegion), ItemsFragment.this::addToCart);
                            recyclerView.setAdapter(itemsAdapter);
                            Toast.makeText(getContext(),itemRegion, Toast.LENGTH_LONG).show();
                        });
                    }
                    else if (maxString == "" && itemRegion != null){
//                    else if(maxEntry.getKey()==null && itemRegion!=null){
                        itemRegion=null;
                        getActivity().runOnUiThread(() -> {
                            itemsAdapter = new ItemsAdapter(getContext(), hm.get("grocery"), ItemsFragment.this::addToCart);
                            recyclerView.setAdapter(itemsAdapter);
                            Toast.makeText(getContext(),"All Products", Toast.LENGTH_LONG).show();
                        });
                    }


                }
//                String tempRegion = placesNearBeacon(nearestBeacon);
//                Log.d("Airport", "MeasuredPower: " + count + " --- " +nearestBeacon.getMeasuredPower() + ", list size: " + list.size() + "--" + tempRegion);
            }
        });

        token = getArguments().getString(Parameters.TOKEN);
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        recyclerView = view.findViewById(R.id.fragment_items_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //fullItemsArrayList = getData(Parameters.API_URL+"/item/getItems", view);
        //produceItemsArrayList = getData(Parameters.API_URL+"/item/getItemsRegion?region=produce", view);
        //groceryItemsArrayList = getData(Parameters.API_URL+"/item/getItemsRegion?region=grocery", view);
        //lifestyleItemsArrayList = getData(Parameters.API_URL+"/item/getItemsRegion?region=lifestyle", view);
        hm.put("all",getData(Parameters.API_URL+"/item/getItems", view));
        hm.put("produce",getData(Parameters.API_URL+"/item/getItemsRegion?region=produce", view));
        hm.put("grocery",getData(Parameters.API_URL+"/item/getItemsRegion?region=grocery", view));
        hm.put("lifestyle",getData(Parameters.API_URL+"/item/getItemsRegion?region=lifestyle", view));
        checkoutButton = view.findViewById(R.id.fragment_items_checkout);
        checkoutButton.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Parameters.ITEM_LIST, itemsAdded);
            bundle.putString(Parameters.TOKEN, token);
            bundle.putSerializable(Parameters.USER_ID, user);
            CartFragment fragment = new CartFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment).addToBackStack(null);
            fragmentTransaction.commit();
        });

        return view;
    }

    public ArrayList<Items> getData(String url, View view){
        OkHttpClient client = new OkHttpClient();
        ArrayList<Items> itemsArrayList = new ArrayList();
        final String[] responseString = new String[1];

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                responseString[0] = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(responseString[0]);
                    Iterator<String> keys = jsonObject.keys();

                    while(keys.hasNext()){

                        String key= keys.next();
                        JSONObject single_item = jsonObject.getJSONObject(key);
                        Double discountedPrice = single_item.getDouble(Parameters.ITEMS_ITEM_PRICE);

                        if (single_item.getDouble(Parameters.ITEMS_ITEM_DISCOUNT)>0)
                            discountedPrice = single_item.getDouble(Parameters.ITEMS_ITEM_PRICE) - ( single_item.getDouble(Parameters.ITEMS_ITEM_PRICE)* single_item.getDouble(Parameters.ITEMS_ITEM_DISCOUNT)/100);

                        Items items1 = new Items(
                                single_item.getString(Parameters.ITEMS_ITEM_NAME),
                                single_item.getString(Parameters.ITEMS_ITEM_REGION),
                                single_item.getString(Parameters.ITEMS_ITEM_ID),
                                Double.valueOf(df2.format(discountedPrice)),
                                single_item.getDouble(Parameters.ITEMS_ITEM_PRICE),
                                single_item.getString(Parameters.ITEMS_ITEM_PHOTO)
                        );
                        itemsArrayList.add(items1);

                    }
                    Comparator<Items> compareByRegion = (Items o1, Items o2) -> o1.getRegion().compareTo( o2.getRegion() );
                    Collections.sort(itemsArrayList, compareByRegion);
                    getActivity().runOnUiThread(() -> {
                        if (itemRegion!=null){
                            Toast.makeText(getContext(),itemRegion, Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getContext(),"All Products", Toast.LENGTH_LONG).show();
                        }
                        itemsAdapter = new ItemsAdapter(getContext(), hm.get("all"), ItemsFragment.this::addToCart);
                        recyclerView.setAdapter(itemsAdapter);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return itemsArrayList;

    }

    @Override
    public void addToCart(Items items) {
        itemsAdded.add(items);
        total+=items.getPrice();
        String url = Parameters.API_URL+"/user/addItem";
        String json = items.toString();
        Log.d("items11", json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.put("discountPrice", 1);
            post(url, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void post(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization",Parameters.BEARER + " " + token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String responseString = responseBody.string();

                    try {
                        JSONObject json = new JSONObject(responseString);
                        message = (String) json.get(Parameters.MESSAGE);
                        Log.d("RESPONSE", message);
                        if (message.equalsIgnoreCase("Cart updated successfully!!")){
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show());
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(getActivity());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    public void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    private String placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        String UUID = beacon.getProximityUUID().toString();
        Log.v(TAG,"Beacon Major: "+beaconKey+ ", UUID: "+UUID);
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return null;
    }

}

