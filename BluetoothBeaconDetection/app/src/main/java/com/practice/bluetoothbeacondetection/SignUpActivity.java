package com.practice.bluetoothbeacondetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.practice.bluetoothbeacondetection.models.User;
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

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private static int SELECT_PICTURE = 1;

    @BindView(R.id.signUp_firstNameEditText)
    EditText firstNameEditText;
    @BindView(R.id.signUp_lastNameEditText)
    EditText lastNameEditText;
    @BindView(R.id.signUp_emailEditText)
    EditText emailIdEditText;
    @BindView(R.id.signUp_passwordEditText)
    EditText passwordEditText;
    @BindView(R.id.signUp_confirmPasswordEditText)
    EditText confirmPasswordEditText;
    @BindView(R.id.signUp_cityEditText)
    EditText cityEditText;
    @BindView(R.id.signUp_genderRadioGroup)
    RadioGroup genderRadioGroup;
    @BindView(R.id.signUp_usernameEditText)
    EditText usernameEditText;

    private String message;
    private User user;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        emailIdEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        cityEditText.setText("");

        firstNameEditText.addTextChangedListener(new TextValidator(firstNameEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        lastNameEditText.addTextChangedListener(new TextValidator(lastNameEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        emailIdEditText.addTextChangedListener(new TextValidator(emailIdEditText) {
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

        confirmPasswordEditText.addTextChangedListener(new TextValidator(confirmPasswordEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
                else if (!passwordEditText.getText().toString().equals(text))
                    textView.setError(Parameters.INCORRECT_CONFIRM_PASSWORD);
            }
        });

        usernameEditText.addTextChangedListener(new TextValidator(usernameEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        cityEditText.addTextChangedListener(new TextValidator(cityEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

    }

    @OnClick(R.id.signUp_signUpButton)
    public void click(View view) {

        if (Parameters.EMPTY.equalsIgnoreCase(firstNameEditText.getText().toString()))
            firstNameEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(lastNameEditText.getText().toString()))
            lastNameEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(emailIdEditText.getText().toString()))
            emailIdEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(usernameEditText.getText().toString()))
            usernameEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(passwordEditText.getText().toString()))
            passwordEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (Parameters.EMPTY.equalsIgnoreCase(confirmPasswordEditText.getText().toString()))
            confirmPasswordEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString()))
            confirmPasswordEditText.setError(Parameters.INCORRECT_CONFIRM_PASSWORD);
        else if (Parameters.EMPTY.equalsIgnoreCase(cityEditText.getText().toString()))
            cityEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
        else {
            int checkedRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(checkedRadioButtonId);

            user = new User(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(),
                    emailIdEditText.getText().toString(), usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    cityEditText.getText().toString(), radioButton.getText().toString());

            String jsonString = gson.toJson(user);
            Log.v(TAG, jsonString);
            post(Parameters.API_URL+"/auth/signup", jsonString);
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
                        String token = (String) json.get(Parameters.TOKEN);
                        message = (String) json.get(Parameters.MESSAGE);
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show());
                        if (token != null) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Parameters.TOKEN, token);
                            editor.apply();
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }

}
