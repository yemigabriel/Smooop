package com.devstudiosng.yg.smooop;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
import com.devstudiosng.yg.smooop.model.ServerResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import static android.app.Activity.RESULT_OK;
import static com.devstudiosng.yg.smooop.LocationActivity.REQUEST_LOCATION;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final int ALERT_BOTTOM_SHEET = 201801;
    private Realm mRealm;
    Context mContext;
    private EppLocation mEppLocation;
    private boolean isAlertMenuOpen;
    private FrameLayout alertFrame, frameOverlay;
    private FloatingActionButton crimeFab, healthFab, mPanicBtn;
    private TextView mLocationLabel;
    private final String TAG = this.getClass().getSimpleName();

    private RealmResults<EppLocation> realmList;
    private RealmResults<EppContact> realmContactList;
    SharedPref sharedPref;

    private GoogleMap mMap;
    private Circle mCircle;
    private Marker mMarker;

    private GoogleApiClient googleApiClient;
    private PlacesClient placesClient;

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
            Log.e(TAG, mEppLocation.getCreatedAt().toString() + " - date");
        }
        realmContactList = mRealm.where(EppContact.class).findAll();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mLocationLabel = rootView.findViewById(R.id.current_location_label);
        mLocationLabel.setClickable(false);
        mLocationLabel.setVisibility(View.INVISIBLE);
//        mLocationLabel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, MapsActivity.class);
//                startActivity(intent);
//            }
//        });
        if (realmList != null && realmList.size() != 0) {
//            mLocationLabel.setText(mEppLocation.getPlace() + "(" + mEppLocation.getLattitude() + "," + mEppLocation.getLongitude() + ")");
            mLocationLabel.setText("(" + mEppLocation.getLattitude() + "," + mEppLocation.getLongitude() + ")");
        }

        if (MyToolBox.isNetworkAvailable(mContext)) {
            if (MyToolBox.isGPSEnabled(mContext)) {
            } else {
                enableLoc();
            }
        } else {
            MyToolBox.AlertMessage(mContext, "Oops", "Network error. Please check your connection and try again");
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
                    MyToolBox.AlertMessage(mContext, "Oops...","No network. Please check your connection and try again");
                }
            }
        });

        healthFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(mContext)) {
                    sendAlert(false);
                } else {
                    MyToolBox.AlertMessage(mContext, "Oops...", "No network. Please check your connection and try again");
                }
            }
        });

        mapSetup(mEppLocation.getLattitude(), mEppLocation.getLongitude());

        return rootView;
    }


    private void sendAlert(boolean isCrime) {
//        if (!isCrime) {
//            AlertMessage(mContext, "A health alert has been sent. Help is on the way");
//        } else {
//            AlertMessage(mContext, "A crime alert has been sent. Help is on the way");
//        }
    }

    private void sendAlert(String alertType) {
        //TODO: Auto turn location
        sendAlertToServer(alertType);

        //TODO: Send SMS
//        if (realmContactList != null && realmContactList.size() != 0) {
//
//            ArrayList<String> phoneArrayList = new ArrayList<>();
//            for (EppContact eppContact : realmContactList) {
////                sendSMS(eppContact.getName(), eppContact.getPhone(), "Hi. I have an emergency ("+alertType+"). I'm currently at "+
////                        mEppLocation.getPlace()+ ". Please send help");
//                phoneArrayList.add(PhoneNumberUtils.stripSeparators(eppContact.getPhone()));
//            }
//
//            sendSMS("Hi. I have an emergency (" + alertType + "). I'm currently at " +
//                    mEppLocation.getPlace() + ". Please send help", phoneArrayList);
//        }
//        //Check if Twitter session auth
//        Twitter.initialize(mContext);
//        TwitterSession activeSession = TwitterCore.getInstance()
//                .getSessionManager().getActiveSession();
//        if (activeSession != null) {
////            TweetComposer.Builder builder = new TweetComposer.Builder(mContext)
////                    .text("just setting up my Twitter Kit.");
////            builder.show();
//            final Intent intent = new ComposerActivity.Builder(mContext)
//                    .session(activeSession)
//                    .text("Hi. I have an emergency (" + alertType + "). I'm currently at " +
//                            mEppLocation.getPlace() + " " + mEppLocation.getLattitude() + "," + mEppLocation.getLongitude() + ". Please send help")
//                    .hashtags("#SmooopApp #emergency")
//                    .createIntent();
//            startActivity(intent);
//        }

        AlertMessage(mContext, "An alert (" + alertType + ") has been sent. \nGet more people involved. Share this alert via other apps?", alertType);

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
                Call<ServerResponse> call = apiService.sendAlert(userId, 0, 0,
                        alertType, lat, lng, address, 0);

                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(mContext, (response.body() != null ? response.body().getMessage() : null) + " ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, (response.body() != null ? response.body().getMessage() : null) + " ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(mContext, t.getMessage() + "", Toast.LENGTH_SHORT).show();
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
        if (mRealm != null) {
            mRealm.close();
        }
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
            Log.e("NETWORK", "network avail...");
        } else {
            Log.e("NETWORK", "no network avail...");
        }

        return isAvailable;
    }


    public void AlertMessage(Context context, String alertMessage, String alertType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String mTitle = "Smooop";
        builder.setTitle(mTitle)
                .setMessage(alertMessage)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareIntent(mEppLocation, alertType);
                    }
                })
                .setNegativeButton("No", null);


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shareIntent(EppLocation mEppLocation, String alertType) {
        String locationUrl = "http://maps.google.com/?ll="+mEppLocation.getLattitude()+","+mEppLocation.getLongitude();
        String shareText = "I have an emergency ("+alertType+"). My current location is at: "+locationUrl+" Please send help. \n -via Smooop.com";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share Alert"));
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

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNos));
        intent.putExtra("sms_body", msg);
        startActivity(intent);
