package com.devstudiosng.yg.smooop.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPref {
    SharedPreferences sharedPreferences;

    public SharedPref(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveAccessToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fb_access_token", token);
        editor.apply(); // This line is IMPORTANT !!!
    }


    public String getToken() {

        return sharedPreferences.getString("fb_access_token", null);
    }

    public void clearToken() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void saveFacebookUserInfo(String first_name,String last_name, String email, String gender, String profileURL){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fb_first_name", first_name);
        editor.putString("fb_last_name", last_name);
        editor.putString("fb_email", email);
        editor.putString("fb_gender", gender);
        editor.putString("fb_profileURL", profileURL);
        editor.apply(); // This line is IMPORTANT !!!
        Log.d("MyApp", "Shared Name : "+first_name+"\nLast Name : "+last_name+"\nEmail : "+email+"\nGender : "+gender+"\nProfile Pic : "+profileURL);
    }

    public void getFacebookUserInfo(){

        Log.d("MyApp", "Name : "+sharedPreferences.getString("fb_name",null)+"\nEmail : "+sharedPreferences.getString("fb_email",null));
    }

    public void saveFirebaseUser(FirebaseUser user) {
        sharedPreferences.edit()
                .putString("fbUserName", user.getDisplayName())
                .putString("fbUserEmail", user.getEmail())
                .apply();
    }

    public String getFirebaseUserName() {
        return sharedPreferences.getString("fbUserName", null);
    }

    public String getFirebaseUserEmail() {
        return sharedPreferences.getString("fbUserEmail", null);
    }

    public void setUserId(int userId) {
        sharedPreferences.edit()
                .putInt("userId", userId)
                .apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt("userId", 0);
    }


//    public void setCurrentUser(String jsonUser) {
//        mSharedPreferences.edit()
//                .putString("jsonUser", jsonUser)
////                .commit();
//                .apply();
//    }
//
//    public User getCurrentUser() {
//        String user =  mSharedPreferences.getString("jsonUser", "");
//        return new GsonBuilder().create().fromJson(user, User.class);
//    }
}

//package com.doxa360.yg.android.savingsdemoapp.helpers;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Parcelable;
//import android.preference.PreferenceManager;
//import android.util.Log;
//
//import com.doxa360.yg.android.savingsdemoapp.model.User;
//import com.google.gson.GsonBuilder;
//
///**
// * Created by Apple on 23/06/15.
// */
//public class DutchSharedPref {
//    SharedPreferences mSharedPreferences;
//
//    public DutchSharedPref(Context context) {
//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//    }
//
//
//    public int getContactCount(){
//        return mSharedPreferences.getInt("contactCount", 0);
//    }
//
//    public void setContactCount(int count){
//        mSharedPreferences
//                .edit()
//                .putInt("contactCount", count)
//                .apply();
//    }
//
//    public int getCallLogCount(){
//        return mSharedPreferences.getInt("callLogCount", 0);
//    }
//
//    public void setCallLogCount(int count){
//        mSharedPreferences
//                .edit()
//                .putInt("callLogCount", count)
//                .apply();
//    }
//
//    public float getLattitude(){
//        return mSharedPreferences.getFloat("lattitude", (float) 0);
//    }
//
//    public void setLattitude(float lattitude){
//        mSharedPreferences
//                .edit()
//                .putFloat("lattitude",  lattitude)
//                .apply();
//    }
//
//    public float getLongtitude(){
//        return mSharedPreferences.getFloat("longtitude", (float) 0);
//    }
//
//    public void setLongtitude(float longtitude){
//        mSharedPreferences
//                .edit()
//                .putFloat("longtitude",  longtitude)
//                .apply();
//    }
//
//    public void setTutorial(boolean value) {
//        mSharedPreferences
//                .edit()
//                .putBoolean("tutorial", value)
//                .apply();
//    }
//
//    public boolean isTutorial() {
//        Log.e("shared pref", mSharedPreferences.getBoolean("tutorial", false)+"");
//        return mSharedPreferences.getBoolean("tutorial", false);
//    }
//
//    public void setTutorial2(boolean value) {
//        mSharedPreferences
//                .edit()
//                .putBoolean("tutorial2", value)
//                .apply();
//    }
//
//    public boolean isTutorial2() {
//        Log.e("shared2", mSharedPreferences.getBoolean("tutorial", false)+"");
//        return mSharedPreferences.getBoolean("tutorial2", false);
//    }
//
//    public void setTutorial3(boolean value) {
//        mSharedPreferences
//                .edit()
//                .putBoolean("tutorial3", value)
//                .apply();
//    }
//
//    public boolean isTutorial3() {
//        Log.e("shared3", mSharedPreferences.getBoolean("tutorial", false)+"");
//        return mSharedPreferences.getBoolean("tutorial3", false);
//    }
//
//    public String getSentiment(){
//        return mSharedPreferences.getString("sentiment", "Update your driving status" );
//    }
//
//    public void setSentiment(String sentiment){
//        mSharedPreferences
//                .edit()
//                .putString("sentiment", sentiment)
//                .apply();
//    }
//
//
//    public boolean isShared() {
//        return mSharedPreferences.getBoolean("pro_shared", false);
//    }
//
//    public void setShared() {
//        mSharedPreferences
//                .edit()
//                .putBoolean("pro_shared", true)
//                .apply();
//    }
//
//    public void setCurrentUser(String jsonUser) {
//        mSharedPreferences.edit()
//                .putString("jsonUser", jsonUser)
////                .commit();
//                .apply();
//    }
//
//    public User getCurrentUser() {
//        String user =  mSharedPreferences.getString("jsonUser", "");
//        return new GsonBuilder().create().fromJson(user, User.class);
//    }
//
//    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
//    public boolean clearCurrentUser() {
//        mSharedPreferences.edit()
//                .remove("jsonUser")
//                .commit();
//        return true;
//    }
//
//    public void setToken(String token) {
//        mSharedPreferences.edit()
//                .putString("token", token)
//                .apply();
//    }
//
//    public String getToken() {
//        return mSharedPreferences.getString("token", "");
//    }
//
//    public void setDeviceId(String deviceId) {
//        mSharedPreferences.edit()
//                .putString("deviceId", deviceId)
//                .apply();
//    }
//
//    public String getDeviceId() {
//        return mSharedPreferences.getString("deviceId", "");
//    }
//
////    public void setDevice(boolean value) {
////        mSharedPreferences
////                .edit()
////                .putBoolean("tutorial", value)
////                .apply();
////    }
////
////    public boolean isTutorial() {
////        Log.e("shared", mSharedPreferences.getBoolean("tutorial", false)+"");
////        return mSharedPreferences.getBoolean("tutorial", false);
////    }
//
//}