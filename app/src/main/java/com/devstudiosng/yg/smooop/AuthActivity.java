package com.devstudiosng.yg.smooop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devstudiosng.yg.smooop.helpers.MyToolBox;
import com.devstudiosng.yg.smooop.helpers.SharedPref;
import com.devstudiosng.yg.smooop.model.ApiService;
import com.devstudiosng.yg.smooop.model.EppLocation;
import com.devstudiosng.yg.smooop.model.ServerResponse;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthActivity extends Activity {

    private static final int RC_SIGN_IN = 10290;
    private Button signInButton, skipButton;
    private List<AuthUI.IdpConfig> providers;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        signInButton = findViewById(R.id.intro_button);
//        skipButton = findViewById(R.id.skip_button);

        sharedPref = new SharedPref(this);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyToolBox.isNetworkAvailable(AuthActivity.this)) {
                    Log.e("Auth", "here");
                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setIsSmartLockEnabled(false)
                                    .setLogo(R.drawable.logo_smaller)
                                    .setTheme(R.style.CustomFirebaseUI)
                                    .setTosAndPrivacyPolicyUrls(
                                            "https://smooop.com/terms",
                                            "https://smooop.com/privacy")

                                    .build(),
                            RC_SIGN_IN);

                } else {
                    MyToolBox.AlertMessage(AuthActivity.this, "Oops...", "Network error. Please check your connection and try again.");
                }
            }
        });

//        skipButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        });

        // Choose authentication providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, (user != null ? user.getDisplayName() : "") + " - " + (user != null ? user.getEmail() : ""), Toast.LENGTH_LONG).show();
//                MyToolBox.AlertMessage(this, user.getDisplayName() + " - " + user.getEmail()
//                        + " - " + user.getUid() + " - " + user.getProviderId());
                 saveToSharedPref(user);

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
//                Log.e("AuthError", response.getError().getMessage());
                if (response != null) {
                    MyToolBox.AlertMessage(this, response.getError().getMessage());
                }
            }
        }
    }

    private void saveToSharedPref(FirebaseUser user) {
        sharedPref.saveFirebaseUser(user);
        EppLocation mEppLocation;
        String address;
        Realm.init(this);
        Realm mRealm = Realm.getDefaultInstance();
//        int realmCount = (int) mRealm.where(EppLocation.class).count();
        RealmResults<EppLocation> realmList = mRealm.where(EppLocation.class).findAll();
//        realmList.last()
        if (realmList != null && realmList.size() != 0) {
            mEppLocation = realmList.last();
            address = mEppLocation.getPlace();
            createUser(user.getEmail(), user.getUid(), user.getProviderId(), user.getDisplayName(), address);
        }
    }

    private void createUser(String email, String userId, String providerId, String name, String address) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing ...");
        progressDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Smooop.SERVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<ServerResponse> call = apiService.signUpUser(email, userId, providerId, name, address);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AuthActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (response.body() != null) {
                                int userId = Integer.parseInt(response.body().getMessage());
                                sharedPref.setUserId(userId);
                                Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    MyToolBox.AlertMessage(AuthActivity.this, "Oops", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                MyToolBox.AlertMessage(AuthActivity.this, "Oops", t.getMessage()+" ");
            }
        });


    }

    public void goToHome() {
        Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