//
//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phoneNos));
//        smsIntent.putExtra("sms_body", msg);
//        startActivity(smsIntent);

    }

    //new map stuff
    private void mapSetup(double lattitude, double longitude) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "map ready");
        mMap = googleMap;
        LatLng lastSeen = new LatLng(mEppLocation.getLattitude(), mEppLocation.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(lastSeen).title("Last seen"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastSeen));
        drawMarkerWithCircle(lastSeen);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(15.8f);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                updateRealmLocation(latLng);
                if(mCircle == null || mMarker == null){
                    drawMarkerWithCircle(latLng);
                }else{
                    updateMarkerWithCircle(latLng);
                }
            }
        });
//        updatePlaceLocation();

    }

    private void updatePlaceLocation() {
        Toast.makeText(mContext, "Updating location ...", Toast.LENGTH_SHORT).show();
        Places.initialize(mContext, getString(R.string.places_api_key));
        placesClient = Places.createClient(mContext);
        List<Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();
//
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FindCurrentPlaceResponse response = task.getResult();
                if (response != null) {
//                    List<PlaceLikelihood> placeLikelihoodList = response.getPlaceLikelihoods();
                    for (com.google.android.libraries.places.api.model.PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.e(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }

                    com.google.android.libraries.places.api.model.PlaceLikelihood likelihood = response.getPlaceLikelihoods().get(0);
                    if (likelihood!=null) {
                        updateMarkerWithCircle(likelihood.getPlace().getLatLng());
//                            PlacesBottomDialogFragment fragment = new PlacesBottomDialogFragment();
//                            fragment.show(getSupportFragmentManager(), "Places list");
                        final EppLocation eppLocation = new EppLocation();
                        eppLocation.setId(UUID.randomUUID().toString());
                        eppLocation.setPlace(likelihood.getPlace().getAddress());
                        eppLocation.setLattitude(likelihood.getPlace().getLatLng().latitude);
                        eppLocation.setLongitude(likelihood.getPlace().getLatLng().longitude);
                        eppLocation.setCreatedAt(new Date());
                        eppLocation.setUpdatedAt(new Date());

                        Log.e(TAG, likelihood.getPlace().getLatLng().latitude +" , "+ likelihood.getPlace().getLatLng().longitude );


                        mRealm.executeTransactionAsync(new Realm.Transaction() {
                            public void execute(Realm realm) {
                                realm.insert(eppLocation);
                                mEppLocation = eppLocation;
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "realm location inserted and closed");
//                                progressDialog.dismiss();
                                Toast.makeText(mContext, "Current location successfully updated", Toast.LENGTH_LONG).show();
                            }
                        });
//
//                            mDesc.setText(likelihood.getPlace().getAddress() + " - " + likelihood.getPlace().getLatLng().latitude +
//                                    "," + likelihood.getPlace().getLatLng().longitude);
//                        mDesc.setText(likelihood.getPlace().getLatLng().latitude +
//                                "," + likelihood.getPlace().getLatLng().longitude);

//                        likelyPlaces.release();
//                        mRealm.close();
//
                    } else {
                        Toast.makeText(mContext, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    MyToolBox.AlertMessage(mContext, apiException.getMessage());
                    Log.e(TAG, "Place not found: "+ apiException.getMessage() + apiException.getStatusCode());
                }
            }
        });



    }

    private void updateMarkerWithCircle(LatLng position) {
        mCircle.setCenter(position);
        mMarker.setPosition(position);
        updateRealmLocation(position);
    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = 350.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x22ff0000; //opaque red fill
        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
        mCircle = mMap.addCircle(circleOptions);
        MarkerOptions markerOptions = new MarkerOptions().position(position);
        mMarker = mMap.addMarker(markerOptions);
    }

    private void updateRealmLocation(LatLng latLng) {
        final EppLocation eppLocation = new EppLocation();
        eppLocation.setId(UUID.randomUUID().toString());
        eppLocation.setPlace("n/a");
        eppLocation.setLattitude(latLng.latitude);
        eppLocation.setLongitude(latLng.longitude);
        eppLocation.setCreatedAt(new Date());
        eppLocation.setUpdatedAt(new Date());


        mRealm.executeTransactionAsync(new Realm.Transaction() {
            public void execute(Realm realm) {
                realm.insert(eppLocation);
                mEppLocation = eppLocation;
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "realm location inserted and closed");
//                Toast.makeText(LocationActivity.this, "Current location added successfully", Toast.LENGTH_LONG).show();
            }
        });

//        mRealm.close();
    }

    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Log.e(TAG, "api client connnected");
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Log.e("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult( getActivity(), REQUEST_LOCATION);
//                                updatePlaceLocation();
//                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                                Log.e(TAG, e.getMessage());

                            }
                            break;
                    }
                }
            });
        }
    }

}
