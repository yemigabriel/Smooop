package com.devstudiosng.yg.smooop;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.devstudiosng.yg.smooop.adapter.TweetAdapter;
import com.devstudiosng.yg.smooop.helpers.MyToolBox;
import com.google.android.gms.auth.api.Auth;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwitterFragment extends Fragment {


    public final static String TAG = NotificationFragment.class.getSimpleName();
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private List<Tweet> tweets;
    private RecyclerView recyclerView;
    private TweetAdapter adapter;

    public TwitterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_twitter, container, false);

        Twitter.initialize(mContext);
        tweets = null;
        
        mProgressBar = rootView.findViewById(R.id.progress_bar);

        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTweets();
            }
        });

        recyclerView = rootView.findViewById(R.id.notification_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                mSwipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }

        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);

        getTweets();

        return rootView;
    }

    private void getTweets() {
//        TwitterSession activeSession = TwitterCore.getInstance()
//                .getSessionManager().getActiveSession();

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getGuestApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<List<Tweet>> call = statusesService.userTimeline(null, "Gidi_Traffic", null,
                null, null, null,true,null,false);
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                if (result.response.isSuccessful()) {
                    tweets = result.data;
                    adapter = new TweetAdapter(tweets, mContext);
                    recyclerView.setAdapter(adapter);
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    MyToolBox.AlertMessage(mContext, "Oops", "Network error. Please check your connection and try again.");
                    mProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void failure(TwitterException exception) {
//                MyToolBox.AlertMessage(mContext, "Oops", exception.getMessage()+"");
                TweetAlertMessage(mContext, "You need to be logged in via Twitter to view traffic updates");
                mProgressBar.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    public static void TweetAlertMessage(Context context, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Oops")
                .setMessage(alertMessage)
                .setPositiveButton("Sign in via Twitter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, AuthActivity.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
