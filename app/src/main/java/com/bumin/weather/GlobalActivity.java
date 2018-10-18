package com.bumin.weather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumin.weather.activity.FirstLaunch;
import com.bumin.weather.activity.WeatherActivity;
import com.bumin.weather.preferences.Preferences;
import com.bumin.weather.preferences.Prefs;

import static java.lang.Thread.sleep;

public class GlobalActivity extends AppCompatActivity {

    public static Preferences cp;
    public static Prefs prefs;
    public static int i = 0;
    public int resCount = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);
        Log.i("Loaded" , "Global");
    }

    @Override
    protected void onResume() {
        cp = new Preferences(this);
        prefs = new Prefs(this);
        super.onResume();

        if (!cp.getPrefs().getBoolean("first" , true)) {
            prefs.setLaunched();
            prefs.setCity(cp.getCity());


        }

        super.onResume();



        Intent intent;

        if (prefs.getLaunched()) {
            intent = new Intent(GlobalActivity.this, FirstLaunch.class);
        }
        else {

            intent = new Intent(GlobalActivity.this, WeatherActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
