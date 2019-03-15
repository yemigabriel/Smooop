package com.devstudiosng.yg.smooop.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.devstudiosng.yg.smooop.AlertBottomFragment;
import com.devstudiosng.yg.smooop.R;
import com.devstudiosng.yg.smooop.model.EppAlertType;
import com.devstudiosng.yg.smooop.model.EppContact;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by Apple on 09/01/16.
 */
public class EppAlertTypeAdapter extends RecyclerView.Adapter<EppAlertTypeAdapter.ItemViewHolder> {

    private static final String TAG = EppAlertTypeAdapter.class.getSimpleName();
    private final AlertBottomFragment fragment;
    private List<EppAlertType> alertTypeList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public EppAlertTypeAdapter(List<EppAlertType> alertTypeList, Context context, AlertBottomFragment fragment) {
        this.alertTypeList = alertTypeList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

     class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView mType;
        ImageView mIcon;

         ItemViewHolder(View itemView) {
            super(itemView);

            mType = (TextView) itemView.findViewById(R.id.type);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EppAlertTypeAdapter.this.fragment.selectedItem =
                            EppAlertTypeAdapter.this.fragment.alertTypeList.get(getPosition()).getType();
                    EppAlertTypeAdapter.this.fragment.dismiss();
                }
            });

        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.eppalerttype_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        EppAlertType alertType = this.alertTypeList.get(position);
        holder.mType.setText(alertType.getType());
        Glide.with(mContext).load(alertType.getIcon())
                .apply(new RequestOptions().fitCenter())
                .into(holder.mIcon);
    }

    @Override
    public int getItemCount() {
        if (this.alertTypeList != null)
            return this.alertTypeList.size();
        else
            return 0;
    }


}
