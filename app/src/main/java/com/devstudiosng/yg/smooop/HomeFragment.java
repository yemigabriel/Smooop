package com.devstudiosng.yg.smooop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.devstudiosng.yg.smooop.helpers.MyToolBox;
import com.devstudiosng.yg.smooop.helpers.SharedPref;
import com.devstudiosng.yg.smooop.model.ApiService;
import com.devstudiosng.yg.smooop.model.EppContact;
import com.devstudiosng.yg.smooop.model.EppLocation;
import com.devstudiosng.yg.smooop.model.EppNotification;
import com.devstudiosng.yg.smooop.model.ServerResponse;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private static final int ALERT_BOTTOM_SHEET = 201801;
    private Realm mRealm;
    Context mContext;
    private EppLocation mEppLocation;
    private boolean isAlertMenuOpen;
    private FrameLayout alertFrame, frameOverlay;
    private FloatingActionButton crimeFab, healthFab, mPanicBtn;
    private final String TAG = this.getClass().getSimpleName();

    private RealmResults<EppLocation> realmList;
    private RealmResults<EppContact> realmContactList;
    SharedPref sharedPref;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        isAlertMenuOpen = false;

        sharedPref = new SharedPref(mContext);

        Realm.init(mContext);
        mRealm = Realm.getDefaultInstance();
//        int realmCount = (int) mRealm.where(EppLocation.class).count();
        realmList = mRealm.where(EppLocation.class).findAll();
