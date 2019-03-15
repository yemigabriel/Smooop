package com.devstudiosng.yg.smooop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.devstudiosng.yg.smooop.R;

import java.util.List;
import io.realm.RealmResults;
import com.devstudiosng.yg.smooop.model.EppContact;

/**
 * Created by Apple on 09/01/16.
 */
public class EppContactAdapter extends RecyclerView.Adapter<EppContactAdapter.ContactItemViewHolder> {

    private static final String TAG = EppContactAdapter.class.getSimpleName();
    private RealmResults<EppContact> eppContacts;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public EppContactAdapter(RealmResults<EppContact> eppContacts, Context context) {
        this.eppContacts = eppContacts;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

     class ContactItemViewHolder extends RecyclerView.ViewHolder {

        TextView mNameTextview, mPhoneTextview, mEmailTextview;

         ContactItemViewHolder(View itemView) {
            super(itemView);

            mNameTextview = (TextView) itemView.findViewById(R.id.name);
            mPhoneTextview = (TextView) itemView.findViewById(R.id.phone);
            mEmailTextview = (TextView) itemView.findViewById(R.id.email);

            //do swipe here

        }
    }

    @Override
    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.eppcontact_layout, parent, false);
        return new ContactItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactItemViewHolder holder, int position) {
        EppContact eppContact = this.eppContacts.get(position);
        holder.mNameTextview.setText(eppContact.getName()+" ");
        holder.mPhoneTextview.setText(eppContact.getPhone()+ " ");
        holder.mEmailTextview.setText(eppContact.getEmail()+ " ");
    }

    @Override
    public int getItemCount() {
        if (this.eppContacts != null)
            return this.eppContacts.size();
        else
            return 0;
    }


}
