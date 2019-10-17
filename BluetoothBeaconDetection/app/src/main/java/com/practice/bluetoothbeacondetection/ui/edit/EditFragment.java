package com.practice.bluetoothbeacondetection.ui.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.practice.bluetoothbeacondetection.R;
import com.practice.bluetoothbeacondetection.models.User;
import com.practice.bluetoothbeacondetection.utilities.Parameters;
import com.practice.bluetoothbeacondetection.utilities.TextValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EditFragment extends Fragment {

    private static final String TAG = "EditFragment";
    private String token;
    private User user;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();
    private String message;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_edit, container, false);
        token = getArguments().getString(Parameters.TOKEN);
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        final EditText firstNameEditText = root.findViewById(R.id.edit_firstNameEditText);
        final EditText lastNameEditText = root.findViewById(R.id.edit_lastNameEditText);
        final EditText cityEditText = root.findViewById(R.id.edit_cityEditText);
        final RadioButton maleButton = root.findViewById(R.id.edit_maleRadioButton);
        final RadioButton femaleButton = root.findViewById(R.id.edit_femaleRadioButton);
        final RadioGroup gender = root.findViewById(R.id.edit_genderRadioGroup);
        if(user!= null){
            firstNameEditText.setText(user.getFirstName());
            lastNameEditText.setText(user.getLastName());
            cityEditText.setText(user.getCity());
            if (user.getGender().equalsIgnoreCase("MALE"))
                maleButton.setChecked(true);
            else
                femaleButton.setChecked(true);
        }

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

        cityEditText.addTextChangedListener(new TextValidator(cityEditText) {
            @Override
            public void validate(TextView textView, String text) {
                if (Parameters.EMPTY.equalsIgnoreCase(text))
                    textView.setError(Parameters.EMPTY_ERROR_MESSAGE);
            }
        });

        root.findViewById(R.id.edit_updateButton).setOnClickListener(view -> {
            if (Parameters.EMPTY.equalsIgnoreCase(firstNameEditText.getText().toString()))
                firstNameEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
            else if (Parameters.EMPTY.equalsIgnoreCase(lastNameEditText.getText().toString()))
                lastNameEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
            else if (Parameters.EMPTY.equalsIgnoreCase(cityEditText.getText().toString()))
                cityEditText.setError(Parameters.EMPTY_ERROR_MESSAGE);
            else{
                int checkedRadioButtonId = gender.getCheckedRadioButtonId();
                RadioButton radioButton = root.findViewById(checkedRadioButtonId);
                user.setFirstName(firstNameEditText.getText().toString());
                user.setLastName(lastNameEditText.getText().toString());
                user.setCity(cityEditText.getText().toString());
                user.setGender(radioButton.getText().toString());
                String jsonString = gson.toJson(user);
                Log.v(TAG, jsonString);
                post(Parameters.API_URL,jsonString);
            }
        });
        return root;
    }

    public void post(String url, String json) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url+"/user/edit")
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
                    Log.v(TAG,responseString);
                    try {
                        JSONObject json = new JSONObject(responseString);
                        //String token = (String) json.get(Parameters.TOKEN);
                        message = (String) json.get(Parameters.MESSAGE);
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                        });
                        getFragmentManager().popBackStack();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
