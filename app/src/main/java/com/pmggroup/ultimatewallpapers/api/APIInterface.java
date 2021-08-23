package com.pmggroup.ultimatewallpapers.api;



import com.pmggroup.ultimatewallpapers.api.response.PhotoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET(".")
    Call<PhotoResponse> getPhotos(@Query("key") String key, @Query("q") String q, @Query("image_type") String image_type, @Query("page") int page, @Query("orientation") String orientation, @Query("pretty")  boolean pretty, @Query("safesearch")  boolean safesearch,@Query("category") String category);

}
