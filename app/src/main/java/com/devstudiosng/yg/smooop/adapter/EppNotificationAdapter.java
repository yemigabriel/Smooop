package com.devstudiosng.yg.smooop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devstudiosng.yg.smooop.R;
import com.devstudiosng.yg.smooop.helpers.MyToolBox;
import com.devstudiosng.yg.smooop.model.EppNotification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmResults;

/**
 * Created by Apple on 09/01/16.
 */
public class EppNotificationAdapter extends RecyclerView.Adapter<EppNotificationAdapter.ContactItemViewHolder> {

    private static final String TAG = EppNotificationAdapter.class.getSimpleName();
    private RealmResults<EppNotification> eppNotifications;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public EppNotificationAdapter(RealmResults<EppNotification> eppNotifications, Context context) {
        this.eppNotifications = eppNotifications;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

     class ContactItemViewHolder extends RecyclerView.ViewHolder {

        TextView mMessage, mCreatedAt; //, mEmailTextview;

         ContactItemViewHolder(View itemView) {
            super(itemView);

            mMessage = (TextView) itemView.findViewById(R.id.message);
            mCreatedAt = (TextView) itemView.findViewById(R.id.created_at);
//            mEmailTextview = (TextView) itemView.findViewById(R.id.email);

            //do swipe here

        }
    }

    @Override
    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.eppnotification_layout, parent, false);
        return new ContactItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactItemViewHolder holder, int position) {
        EppNotification eppNotification = this.eppNotifications.get(position);
        holder.mMessage.setText(eppNotification.getMessage()+" ");


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(eppNotification.getCreatedAt());
            holder.mCreatedAt.setText(MyToolBox.getShortTimeAgo(date.getTime(), mContext)+ " ago ");
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        holder.mEmailTextview.setText(eppContact.getEmail()+ " ");
    }

    @Override
    public int getItemCount() {
        if (this.eppNotifications != null)
            return this.eppNotifications.size();
        else
            return 0;
    }


}
