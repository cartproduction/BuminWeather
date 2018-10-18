package com.bumin.weather.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import com.bumin.weather.app.ApiInterface;
import com.bumin.weather.app.MyApplication;
import com.bumin.weather.cards.RetrofitAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumin.weather.GlobalActivity;
import com.bumin.weather.R;
import com.bumin.weather.activity.FirstLaunch;
import com.bumin.weather.activity.WeatherActivity;
import com.bumin.weather.internet.CheckConnection;
import com.bumin.weather.model.Data;
import com.bumin.weather.model.Example;
import com.bumin.weather.model.Weather;
import com.bumin.weather.permissions.GPSTracker;
import com.bumin.weather.permissions.Permissions;
import com.bumin.weather.preferences.Prefs;
import com.bumin.weather.utils.Constants;
import com.bumin.weather.utils.Utils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static java.lang.Thread.sleep;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;
    @BindView(R.id.button1) TextView button;
    @BindView(R.id.weather_icon11) TextView weatherIcon;
    @BindView(R.id.wind_view) TextView windView;
    @BindView(R.id.humidity_view) TextView humidityView;
    @BindView(R.id.direction_view) TextView directionView;
    @BindView(R.id.daily_view) TextView dailyView;
    @BindView(R.id.updated_field) TextView updatedField;
    @BindView(R.id.city_field) TextView cityField;
    @BindView(R.id.sunrise_view) TextView sunriseView;
    @BindView(R.id.sunset_view) TextView sunsetView;
    @BindView(R.id.sunrise_icon) TextView sunriseIcon;
    @BindView(R.id.sunset_icon) TextView sunsetIcon;
    @BindView(R.id.wind_icon) TextView windIcon;
    @BindView(R.id.humidity_icon) TextView humidityIcon;
    @BindView(R.id.horizontal_recycler_view) RecyclerView horizontalRecyclerView;
    LinearLayoutManager horizontalLayoutManager;
    double tc;
    public int resCount = 0;
    Handler handler;
    BottomSheetDialogFragment bottomSheetDialogFragment;
    @BindView(R.id.swipe) SwipeRefreshLayout swipeView;
    FloatingActionButton fab;
    FABProgressCircle fabProgressCircle;

    CheckConnection cc;
    String citys = null;
    MaterialDialog pd;
    ProgressDialog pdd;
    Prefs preferences;
    GPSTracker gps;
    View rootView;
    Permissions permission;

    public WeatherFragment() {
        handler = new Handler();
    }

    public WeatherFragment setCity(String city) {
        this.citys = city;
        return this;
    }

    public String getCity() {
        return citys;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.bind(this , rootView);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this.activity())
                .title(getString(R.string.please_wait))
                .content(getString(R.string.loading))
                .cancelable(false)
                .progress(true , 0);
        pd = builder.build();
        setHasOptionsMenu(true);
        preferences = new Prefs(context());
        weatherFont = Typeface.createFromAsset(activity().getAssets(), "fonts/weather-icons-v2.0.10.ttf");
        fab = ((WeatherActivity) activity()).findViewById(R.id.fab);
        Bundle bundle = getArguments();
        int mode;
        if (bundle != null)
            mode = bundle.getInt(Constants.MODE , 0);
        else
            mode = 0;

        if (mode == 0) {

            if(citys == null)

                updateWeatherData(preferences.getCity(), null, null);
            else
                updateWeatherData(citys, null, null);
        }else
            updateWeatherData(null , Float.toString(preferences.getLatitude()) , Float.toString(preferences.getLongitude()));
        gps = new GPSTracker(context());
        cityField.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        updatedField.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        humidityView.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        sunriseIcon.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        sunriseIcon.setTypeface(weatherFont);
        sunriseIcon.setText(activity().getString(R.string.sunrise_icon));
        sunsetIcon.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        sunsetIcon.setTypeface(weatherFont);
        sunsetIcon.setText(activity().getString(R.string.sunset_icon));
        windIcon.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        windIcon.setTypeface(weatherFont);
        windIcon.setText(activity().getString(R.string.speed_icon));
        humidityIcon.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        humidityIcon.setTypeface(weatherFont);
        humidityIcon.setText(activity().getString(R.string.humidity_icon));
        windView.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        swipeView.setColorSchemeResources(R.color.red, R.color.green , R.color.blue , R.color.yellow , R.color.orange);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changeCity(preferences.getCity());
                        swipeView.setRefreshing(false);
                    }
                });
            }
        });
        horizontalLayoutManager
                = new LinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        horizontalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (horizontalLayoutManager.findLastVisibleItemPosition() == 9 || citys != null)
                    fab.hide();
                else
                    fab.show();
            }
        });
        directionView.setTypeface(weatherFont);
        directionView.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        dailyView.setText(getString(R.string.daily));
        dailyView.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        sunriseView.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        sunsetView.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        button.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        pd.show();
        horizontalRecyclerView.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
        weatherIcon.setTypeface(weatherFont);
        weatherIcon.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        if (citys == null)
            ((WeatherActivity) activity()).showFab();
        else
            ((WeatherActivity) activity()).hideFab();
        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            cc = new CheckConnection(context());
            if (!cc.isNetworkAvailable())
                showNoInternet();
            else {
                pd.show();
                updateWeatherData(preferences.getCity(), null, null);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gps.stopUsingGPS();
    }

    private void updateWeatherData(String city, final String lat, final String lon) {
        if (citys == null)
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            } , 50);


        new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {

                pdd = new ProgressDialog(getActivity());
                pdd.setCancelable(false);
                pdd.show();

            }

            @Override
            protected String doInBackground(String... params) {


                ApiInterface apiService = RetrofitAdapter.getClient().create(ApiInterface.class);

                Call<Example> user = apiService.getWeatherData(MyApplication.WEB_API_KEY, params[0]);

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

                pdd.dismiss();
                resCount=0;
                new Thread() {
                    public void run() {



                        if (pd.isShowing())
                            pd.dismiss();
                        if (MyApplication.weatherData == null) {
                            preferences.setCity(preferences.getLastCity());
                            handler.post(new Runnable() {
                                public void run() {
                                    GlobalActivity.i = 1;
                                    if (!preferences.getLaunched()) {
                                        FirstStart();
                                    } else {
                                        if (citys == null)
                                            //fabProgressCircle.hide();
                                            cc = new CheckConnection(context());
                                        if (!cc.isNetworkAvailable()) {
                                            showNoInternet();
                                        }
                                        else {
                                            if (pd.isShowing())
                                                pd.dismiss();
                                            showInputDialog();
                                        }
                                    }
                                }
                            });
                        }
                        else {
                            handler.post(new Runnable() {
                                public void run() {
                                    preferences.setLaunched();
                                    renderWeather();
                                    if (!preferences.getv3TargetShown())
                                        showTargets();
                                    if (pd.isShowing())
                                        pd.dismiss();
                                    if (citys == null) {
                                        preferences.setLastCity(MyApplication.weatherData.getRequest().get(0).getQuery());
                                        ((WeatherActivity) activity()).createShortcuts();
                                        progress();
                                    }
                                    else
                                        preferences.setLastCity(preferences.getLastCity());

                                }
                            });
                        }
                    }
                }.start();

            }
        }.execute(city);

    }

    private void progress() {
        //fabProgressCircle.onArcAnimationComplete();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               // fabProgressCircle.hide();
            }
        } , 500);
    }

    public void FirstStart() {
        if (pd.isShowing())
            pd.dismiss();
        Intent intent = new Intent(activity(), FirstLaunch.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void changeCity(String city)
    {
        updateWeatherData(city, null, null);
        preferences.setCity(city);
    }

    public void changeCity(String lat , String lon)
    {
        ((WeatherActivity) activity()).showFab();
        updateWeatherData(null, lat, lon);
    }
    
    private Context context() {
        return getContext();
    }
    
    private FragmentActivity activity() {
        return getActivity();
    }

    private void showInputDialog() {
            new MaterialDialog.Builder(activity())
                    .title(getString(R.string.change_city))
                    .content(getString(R.string.could_not_find))
                    .negativeText(getString(android.R.string.cancel))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog , @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .input(null, null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, @NonNull CharSequence input) {
                            changeCity(input.toString());
                        }
                    })
                    .cancelable(false)
                    .show();
    }

    private void showTargets() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new MaterialTapTargetPrompt.Builder(activity())
                            .setTarget(R.id.fab)
                            .setBackgroundColour(ContextCompat.getColor(context() , R.color.md_light_blue_400))
                            .setFocalColour(ContextCompat.getColor(context() , R.color.colorAccent))
                            .setPrimaryText(getString(R.string.target_search_title))
                            .setSecondaryText(getString(R.string.target_search_content))
                            .setIconDrawableColourFilter(ContextCompat.getColor(context() , R.color.md_black_1000))
                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                @Override
                                public void onPromptStateChanged(MaterialTapTargetPrompt prompt , int state) {
                                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                                        showRefresh();
                                }
                            })
                            .show();
                }
            }, 4500);
    }

    private void showLocTarget() {
        new MaterialTapTargetPrompt.Builder(activity())
                .setTarget(R.id.location)
                .setBackgroundColour(ContextCompat.getColor(context() , R.color.md_light_blue_400))
                .setPrimaryText(getString(R.string.location))
                .setFocalColour(ContextCompat.getColor(context() , R.color.colorAccent))
                .setSecondaryText(getString(R.string.target_location_content))
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt , int state) {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                            preferences.setv3TargetShown(true);
                    }
                })
                .show();
    }

    private void showRefresh() {
        new MaterialTapTargetPrompt.Builder(activity())
                .setTarget(R.id.toolbar)
                .setBackgroundColour(ContextCompat.getColor(context() , R.color.md_light_blue_400))
                .setPrimaryText(getString(R.string.target_refresh_title))
                .setFocalColour(ContextCompat.getColor(context() , R.color.colorAccent))
                .setSecondaryText(getString(R.string.target_refresh_content))
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt , int state) {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSING)
                            showLocTarget();
                    }
                })
                .show();
    }

    public void showNoInternet() {
        new MaterialDialog.Builder(context())
                .title(getString(R.string.no_internet_title))
                .cancelable(false)
                .content(getString(R.string.no_internet_content))
                .positiveText(getString(R.string.no_internet_mobile_data))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivityForResult(intent , 0);
                    }
                })
                .negativeText(getString(R.string.no_internet_wifi))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS) , 0);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode ,
                                           @NonNull String permissions[] ,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCity();
                } else {
                    permission.permissionDenied();
                }
                break;
            }
        }
    }

    private void showCity() {
        gps = new GPSTracker(context());
        if (!gps.canGetLocation())
            gps.showSettingsAlert();
        else {
            String lat = gps.getLatitude();
            String lon = gps.getLongitude();
            changeCity(lat, lon);
        }
    }

    private void renderWeather(){
        try {
            tc = Double.parseDouble(MyApplication.weatherData.getCurrentCondition().get(0).getTempC());
            if (citys == null)
                preferences.setCity(MyApplication.weatherData.getRequest().get(0).getQuery());
            int a = (int) Math.round(Double.parseDouble(MyApplication.weatherData.getCurrentCondition().get(0).getTempC()));
            final String city = MyApplication.weatherData.getRequest().get(0).getQuery();
            cityField.setText(city);
            cityField.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v) {
                }
            });
            List<Weather> details = MyApplication.weatherData.getWeather();
            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(details);
            horizontalRecyclerView.setAdapter(horizontalAdapter);
            sunriseView.setText(MyApplication.weatherData.getWeather().get(0).getAstronomy().get(0).getSunrise());
            sunsetView.setText(MyApplication.weatherData.getWeather().get(0).getAstronomy().get(0).getSunset());
            DateFormat df = DateFormat.getDateTimeInstance();
            Date uptade = new Date();
            String updatedOn = "Last update: " + uptade;
           // updatedField.setText(updatedOn);
            final String humidity = getString(R.string.humidity_ , MyApplication.weatherData.getCurrentCondition().get(0).getHumidity());
            final String humidity1 = getString(R.string.humidity , MyApplication.weatherData.getCurrentCondition().get(0).getHumidity());
            humidityView.setText(humidity);
            humidityIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            humidityView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            final String wind = getString(R.string.wind , MyApplication.weatherData.getCurrentCondition().get(0).getWindspeedKmph() , PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.PREF_TEMPERATURE_UNITS , Constants.METRIC).equals(Constants.METRIC) ? getString(R.string.mps) : getString(R.string.mph));
            final String wind1 = getString(R.string.wind_ , MyApplication.weatherData.getCurrentCondition().get(0).getWindspeedKmph() , PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.PREF_TEMPERATURE_UNITS , Constants.METRIC).equals(Constants.METRIC) ? getString(R.string.mps) : getString(R.string.mph));
            windView.setText(wind);
            windIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            windView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            //Picasso.with(getActivity()).load(MyApplication.weatherData.getCurrentCondition().get(0).getWeatherIconUrl().get(0).getValue()).into(weatherIcon);
