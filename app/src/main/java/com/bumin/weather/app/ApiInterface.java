package com.bumin.weather.app;


import com.bumin.weather.model.Data;
import com.bumin.weather.model.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ETMaster on 23/06/2017.
 */

public interface ApiInterface {


    @GET(MyApplication.OTHER_NODES)
    Call<Example> getWeatherData(@Query("key") String key, @Query("q") String city);

}