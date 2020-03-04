package com.devstudiosng.yg.smooop.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devstudiosng.yg.smooop.AuthActivity;
import com.devstudiosng.yg.smooop.LocationActivity;
import com.devstudiosng.yg.smooop.R;
import com.devstudiosng.yg.smooop.model.EppLocation;
import com.google.android.libraries.places.api.model.PlaceLikelihood;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by Apple on 09/01/16.
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ContactItemViewHolder> {

    private static final String TAG = PlaceAdapter.class.getSimpleName();
//    private List<String> places;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<PlaceLikelihood> placeLikelihoods;

    public PlaceAdapter(List<PlaceLikelihood> places, Context context) {
        this.placeLikelihoods = places;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    class ContactItemViewHolder extends RecyclerView.ViewHolder {

        TextView mPlace;

        ContactItemViewHolder(View itemView) {
            super(itemView);

            mPlace = (TextView) itemView.findViewById(R.id.place_label);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlaceLikelihood likelihood = placeLikelihoods.get(getPosition());
                    final EppLocation eppLocation = new EppLocation();
                    eppLocation.setId(UUID.randomUUID().toString());
                    eppLocation.setPlace(likelihood.getPlace().getAddress());
                    eppLocation.setLattitude(likelihood.getPlace().getLatLng().latitude);
                    eppLocation.setLongitude(likelihood.getPlace().getLatLng().longitude);
                    eppLocation.setCreatedAt(new Date());
                    eppLocation.setUpdatedAt(new Date());


                    Realm mRealm;
                    Realm.init(mContext);
                    mRealm = Realm.getDefaultInstance();

                    mRealm.executeTransactionAsync(new Realm.Transaction() {
                        public void execute(Realm realm) {
                            realm.insert(eppLocation);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Log.e(TAG, "realm location inserted and closed - adapter");
                            Toast.makeText(mContext, "Current location added successfully", Toast.LENGTH_LONG).show();
                        }
                    });

//                    mDesc.setText(likelihood.getPlace().getAddress() + " - " + likelihood.getPlace().getLatLng().latitude +
//                            "," + likelihood.getPlace().getLatLng().longitude);

//                        likelyPlaces.release();
                    mRealm.close();
//                    Toast.makeText(LocationActivity.this, "Current location added successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, AuthActivity.class);
                    mContext.startActivity(intent);
//
                }
            });

        }
    }

    @Override
    public ContactItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.places_layout, parent, false);
        return new ContactItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactItemViewHolder holder, int position) {
        String place = this.placeLikelihoods.get(position).getPlace().getAddress();
        holder.mPlace.setText(place);

//        holder.mEmailTextview.setText(eppContact.getEmail()+ " ");
    }

    @Override
    public int getItemCount() {
        if (this.placeLikelihoods != null)
            return this.placeLikelihoods.size();
        else
            return 0;
    }


}
