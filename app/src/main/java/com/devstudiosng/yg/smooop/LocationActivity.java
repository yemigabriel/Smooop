package com.devstudiosng.yg.smooop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devstudiosng.yg.smooop.helpers.MyToolBox;
import com.devstudiosng.yg.smooop.model.EppContact;
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
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.location.places.PlaceLikelihood;
//import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
//import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import com.devstudiosng.yg.smooop.model.EppLocation;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.compat.Place;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

// Add import statements for the new library.
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.net.PlacesClient;


public class LocationActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 198;
    private String TAG = this.getClass().getSimpleName();
//    protected GeoDataClient mGeoDataClient;
//    PlaceDetectionClient mPlaceDetectionClient;

    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;

    public List<PlaceLikelihood> placeLikelihoodList;

    // Initialize Places.


    // Create a new Places client instance.
    PlacesClient placesClient;

    Realm mRealm;

    ImageView mLocationBtn;
    ProgressBar mProgressBar;
    TextView mDesc;

    Button introBtn;

    String[] PERMISSIONS= {
            ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_location);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        RealmResults<EppLocation> eppLocations = mRealm.where(EppLocation.class).findAll();


// Initialize Places.
//        Places.get
//        Places.initialize(getApplicationContext(), apiKey);
        Places.initialize(this, getString(R.string.places_api_key));
//
//// Create a new Places client instance.
        placesClient = Places.createClient(this);

//        mGoogleApiClient = new GoogleApiClient
//                .Builder(getActivity())
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .build();

        progressDialog = new ProgressDialog(this);
