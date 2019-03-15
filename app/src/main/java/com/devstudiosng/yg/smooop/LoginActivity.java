package com.devstudiosng.yg.smooop;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.devstudiosng.yg.smooop.helpers.MyToolBox;
import com.devstudiosng.yg.smooop.helpers.SharedPref;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginActivity extends Activity {

    private final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private static final String EMAIL = "email";
    private SharedPref sharedPref;
    private TwitterLoginButton twitterLoginButton;
    private static String twitterEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Twitter.initialize(this);

        setContentView(R.layout.activity_login);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPref = new SharedPref(this);
        callbackManager = CallbackManager.Factory.create();


//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.tw_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
                String twitterName = session.getUserName();
                long twitterUserId = session.getUserId();
                getTwitterUserEmail(result);

            }

            @Override
            public void failure(TwitterException exception) {
                MyToolBox.AlertMessage(LoginActivity.this, "Oops", exception.getMessage());
            }
        });


        fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", EMAIL));
        // If you are using in a fragment, call fbLoginButton.setFragment(this);    

        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                // save accessToken to SharedPreference
                sharedPref.saveAccessToken(accessToken);
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse response) {

                                // Getting FB User Data
                                Bundle facebookData = getFacebookData(jsonObject, accessToken);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);

    }

    private Bundle getFacebookData(JSONObject object, String accessToken) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));


            sharedPref.saveFacebookUserInfo(object.getString("first_name"),
                    object.getString("last_name"),object.getString("email"),
                    object.getString("gender"), profile_pic.toString());

            signInServer(object.getString("email"), "facebook", accessToken);
            Toast.makeText(this, object.getString("email"), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }

        return bundle;
    }

    private void getTwitterUserEmail(Result<TwitterSession> result) {
        TwitterSession session = result.data;
        //get user email
        new TwitterAuthClient().requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                twitterEmail = result.data;
                Log.e(TAG, "twitterEmail: " + twitterEmail);
            }

            @Override
            public void failure(TwitterException e) {
                Log.e(TAG, "Cannot get Twitter email");
            }
        });
    }

    private void signInServer(String email, String facebook, String accessToken) {


    }

}
