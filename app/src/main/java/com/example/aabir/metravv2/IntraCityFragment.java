package com.example.aabir.metravv2;

/**
 * Created by abir on 4/26/2017.
 */

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.aabir.metravv2.Utility.BusOffline;
import com.example.aabir.metravv2.Utility.BusOfflineGroup;
import com.example.aabir.metravv2.Utility.BusOfflineListAdapter;

import java.util.ArrayList;

public class IntraCityFragment extends Fragment
    {

      public IntraCityFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intra_city, container, false);
    }


}
