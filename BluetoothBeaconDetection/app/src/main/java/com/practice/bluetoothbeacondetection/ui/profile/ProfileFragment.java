package com.practice.bluetoothbeacondetection.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.practice.bluetoothbeacondetection.R;
import com.practice.bluetoothbeacondetection.models.User;
import com.practice.bluetoothbeacondetection.ui.edit.EditFragment;
import com.practice.bluetoothbeacondetection.utilities.Parameters;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private String token;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        token = getArguments().getString(Parameters.TOKEN);
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        final TextView userIdTextView = root.findViewById(R.id.profile_userIdTextView);
        final TextView fullNameTextView = root.findViewById(R.id.profile_fullNameTextView);
        final TextView emailIdTextView = root.findViewById(R.id.profile_emailTextView);
        final TextView usernameTextView = root.findViewById(R.id.profile_usernameTextView);
        final TextView cityTextView = root.findViewById(R.id.profile_cityTextView);
        final TextView genderTextView = root.findViewById(R.id.profile_genderTextView);
        if(user!= null){
            userIdTextView.setText("User Id: "+user.getUserId());
            fullNameTextView.setText("Name: "+user.getFirstName()+ " "+ user.getLastName());
            emailIdTextView.setText("Email Id: "+user.getEmail());
            usernameTextView.setText("Username: "+user.getUsername());
            cityTextView.setText("City: "+user.getCity());
            genderTextView.setText("Gender: "+user.getGender());
        }

        root.findViewById(R.id.profile_editButton).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Parameters.USER_ID, user);
            bundle.putString(Parameters.TOKEN, token);
            EditFragment fragment = new EditFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment).addToBackStack(null);
            fragmentTransaction.commit();
        });
        return root;
    }


}