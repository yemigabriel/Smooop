package com.devstudiosng.yg.smooop;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devstudiosng.yg.smooop.adapter.EppContactAdapter;
import com.devstudiosng.yg.smooop.adapter.EppNotificationAdapter;
import com.devstudiosng.yg.smooop.helpers.MyToolBox;
import com.devstudiosng.yg.smooop.model.ApiService;
import com.devstudiosng.yg.smooop.model.EppContact;
import com.devstudiosng.yg.smooop.model.EppNotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    public final static String TAG = NotificationFragment.class.getSimpleName();
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private Realm mRealm;
    private List<EppNotification> mEppNotifications;
    EppNotificationAdapter adapter;
    RealmResults<EppNotification> eppNotifications;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        Realm.init(mContext);
        mRealm = Realm.getDefaultInstance();

        mProgressBar = rootView.findViewById(R.id.progress_bar);

        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                getNotifications(true);
            }
        });

        mRecyclerView = rootView.findViewById(R.id.notification_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);

        getNotifications(true);

//        Button clickme = rootView.findViewById(R.id.click);
//        clickme.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                loadFromNetwork();
//            }
//        });



        return rootView;
    }

    private void getNotifications(boolean loadNetwork) {
        eppNotifications = mRealm.where(EppNotification.class).findAll().sort("createdAt");
        adapter = new EppNotificationAdapter(eppNotifications, mContext);
        mRecyclerView.setAdapter(adapter);
        mProgressBar.setVisibility(View.INVISIBLE);
        Log.e(TAG, eppNotifications.size() + " size");
        if (loadNetwork) {
            loadFromNetwork();
        }


    }

    private void loadFromNetwork() {
        if (!MyToolBox.isNetworkAvailable(mContext)) {
            MyToolBox.AlertMessage(mContext, "Network Error", "Please check your internet connection and try again");
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Smooop.SERVER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);
            Call<List<EppNotification>> call = apiService.notifications();
            call.enqueue(new Callback<List<EppNotification>>() {
                @Override
                public void onResponse(Call<List<EppNotification>> call, Response<List<EppNotification>> response) {
                    if (response.isSuccessful()) {
                        mEppNotifications = response.body();
                        if (mEppNotifications != null) {
                            Log.e(TAG, response.body() +"");
                            if (eppNotifications.size() != mEppNotifications.size()) {
                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.delete(EppNotification.class);
                                    }
                                });
                                for (EppNotification notification : mEppNotifications) {
                                    //do adding of notification to realm
                                    Log.e(TAG, notification.getMessage() + " ");
                                    notification.setId(UUID.randomUUID().toString());
//                                if (notification.rea)
                                    Date date = Calendar.getInstance().getTime();
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String strDate = dateFormat.format(date);

//                                    notification.setCreatedAt(strDate);
//                                    notification.setUpdatedAt(strDate);
                                    mRealm.executeTransactionAsync(new Realm.Transaction() {
                                        public void execute(Realm realm) {
                                            realm.insert(notification);
                                        }
                                    }, new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e(TAG, "realm notification inserted and closed");
                                            Toast.makeText(mContext, "Notification updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }


                                getNotifications(false);
                                        adapter.notifyDataSetChanged();

                            }
                        }
                        //refresh notify adapter
                        mProgressBar.setVisibility(View.INVISIBLE);
                    } else {
                        mProgressBar.setVisibility(View.INVISIBLE);
//                        mRefreshImage.setVisibility(View.VISIBLE);
                        MyToolBox.AlertMessage(mContext, "Oops", "Network error... Please check your connection.");
                    }

                    if(mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<List<EppNotification>> call, Throwable t) {
                    MyToolBox.AlertMessage(mContext, "Oops", "Network error. Please check your connection.");
                    mProgressBar.setVisibility(View.INVISIBLE);
                Log.e(TAG, t.getMessage()+"");

                    if(mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
