package com.practice.bluetoothbeacondetection;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.practice.bluetoothbeacondetection.utilities.Parameters;
import com.practice.bluetoothbeacondetection.utilities.TextValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_signUpTextView)
    TextView signUpTextView;
    private static final String TAG = "MainActivity";
    @BindView(R.id.main_usernameEditText)
    EditText usernameEditText;
    @BindView(R.id.main_passwordEditText)
    EditText passwordEditText;
    private String message;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkInternetPermissionGranted();

        usernameEditText.addTextChangedListener(new TextValidator(usernameEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        passwordEditText.addTextChangedListener(new TextValidator(passwordEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        signUpTextView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @OnClick(R.id.main_loginButton)
    public void submit(View view){
        if (Parameters.EMPTY.equalsIgnoreCase(usernameEditText.getText().toString()))
            usernameEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(passwordEditText.getText().toString()))
            passwordEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            JSONObject object = new JSONObject();
            try {
                object.put(Parameters.USERNAME, username);
                object.put(Parameters.PASSWORD, password);
                String json = object.toString();
                post(Parameters.API_URL+"/auth/signin",json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void checkInternetPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString(Parameters.TOKEN, "");
        if (token!=null && !Parameters.EMPTY.equalsIgnoreCase(token)){
            updateUI();
        }else {
            token = null;
        }
    }

    public void post(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
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
                    String responseString = responseBody.string();
                    Log.v(TAG,responseString);
                    try {
                        JSONObject json = new JSONObject(responseString);
                        token = (String) json.get(Parameters.TOKEN);
                        message = (String) json.get(Parameters.MESSAGE);
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show());
                        if (token != null) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Parameters.TOKEN, token);
                            editor.apply();
                            updateUI();
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void updateUI(){
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
