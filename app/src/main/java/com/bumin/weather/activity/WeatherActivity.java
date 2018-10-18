package com.bumin.weather.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bumin.weather.app.ApiInterface;
import com.bumin.weather.app.MyApplication;
import com.bumin.weather.cards.RetrofitAdapter;
import com.bumin.weather.fragment.MapsFragment;

import android.util.Log;
import android.view.View;

import com.bumin.weather.BuildConfig;
import com.bumin.weather.R;
import com.bumin.weather.activity.settings.SettingsActivity;
import com.bumin.weather.app.MyContextWrapper;
import com.bumin.weather.fragment.WeatherFragment;
import com.bumin.weather.model.Data;
import com.bumin.weather.model.Example;
import com.bumin.weather.preferences.DBHelper;
import com.bumin.weather.preferences.Prefs;
import com.bumin.weather.utils.Constants;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shortbread.Shortbread;
import shortbread.Shortcut;

import static java.lang.Thread.sleep;

public class WeatherActivity extends AppCompatActivity {
    Prefs preferences;
    WeatherFragment wf;
    public int resCount = 0;
    //MapsFragment mf;
    Toolbar toolbar;
    Drawer drawer;
    ProgressDialog pd;
    NotificationManagerCompat mManager;
    Handler handler;
    FloatingActionButton fab;
    DBHelper dbHelper;
    Fragment f;

    int mode = 0;

    @Shortcut(id = "home", icon = R.drawable.shortcut_home, shortLabel = "Home", rank = 2)
    public void addWeather() {

    }


    /*@Shortcut(id = "maps", icon = R.drawable.shortcut_map, shortLabel = "Weather Maps")
    public void addMaps() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawer.setSelectionAtPosition(3);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, mf)
                        .commit();
            }
        }, 750);
    }*/

    public void hideFab() {
        fab.hide();
        findViewById(R.id.fabProgressCircle).setVisibility(View.INVISIBLE);
        ((FABProgressCircle) findViewById(R.id.fabProgressCircle)).hide();
    }