//200şimşek300yağmur500 yağmur600kar800 günes700 cloud
            int wid;
            switch (MyApplication.weatherData.getCurrentCondition().get(0).getWeatherDesc().get(0).getValue()){

                case "Cloudy":
                    wid = 700;
                    break;
                case "Partly cloudy":
                    wid = 700;
                    break;
                case "Patchy rain possible":
                    wid = 500;
                    break;
                case "Clear":
                    wid = 800;
                    break;
                default:
                    wid = 700;
                    break;


            }

            weatherIcon.setText(Utils.setWeatherIcon(context() , wid));
            weatherIcon.setOnClickListener(new View.OnClickListener()
            {
                public void onClick (View v)
                {
                    try {
                        String rs = MyApplication.weatherData.getCurrentCondition().get(0).getWeatherDesc().get(0).getValue();
                        String[] strArray = rs.split(" ");
                        StringBuilder builder = new StringBuilder();
                        for (String s : strArray) {
                            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                            builder.append(cap.concat(" "));
                        }
                        ;
                    }
                    catch (Exception e) {
                        Log.e("Error", "Main Weather Icon OnClick Details could not be loaded");
                    }
                }
            });
            String r1 = Integer.toString(a) + "°";
            button.setText(r1);
            int deg =  Integer.parseInt(MyApplication.weatherData.getCurrentCondition().get(0).getWinddirDegree());
            setDeg(deg);
        }catch(Exception e){
            Log.e(WeatherFragment.class.getSimpleName() , "One or more fields not found in the JSON data");
        }
    }

    private void setDeg(int deg) {
        int index = Math.abs(Math.round(deg % 360) / 45);
        switch (index) {
            case 0 : directionView.setText(activity().getString(R.string.top));
                setDirection(getString(R.string.north));
                break;
            case 1 : directionView.setText(activity().getString(R.string.top_right));
                setDirection(getString(R.string.north_east));
                break;
            case 2 : directionView.setText(activity().getString(R.string.right));
                setDirection(getString(R.string.east));
                break;
            case 3 : directionView.setText(activity().getString(R.string.bottom_right));
                setDirection(getString(R.string.south_east));
                break;
            case 4 : directionView.setText(activity().getString(R.string.down));
                setDirection(getString(R.string.south));
                break;
            case 5 : directionView.setText(activity().getString(R.string.bottom_left));
                setDirection(getString(R.string.south_west));
                break;
            case 6 : directionView.setText(activity().getString(R.string.left));
                setDirection(getString(R.string.west));
                break;
            case 7 : directionView.setText(activity().getString(R.string.top_left));
                setDirection(getString(R.string.north_west));
                break;
        }
    }

    private void setDirection(final String string) {
        directionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public static CustomBottomSheetDialogFragment newInstance(Weather describable) {
        CustomBottomSheetDialogFragment fragment = new CustomBottomSheetDialogFragment();
        Bundle bundle = new Bundle();
 //       bundle.putSerializable(DESCRIBABLE_KEY, describable);
        fragment.setArguments(bundle);

        return fragment;
    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
        private List<Weather> horizontalList;

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView details_view;
            ImageView weather_ic;

            MyViewHolder(View view) {
                super(view);
                weather_ic = view.findViewById(R.id.weather_icon);
                details_view = view.findViewById(R.id.details_view);
            }
        }

        HorizontalAdapter(List<Weather> horizontalList) {
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.weather_daily_list_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            String[] parts = horizontalList.get(position).getDate().split("-");
            String part1 = parts[0]; // 004
            String part2 = parts[1]; // 034556
            String part3 = parts[2];

            Date expiry = new Date(Integer.parseInt(part1),Integer.parseInt(part2),Integer.parseInt(part3));
            String date = new SimpleDateFormat("EE, dd" , new Locale(new Prefs(context()).getLanguage())).format(expiry);
            String line2 =
                    horizontalList.get(position).getMaxtempC() + "°" + "      ";
            String line3 = horizontalList.get(position).getMintempC() + "°";
            String fs = date + "\n" + line2 + line3 + "\n";
            SpannableString ss1 = new SpannableString(fs);
            ss1.setSpan(new RelativeSizeSpan(1.1f) , fs.indexOf(date) , date.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss1.setSpan(new RelativeSizeSpan(1.4f) , fs.indexOf(line2) , date.length() + line2.length() , 0);
            holder.details_view.setText(ss1);
            final int pos = position;
            holder.details_view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    bottomSheetDialogFragment = newInstance(horizontalList.get(pos));
                    bottomSheetDialogFragment.show(activity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                }
            });
            holder.weather_ic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    bottomSheetDialogFragment = newInstance(horizontalList.get(pos));
                    bottomSheetDialogFragment.show(activity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                }
            });

            Picasso.get().load(horizontalList.get(position).getHourly().get(4).getWeatherIconUrl().get(0).getValue()).into(holder.weather_ic);

            holder.details_view.setTextColor(ContextCompat.getColor(context() , R.color.textColor));
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }
}


