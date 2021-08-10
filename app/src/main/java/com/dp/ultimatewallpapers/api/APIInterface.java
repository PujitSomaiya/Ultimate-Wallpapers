package com.dp.ultimatewallpapers.api;

import com.pujit.wallpaperdemo.api.response.PhotoResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET(".")
    Call<PhotoResponse> getPhotos(@retrofit2.http.Query("key") String key, @retrofit2.http.Query("q") String q, @retrofit2.http.Query("image_type") String image_type, @retrofit2.http.Query("page") int page,@retrofit2.http.Query("orientation") String orientation, @retrofit2.http.Query("pretty") boolean pretty);

}
