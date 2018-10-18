package com.bumin.weather.fragment;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumin.weather.R;
import com.bumin.weather.app.MyApplication;
import com.bumin.weather.preferences.Prefs;
import com.bumin.weather.utils.Constants;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    TextView windIcon , rainIcon , snowIcon , humidityIcon , pressureIcon;
    TextView windText , rainText , snowText , humidityText , pressureText;
    TextView nightValue , mornValue , dayValue , eveValue;
    TextView condition;
    View rootView;
    Prefs preferences;
    Typeface weatherFont;
    private static final String DESCRIBABLE_KEY = Constants.DESCRIBABLE_KEY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather-icons-v2.0.10.ttf");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_modal , container, false);
        condition = rootView.findViewById(R.id.description);
        preferences = new Prefs(getContext());
        nightValue = rootView.findViewById(R.id.night_temperature);
        mornValue = rootView.findViewById(R.id.morning_temperature);
        dayValue = rootView.findViewById(R.id.day_temperature);
        eveValue = rootView.findViewById(R.id.evening_temperature);
        windIcon = rootView.findViewById(R.id.wind_icon);
        windIcon.setTypeface(weatherFont);
        windIcon.setText(getString(R.string.speed_icon));
        rainIcon = rootView.findViewById(R.id.rain_icon);
        rainIcon.setTypeface(weatherFont);
        rainIcon.setText(getString(R.string.rain));
        snowIcon = rootView.findViewById(R.id.snow_icon);
        snowIcon.setTypeface(weatherFont);
        snowIcon.setText(getString(R.string.snow));
        humidityIcon = rootView.findViewById(R.id.humidity_icon);
        humidityIcon.setTypeface(weatherFont);
        humidityIcon.setText(getString(R.string.humidity_icon));
        pressureIcon = rootView.findViewById(R.id.pressure_icon);
        pressureIcon.setTypeface(weatherFont);
        pressureIcon.setText(getString(R.string.pressure_icon));
        windText = rootView.findViewById(R.id.wind);
        rainText = rootView.findViewById(R.id.rain);
        snowText = rootView.findViewById(R.id.snow);
        humidityText = rootView.findViewById(R.id.humidity);
        pressureText = rootView.findViewById(R.id.pressure);
        updateElements();
        return rootView;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        //super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_modal, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);

        }
    }

    public void updateElements() {
        setCondition();
        setOthers();
        setTemperatures();
    }

    public void setCondition() {
            String cond = MyApplication.weatherData.getCurrentCondition().get(0).getWeatherDesc().get(0).getValue();
            String[] strArray = cond.split(" ");
            final StringBuilder builder = new StringBuilder();
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap.concat(" "));
            }
            condition.setText(builder.toString());
    }

    public void setOthers() {
        try {
            String wind = getString(R.string.wind_ , MyApplication.weatherData.getCurrentCondition().get(0).getWindspeedKmph() , PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.PREF_TEMPERATURE_UNITS , Constants.METRIC).equals(Constants.IMPERIAL) ? getString(R.string.mph) : getString(R.string.mps));
            windText.setText(wind);

            humidityText.setText(getString(R.string.humidity , MyApplication.weatherData.getCurrentCondition().get(0).getHumidity()));
            pressureText.setText(getString(R.string.pressure , MyApplication.weatherData.getCurrentCondition().get(0).getPressure()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTemperatures() {
        dayValue.setText(String.format("%s°" , MyApplication.weatherData.getWeather().get(0).getHourly().get(4).getTempC()));
        mornValue.setText(String.format("%s°" ,MyApplication.weatherData.getWeather().get(0).getHourly().get(3).getTempC()));
        eveValue.setText(String.format("%s°" , MyApplication.weatherData.getWeather().get(0).getHourly().get(5).getTempC()));
        nightValue.setText(String.format("%s°" , MyApplication.weatherData.getWeather().get(0).getHourly().get(7).getTempC()));
    }
}