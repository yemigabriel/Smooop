package com.devstudiosng.yg.smooop;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devstudiosng.yg.smooop.adapter.EppAlertTypeAdapter;
import com.devstudiosng.yg.smooop.model.EppAlertType;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.devstudiosng.yg.smooop.NotificationFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlertBottomFragment extends BottomSheetDialogFragment {

    public String selectedItem;
    RecyclerView recyclerView;
    private Context mContext;
    public List<EppAlertType> alertTypeList;

    public AlertBottomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alert_bottom, container, false);

        recyclerView = rootView.findViewById(R.id.alert_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        alertTypeList = new ArrayList<EppAlertType>();
        alertTypeList.add(new EppAlertType(1, "Accident", R.drawable.traffic_accident_96));
        alertTypeList.add(new EppAlertType(2, "Crime: Robbery", R.drawable.crime_96));
        alertTypeList.add(new EppAlertType(3, "Crime: Kidnapping", R.drawable.spy_96));
        alertTypeList.add(new EppAlertType(4, "Domestic Violence", R.drawable.fight_96));
        alertTypeList.add(new EppAlertType(5,"Fire", R.drawable.gas_96));
        alertTypeList.add(new EppAlertType(6, "Health Emergency", R.drawable.ambulance_white_96));
        alertTypeList.add(new EppAlertType(7, "Tow service", R.drawable.car_service_96));
        alertTypeList.add(new EppAlertType(8, "Others", R.drawable.outline_notification_important_white_48dp));

        EppAlertTypeAdapter adapter = new EppAlertTypeAdapter(alertTypeList, mContext, this);
        recyclerView.setAdapter(adapter);


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e(TAG, "dismissed, passing selected item "+ selectedItem);
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.selected_alert_type), selectedItem);
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
    }
}
