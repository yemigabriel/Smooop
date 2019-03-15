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
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by Apple on 09/01/16.
 */
public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ContactItemViewHolder> {

    private static final String TAG = TweetAdapter.class.getSimpleName();
    private List<Tweet> tweets;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public TweetAdapter(List<Tweet> tweets, Context context) {
        this.tweets = tweets;
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
        Tweet tweet = this.tweets.get(position);
        holder.mMessage.setText(tweet.text);
        holder.mCreatedAt.setText(tweet.createdAt);

//        holder.mEmailTextview.setText(eppContact.getEmail()+ " ");
    }

    @Override
    public int getItemCount() {
        if (this.tweets != null)
            return this.tweets.size();
        else
            return 0;
    }


}
