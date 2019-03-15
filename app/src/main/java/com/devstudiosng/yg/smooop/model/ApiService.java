package com.devstudiosng.yg.smooop.model;


import com.devstudiosng.yg.smooop.Smooop;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET(Smooop.SIGN_UP_API)
    Call<ServerResponse> signUpUser(@Query("email") String email,
                      @Query("userId") String userId,
                      @Query("providerId") String providerId,
                    @Query("name") String name,
                                    @Query("address") String address);
//
    @GET(Smooop.ALERT)
    Call<ServerResponse> sendAlert(@Query ("user_id") int userId,
                             @Query ("device_id") int deviceId,
                             @Query ("category_id") int categoryId,
                             @Query ("category") String category,
                             @Query ("lat") double lat,
                             @Query ("lng") double lng,
                             @Query ("address") String address,
                             @Query ("status") int status);

    @GET(Smooop.NOTIFICATIONS)
    Call<List<EppNotification>> notifications();

//    @GET(WallpaperApp.TOP_WALLPAPERS)
//    Call<List<Wallpaper>> topWallpapers();
//
////    @GET(WallpaperApp.ALL_USER_ADS)
////    Call<List<Ad>> userAds(@Query("user_id") int user_id);
//
//    @GET(WallpaperApp.ALL_CATEGORIES)
//    Call<List<Category>> allCategories();

}
