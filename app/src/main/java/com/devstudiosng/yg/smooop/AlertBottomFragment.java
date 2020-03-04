package com.devstudiosng.yg.smooop;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devstudiosng.yg.smooop.adapter.EppAlertTypeAdapter;
import com.devstudiosng.yg.smooop.model.EppAlertType;
import com.thinkincode.utils.views.HorizontalFlowLayout;

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
    private HorizontalFlowLayout flowLayout;
    private ColorStateList colorStateList;

    public AlertBottomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alert_bottom, container, false);

//        recyclerView = rootView.findViewById(R.id.alert_recyclerview);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//        recyclerView.setLayoutManager(layoutManager);

        flowLayout = rootView.findViewById(R.id.alert_flowlayout);

        alertTypeList = new ArrayList<EppAlertType>();
        alertTypeList.add(new EppAlertType(1, "Accident", R.drawable.traffic_accident_96));
        alertTypeList.add(new EppAlertType(2, "Crime: Robbery", R.drawable.crime_96));
        alertTypeList.add(new EppAlertType(3, "Crime: Kidnapping", R.drawable.spy_96));
        alertTypeList.add(new EppAlertType(4, "Domestic Violence", R.drawable.fight_96));
        alertTypeList.add(new EppAlertType(5, "Police Harassment", R.drawable.fight_96));
        alertTypeList.add(new EppAlertType(6,"Fire", R.drawable.gas_96));
        alertTypeList.add(new EppAlertType(7, "Health Emergency", R.drawable.ambulance_white_96));
        alertTypeList.add(new EppAlertType(8, "Tow service", R.drawable.car_service_96));
        alertTypeList.add(new EppAlertType(9, "Others", R.drawable.outline_notification_important_white_48dp));

//        EppAlertTypeAdapter adapter = new EppAlertTypeAdapter(alertTypeList, mContext, this);
//        recyclerView.setAdapter(adapter);
        int[][] states = new int[][] {
                new int[] {android.R.attr.state_enabled}, // enabled
//                new int[] {-android.R.attr.state_enabled}, // disabled
//                new int[] {-android.R.attr.state_checked}, // unchecked
//                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                R.color.secondary_text,
//                Color.RED,
//                Color.GREEN,
//                Color.BLUE
        };

        colorStateList = new ColorStateList(states, colors);
        createChips();


        return rootView;
    }

    private void createChips() {
        for (EppAlertType alertType: alertTypeList) {
            LinearLayout linearLayout = new LinearLayout(mContext);


//            linearLayout.setLayoutParams();
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(16,16,16,16);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout.setPadding(32,32,20,20);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setBackground(getResources().getDrawable(R.drawable.alert_chips));
            ImageView iconView = new ImageView(mContext);
            iconView.setImageResource(alertType.getIcon());
            iconView.setImageTintList(colorStateList);

            FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(
                    64, // Width in pixel
                    64);
            iconLp.gravity = Gravity.START|Gravity.CENTER ;

            iconView.setLayoutParams(iconLp);
            iconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAlertType(alertType.getType());
                }
            });

            TextView textView = new TextView(mContext);
            textView.setText(alertType.getType());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAlertType(alertType.getType());
                }
            });

            linearLayout.addView(iconView);
            linearLayout.addView(textView);
            flowLayout.addView(linearLayout);

        }
    }

    private void getAlertType(String type) {
        selectedItem = type;
        this.dismiss();
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
        if (selectedItem != null && !selectedItem.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra(getString(R.string.selected_alert_type), selectedItem);
            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        }
    }
}
