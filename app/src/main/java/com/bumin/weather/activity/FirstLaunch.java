package com.bumin.weather.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bumin.weather.R;
import com.bumin.weather.app.MyContextWrapper;
import com.bumin.weather.fragment.FirstLaunchFragment;
import com.bumin.weather.preferences.Prefs;

public class FirstLaunch extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = MyContextWrapper.wrap(newBase, new Prefs(newBase).getLanguage());
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment , new FirstLaunchFragment())
                .commit();
        setSupportActionBar(toolbar);
    }
}
