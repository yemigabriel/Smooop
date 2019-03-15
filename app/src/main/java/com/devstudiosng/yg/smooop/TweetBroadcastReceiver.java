package com.devstudiosng.yg.smooop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

public class TweetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
            // success
//            final Long tweetId = intentExtras.getLong(TweetUploadService.EXTRA_TWEET_ID);
            Toast.makeText(context, "Alert tweeted successfully", Toast.LENGTH_SHORT).show();
        } else if (TweetUploadService.UPLOAD_FAILURE.equals(intent.getAction())) {
            // failure
//            final Intent retryIntent = intentExtras.getParcelable(TweetUploadService.EXTRA_RETRY_INTENT);
            Log.e("Tweet", "tweet failed");
        }

//        else if (TweetUploadService.TWEET_COMPOSE_CANCEL.equals(intent.getAction())) {
//            // cancel
//        }
    }
}
