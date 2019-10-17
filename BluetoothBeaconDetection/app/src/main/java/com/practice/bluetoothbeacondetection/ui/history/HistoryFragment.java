package com.practice.bluetoothbeacondetection.ui.history;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.bluetoothbeacondetection.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    RecyclerView recyclerView;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_fragmengt, container, false);

        return view;
    }

}