//
//        mGeoDataClient = Places.getGeoDataClient(this);
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);


        introBtn = findViewById(R.id.intro_button);
        if (eppLocations == null || eppLocations.size() == 0) {
//            introBtn.setEnabled(false);
        }
        mLocationBtn = findViewById(R.id.location_button);
        mDesc = findViewById(R.id.desc);
        TextView mTitleLabel = findViewById(R.id.title_label);

        Typeface pacificoFont = Typeface.createFromAsset(getAssets(),"fonts/pacifico.ttf");
        mTitleLabel.setTypeface(pacificoFont);


        introBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eppLocations == null || eppLocations.size() == 0) {
                    MyToolBox.AlertMessage(LocationActivity.this, "Please tap on the location icon to get your current location");
                } else {
                    Intent intent = new Intent(LocationActivity.this, AuthActivity.class);
                    startActivity(intent);
                }
            }
        });

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              TODO  getPermission();
                if (MyToolBox.isNetworkAvailable(LocationActivity.this)) {
                    if (MyToolBox.isGPSEnabled(LocationActivity.this)) {
//                    Get location
                        getUserCurrentPlace();
                    } else {
                        enableLoc();
                    }
                } else {
                    MyToolBox.AlertMessage(LocationActivity.this, "Oops", "Network error. Please check your connection and try again");
                }
            }
        });


    }

    private boolean isGPSEnabled() {
        return MyToolBox.isGPSEnabled(this);
    }

    private void getUserCurrentPlace() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this,
                    ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(LocationActivity.this, PERMISSIONS,
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            }
            else {
                ActivityCompat.requestPermissions(LocationActivity.this,
                        PERMISSIONS, MY_PERMISSIONS_REQUEST_LOCATION);
            }

        } else {
            progressDialog.setMessage("Fetching current location ...");
            progressDialog.show();

            // Use fields to define the data types to return.
            List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.builder(placeFields).build();
//
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    if (response != null) {
                        placeLikelihoodList = response.getPlaceLikelihoods();
                        for (com.google.android.libraries.places.api.model.PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            Log.e(TAG, String.format("Place '%s' has likelihood: %f",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getLikelihood()));
                        }

                        com.google.android.libraries.places.api.model.PlaceLikelihood likelihood = response.getPlaceLikelihoods().get(0);
                        if (likelihood!=null) {
//                            PlacesBottomDialogFragment fragment = new PlacesBottomDialogFragment();
//                            fragment.show(getSupportFragmentManager(), "Places list");
                            final EppLocation eppLocation = new EppLocation();
                            eppLocation.setId(UUID.randomUUID().toString());
                            eppLocation.setPlace(likelihood.getPlace().getAddress());
                            eppLocation.setLattitude(likelihood.getPlace().getLatLng().latitude);
                            eppLocation.setLongitude(likelihood.getPlace().getLatLng().longitude);
                            eppLocation.setCreatedAt(new Date());
                            eppLocation.setUpdatedAt(new Date());


                            mRealm.executeTransactionAsync(new Realm.Transaction() {
                                public void execute(Realm realm) {
                                    realm.insert(eppLocation);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    Log.e(TAG, "realm location inserted and closed");
                                    progressDialog.dismiss();
                                    Toast.makeText(LocationActivity.this, "Current location added successfully", Toast.LENGTH_LONG).show();
                                }
                            });
//
//                            mDesc.setText(likelihood.getPlace().getAddress() + " - " + likelihood.getPlace().getLatLng().latitude +
//                                    "," + likelihood.getPlace().getLatLng().longitude);
                            mDesc.setText(likelihood.getPlace().getLatLng().latitude +
                                    "," + likelihood.getPlace().getLatLng().longitude);

//                        likelyPlaces.release();
                            mRealm.close();
                            Toast.makeText(LocationActivity.this, "Current location added successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LocationActivity.this, AuthActivity.class);
                            startActivity(intent);
//
                        } else {
                            Toast.makeText(LocationActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        MyToolBox.AlertMessage(LocationActivity.this, apiException.getMessage());
                        Log.e(TAG, "Place not found: "+ apiException.getMessage() + apiException.getStatusCode());
                    }
                }
            });

//            Toast.makeText(LocationActivity.this, "Obtaining current location ...", Toast.LENGTH_LONG).show();
//            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);

//            mPlaceDetectionClient.getCurrentPlace(null).addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                    Log.e(TAG, "here o");
//                    try {
//                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//                    if (likelyPlaces != null) {
//                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                            Log.e(TAG, String.format("Place '%s' has likelihood: %g",
//                                    placeLikelihood.getPlace().getName(),
//                                    placeLikelihood.getLikelihood()));
//                            Log.e(TAG, placeLikelihood.getPlace().getLatLng().latitude + " - " +
//                                    placeLikelihood.getPlace().getLatLng().longitude);
//                        }
//
//                        PlaceLikelihood likelihood = likelyPlaces.get(0);
//                        final EppLocation eppLocation = new EppLocation();
//                        eppLocation.setId(UUID.randomUUID().toString());
//                        eppLocation.setPlace(likelihood.getPlace().getAddress().toString());
//                        eppLocation.setLattitude(likelihood.getPlace().getLatLng().latitude);
//                        eppLocation.setLongitude(likelihood.getPlace().getLatLng().longitude);
//                        eppLocation.setCreatedAt(new Date());
//                        eppLocation.setUpdatedAt(new Date());
//
//
//                        mRealm.executeTransactionAsync(new Realm.Transaction() {
//                            public void execute(Realm realm) {
//                                realm.insert(eppLocation);
//                            }
//                        }, new Realm.Transaction.OnSuccess() {
//                            @Override
//                            public void onSuccess() {
//                                Log.e(TAG, "realm location inserted and closed");
//                                progressDialog.dismiss();
//                                Toast.makeText(LocationActivity.this, "Current location added successfully", Toast.LENGTH_LONG).show();
//                            }
//                        });
//
//                        mDesc.setText(likelihood.getPlace().getAddress() + " - " + likelihood.getPlace().getLatLng().latitude +
//                                "," + likelihood.getPlace().getLatLng().longitude);
//
//                        likelyPlaces.release();
//
//                        mRealm.close();
//                    }
//
//                    }
//                    catch (Exception e) {
////                        Crashlytics.log(e.getMessage());
//                        Log.e("Crash error", e.getMessage());
//                        progressDialog.dismiss();
//                        Toast.makeText(LocationActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
//
//                    }
//                }
//            });
        }

    }


    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(LocationActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Log.e(TAG, "api client connnected");
//                            getUserCurrentPlace();
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
                                status.startResolutionForResult( LocationActivity.this, REQUEST_LOCATION);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserCurrentPlace();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
//                else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                Log.e(TAG, "Location activated");
                getUserCurrentPlace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        getUserCurrentPlace();
//                    }
//                });
            }
        }

    }


}