//        realmList.last()
        if (realmList != null && realmList.size() != 0) {
            mEppLocation = realmList.last();
        }
        realmContactList = mRealm.where(EppContact.class).findAll();

        TextView mLocationLabel = rootView.findViewById(R.id.current_location_label);
        if (realmList != null && realmList.size() != 0) {
            mLocationLabel.setText(mEppLocation.getPlace() + "(" + mEppLocation.getLattitude() + "," + mEppLocation.getLongitude() + ")");
        }

        mPanicBtn = rootView.findViewById(R.id.fabPanic);

        crimeFab = rootView.findViewById(R.id.crime_fab);
        healthFab = rootView.findViewById(R.id.health_fab);
        alertFrame = rootView.findViewById(R.id.alert_frame);
        frameOverlay = rootView.findViewById(R.id.frame_overlay);

        mPanicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isNetworkAvailable(mContext)) {
////                    Toast.makeText(mContext, "An alert has been sent. Help is on the way", Toast.LENGTH_LONG).show();
//
//                    AlertMessage(mContext, "An alert has been sent. Help is on the way");
//                } else {
//                    AlertMessage(mContext, "No network. Please check your connection and try again");
//                }
//                if (!isAlertMenuOpen) {
//                    showFabAlert();
//                } else {
//                    hideFabLayout();
//                }

                AlertBottomFragment bottomSheetDialog = new AlertBottomFragment();
                bottomSheetDialog.setTargetFragment(HomeFragment.this, ALERT_BOTTOM_SHEET);
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Alert Bottom Sheet");
            }
        });

        crimeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(mContext)) {
                    sendAlert(true);
                } else {
                    AlertMessage(mContext, "No network. Please check your connection and try again");
                }
            }
        });

        healthFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(mContext)) {
                    sendAlert(false);
                } else {
                    AlertMessage(mContext, "No network. Please check your connection and try again");
                }
            }
        });


        return rootView;
    }

    private void sendAlert(boolean isCrime) {
        if (!isCrime) {
            AlertMessage(mContext, "A health alert has been sent. Help is on the way");
        } else {
            AlertMessage(mContext, "A crime alert has been sent. Help is on the way");
        }
    }

    private void sendAlert(String alertType) {
        //TODO: Auto turn location
        sendAlertToServer(alertType);

        //TODO: Send SMS
        if (realmContactList != null && realmContactList.size() != 0) {

            ArrayList<String> phoneArrayList = new ArrayList<>();
            for (EppContact eppContact: realmContactList) {
//                sendSMS(eppContact.getName(), eppContact.getPhone(), "Hi. I have an emergency ("+alertType+"). I'm currently at "+
//                        mEppLocation.getPlace()+ ". Please send help");
                phoneArrayList.add(PhoneNumberUtils.stripSeparators(eppContact.getPhone()));
            }

                sendSMS("Hi. I have an emergency ("+alertType+"). I'm currently at "+
                        mEppLocation.getPlace()+ ". Please send help", phoneArrayList);
        }
        //Check if Twitter session auth
        Twitter.initialize(mContext);
        TwitterSession activeSession = TwitterCore.getInstance()
                .getSessionManager().getActiveSession();
        if (activeSession != null){
//            TweetComposer.Builder builder = new TweetComposer.Builder(mContext)
//                    .text("just setting up my Twitter Kit.");
//            builder.show();
            final Intent intent = new ComposerActivity.Builder(mContext)
                    .session(activeSession)
                    .text("Hi. I have an emergency ("+alertType+"). I'm currently at "+
                            mEppLocation.getPlace()+ " "+ mEppLocation.getLattitude() +","+ mEppLocation.getLongitude()+ ". Please send help")
                    .hashtags("#SmooopApp #emergency")
                    .createIntent();
            startActivity(intent);
        }

        AlertMessage(mContext, "An alert ("+alertType+") has been sent. Help is on the way");

    }

    private void sendAlertToServer(String alertType) {
        if (!MyToolBox.isNetworkAvailable(mContext)) {
            MyToolBox.AlertMessage(mContext, "Network Error", "Please check your internet connection and try again");
        } else {
            if (sharedPref.getUserId() == 0) {
//                MyToolBox.AlertMessage(mContext, "Server Error", "You are not logged in.");
                Toast.makeText(mContext, "You are not logged in...", Toast.LENGTH_SHORT).show();
            } else {
                int userId = sharedPref.getUserId();
                double lat = mEppLocation.getLattitude();
                double lng = mEppLocation.getLongitude();
                String address = mEppLocation.getPlace();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Smooop.SERVER_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);
                Call<ServerResponse> call = apiService.sendAlert(userId,0,0,
                        alertType,lat,lng,address,0);

                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(mContext, (response.body() != null ? response.body().getMessage() : null) +" ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, (response.body() != null ? response.body().getMessage() : null) +" ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(mContext, t.getMessage()+"", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }
    }

    private void showFabAlert() {
        Animation show_fab_1 = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        frameOverlay.setVisibility(View.VISIBLE);
        alertFrame.startAnimation(show_fab_1);

        mPanicBtn.setImageResource(R.drawable.ic_close_white_36dp);

        crimeFab.setClickable(true);
        healthFab.setClickable(true);
        isAlertMenuOpen = true;
    }

    private void hideFabLayout() {
        Animation hideLayout = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);
        frameOverlay.setVisibility(View.INVISIBLE);
        alertFrame.startAnimation(hideLayout);

        mPanicBtn.setImageResource(R.drawable.outline_notification_important_white_48dp);

        crimeFab.setClickable(false);
        healthFab.setClickable(false);
        isAlertMenuOpen = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
            Log.e("NETWORK","network avail...");
        }
        else{
            Log.e("NETWORK", "no network avail...");
        }

        return isAvailable;
    }


    public static void AlertMessage(Context context, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String mTitle = "Smooop";
        builder.setTitle(mTitle)
                .setMessage(alertMessage)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ALERT_BOTTOM_SHEET) {
                Log.e(TAG, "dialog closed");
                sendAlert(data.getStringExtra(getString(R.string.selected_alert_type)));
            }
        }
    }

    public void sendSMS(String msg, ArrayList phoneNos) {
//        String strippedPhoneNo = PhoneNumberUtils.stripSeparators(phoneNo);
//        Log.e(TAG, strippedPhoneNo + " and " + phoneNo);

//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(strippedPhoneNo, null, msg, null, null);
//            Toast.makeText(mContext, "Message sent to "+name,
//                    Toast.LENGTH_LONG).show();
//        } catch (Exception ex) {
//            Toast.makeText(mContext,ex.getMessage().toString(),
//                    Toast.LENGTH_LONG).show();
//            ex.printStackTrace();
//        }

        Intent intent = new Intent( Intent.ACTION_SENDTO, Uri.parse( "smsto:" + phoneNos));
        intent.putExtra( "sms_body", msg );
        startActivity(intent);
//
//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phoneNos));
//        smsIntent.putExtra("sms_body", msg);
//        startActivity(smsIntent);

    }


}
