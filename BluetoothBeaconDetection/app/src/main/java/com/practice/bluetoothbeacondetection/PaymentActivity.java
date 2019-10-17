package com.practice.bluetoothbeacondetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.practice.bluetoothbeacondetection.models.User;
import com.practice.bluetoothbeacondetection.utilities.Parameters;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.stripe.android.view.PaymentMethodsActivityStarter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PaymentActivity extends AppCompatActivity {

    private Stripe stripe;
    private OkHttpClient client = new OkHttpClient();
    private String token;
    private Card card;
    private User user;
    private String TAG = "PaymentActivity";
    private Button paymentButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setTitle("Add New Card");
        token = getIntent().getExtras().getString(Parameters.TOKEN);
        user = (User) getIntent().getExtras().getSerializable(Parameters.USER_ID);
        PaymentConfiguration.init(getApplicationContext(), "pk_test_XEnZDjQFcIvAelNPKlRcaOJ100EcD66wp4");
        stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
        CardMultilineWidget cardWidget = findViewById(R.id.card_widget);

        paymentButton = findViewById(R.id.card_payment);

        paymentButton.setOnClickListener(view -> {
            card = cardWidget.getCard();
            card = card.toBuilder().customer(user.getCustomerId()).build();
            if(card!=null){
                Log.d("CUstomer", card.getCustomerId());
                tokenizeCard(card);
            }
        });

        JSONObject api = new JSONObject();
        try {
            api.put("api_version", "2019-09-09");
            post(Parameters.API_URL + "/user/client_token",api.toString() );
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void post(String url, String json) {
        RequestBody body = RequestBody.create(json, MainActivity.JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", Parameters.BEARER + " " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);


                }
            }
        });
    }

    private void launchPaymentMethodsActivity() {
        new PaymentMethodsActivityStarter(this).startForResult();
    }
    private void tokenizeCard(@NonNull Card card) {
        stripe.createToken(
                card,
                new ApiResultCallback<Token>() {
                    public void onSuccess(@NonNull Token token) {
                        // send token ID to your server, you'll create a charge next
                        Log.d("Payment", "Successful  "+ token);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(Parameters.CARD_TOKEN, token.getId());
                            postForPayment(Parameters.API_URL+"/user/createCard", jsonObject.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        Log.d("Payment", "Error");
                    }
                }
        );
    }





    public void postForPayment(String url, String json) {
        RequestBody body = RequestBody.create(json, MainActivity.JSON);
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
                    Intent i = new Intent(PaymentActivity.this, CardList.class);
                    i.putExtra(Parameters.TOKEN, token);
                    i.putExtra(Parameters.USER_ID, user);
                    startActivity(i);
                    finish();
                }
            }
        });
    }


}

