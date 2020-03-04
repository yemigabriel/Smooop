package com.devstudiosng.yg.smooop;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.devstudiosng.yg.smooop.adapter.PlaceAdapter;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesBottomDialogFragment extends BottomSheetDialogFragment {

    RecyclerView recyclerView;
    PlaceAdapter adapter;
    private Context mContext;

    public PlacesBottomDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_places_bottom_dialog, container, false);

        recyclerView = rootView.findViewById(R.id.place_recyclerview);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PlaceAdapter(((LocationActivity) Objects.requireNonNull(getActivity())).placeLikelihoodList, mContext);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