    public void showFab() {
        fab.show();
        findViewById(R.id.fabProgressCircle).setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        } , 500);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = MyContextWrapper.wrap(newBase, new Prefs(newBase).getLanguage());
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Log.i("Activity", WeatherActivity.class.getSimpleName());
        mManager = NotificationManagerCompat.from(this);
        preferences = new Prefs(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFab();
                showInputDialog();
            }
        });
        Intent intent = getIntent();
        handler = new Handler();
        fab.show();
        wf = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("mode", intent.getIntExtra(Constants.MODE, 0));
        wf.setArguments(bundle);
        //mf = new MapsFragment();
        dbHelper = new DBHelper(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, wf)
                .commit();
        initDrawer();
     }

    private void showInputDialog() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.change_city))
                .content(getString(R.string.enter_zip_code))
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        fab.show();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        fab.show();
                    }
                })
                .negativeText(getString(android.R.string.cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        showFab();
                    }
                })
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, @NonNull CharSequence input) {
                        changeCity(input.toString());
                        showFab();
                    }
                }).show();
    }

    private void showCityDialog() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.drawer_item_add_city))
                .content(getString(R.string.pref_add_city_content))
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .negativeText(getString(android.R.string.cancel))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, @NonNull CharSequence input) {
                        checkForCity(input.toString());
                    }
                }).show();
    }

    int i = 5;

    private void checkForCity(String city) {

        final Context context = this;

        new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {

                pd = new ProgressDialog(WeatherActivity.this);
                pd.setMessage("Loading..");
                pd.setCancelable(false);
                pd.show();

            }

            @Override
            protected String doInBackground(String... params) {

                ApiInterface apiService = RetrofitAdapter.getClient().create(ApiInterface.class);


                Call<Example> user = apiService.getWeatherData(MyApplication.WEB_API_KEY,params[0] );

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



                while(resCount != 1){
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                pd.dismiss();

                new Thread() {
                    @Override
                    public void run() {



                        if (MyApplication.weatherData == null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new MaterialDialog.Builder(context)
                                            .title(getString(R.string.city_not_found))
                                            .content(getString(R.string.city_not_found))
                                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .negativeText(getString(android.R.string.ok))
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            });
                        } else {
                            if (dbHelper.cityExists(MyApplication.weatherData.getRequest().get(0).getQuery())) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new MaterialDialog.Builder(context)
                                                .title(getString(R.string.city_already_exists))
                                                .content(getString(R.string.need_not_add))
                                                .negativeText(getString(android.R.string.ok))
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    }
                                });
                            }
                            else {
                                dbHelper.addCity(MyApplication.weatherData.getRequest().get(0).getQuery());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SecondaryDrawerItem itemx = new SecondaryDrawerItem().withName(MyApplication.weatherData.getRequest().get(0).getQuery())
                                                .withIcon(new IconicsDrawable(context)
                                                        .icon(GoogleMaterial.Icon.gmd_place))
                                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                                    @Override
                                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                                        if (!(f instanceof WeatherFragment)) {
                                                            wf = new WeatherFragment().setCity(MyApplication.weatherData.getRequest().get(0).getQuery());
                                                            getSupportFragmentManager().beginTransaction()
                                                                    .replace(R.id.fragment, wf)
                                                                    .commit();
                                                        }
                                                        return true;
                                                    }
                                                });
                                        drawer.addItemAtPosition(itemx, ++i);
                                    }
                                });
                            }
                        }
                    }
                }.start();

            }
        }.execute(city);



    }

    public void createShortcuts() {
        if (mode == 0) {
            Shortbread.create(this);
            mode = -1;
        }
    }

    public void changeCity(String city){
        WeatherFragment wf = (WeatherFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        wf.changeCity(city);
        new Prefs(this).setCity(city);
    }

    public void initDrawer() {
        final IProfile profile = new ProfileDrawerItem().withName(getString(R.string.app_name))
                .withEmail("Version : " + BuildConfig.VERSION_NAME)
                .withIcon(R.mipmap.ic_launcher_x);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withTextColor(ContextCompat.getColor(this , R.color.md_amber_400))
                .addProfiles(
                        profile
                )
                .withSelectionListEnabled(false)
                .withProfileImagesClickable(false)
                .build();
        SecondaryDrawerItem item1 = new SecondaryDrawerItem().withName(R.string.drawer_item_home)
                .withIcon(new IconicsDrawable(this)
                        .icon(WeatherIcons.Icon.wic_day_sunny))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        wf = new WeatherFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, wf)
                                .commit();
                        return true;
                    }
                });

        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withName(R.string.drawer_item_add_city)
                .withIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_add_location))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        showCityDialog();
                        return true;
                    }
                })
                .withSelectable(false);

        SecondaryDrawerItem item9 = new SecondaryDrawerItem().withName(R.string.settings)
                .withIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_settings))
                .withSelectable(false)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(WeatherActivity.this, SettingsActivity.class));
                        return true;
                    }
                });
        DrawerBuilder drawerBuilder = new DrawerBuilder();
        drawerBuilder
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item4
                )
                .addStickyDrawerItems(
                        item9);
        List<String> cities = dbHelper.getCities();
        final ListIterator<String> listIterator = cities.listIterator(cities.size());
        while (listIterator.hasPrevious()) {
            final String city = listIterator.previous();
            drawerBuilder.addDrawerItems(new SecondaryDrawerItem().withName(city)
                    .withIcon(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_place))
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            wf = new WeatherFragment().setCity(city);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment, wf)
                                    .commit();
                            return true;
                        }
                    })
            );
        }
        drawer = drawerBuilder.build();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        }
        else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private static final String DESCRIBABLE_KEY = "describable_key";

}
