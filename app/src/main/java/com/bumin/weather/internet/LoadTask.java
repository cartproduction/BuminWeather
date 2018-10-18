package com.bumin.weather.internet;

import android.app.Activity;
import android.os.AsyncTask;

import com.bumin.weather.app.ApiInterface;
import com.bumin.weather.app.MyApplication;
import com.bumin.weather.cards.RetrofitAdapter;
import com.bumin.weather.model.Data;
import com.bumin.weather.model.Example;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class LoadTask extends AsyncTask<Object, Object, Object> {


    Activity act;
    public int resCount = 0;
    public String city;


    public LoadTask(Activity act,String city) {
        super();
        this.act = act;
        this.city = city;

    }

    @Override
    protected void onPreExecute() {


    }

    @Override
    protected Object doInBackground(Object... currentPage) {

        ApiInterface apiService = RetrofitAdapter.getClient().create(ApiInterface.class);


        Call<Example> user = apiService.getWeatherData(MyApplication.WEB_API_KEY, city);

        user.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                System.out.println(response.toString());

                MyApplication.weatherData = response.body().getData();

                resCount++;
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });



        while(resCount!=1){
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    @Override
    protected void onPostExecute(Object newsItems) {



    }

}