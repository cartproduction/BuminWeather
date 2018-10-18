package com.bumin.weather.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumin.weather.BuildConfig;
import com.bumin.weather.R;
import com.bumin.weather.activity.WeatherActivity;
import com.bumin.weather.app.MyApplication;
import com.bumin.weather.preferences.Prefs;
import com.bumin.weather.utils.Constants;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SmallWidgetProvider extends AbstractWidgetProvider {

    private static final String TAG = "SmallWidgetProvider";

    private static final String ACTION_UPDATE_TIME = "com.bumin.weather.UPDATE_TIME";

    private static final long DURATION_MINUTE = TimeUnit.SECONDS.toMillis(30);

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            Log.i("In" , "New Widget Provider");
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_small);

            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_button_refresh, pendingIntent);

            intent = new Intent(context, WeatherActivity.class);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            String temperatureScale = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREF_TEMPERATURE_UNITS , Constants.METRIC).equals(Constants.METRIC) ? context.getString(R.string.c) : context.getString(R.string.f);
            String temperature = String.format(Locale.getDefault(), "%.0s", MyApplication.weatherData.getCurrentCondition().get(0).getTempC());

            remoteViews.setTextViewText(R.id.widget_city, MyApplication.weatherData.getRequest().get(0).getQuery());
            remoteViews.setTextViewText(R.id.widget_temperature, temperature + temperatureScale);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        scheduleNextUpdate(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context.getPackageName(), getClass().getName());
        int ids[] = appWidgetManager.getAppWidgetIds(provider);
        onUpdate(context, appWidgetManager, ids);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        Log.d(TAG, "Disable simple widget updates");
        cancelUpdate(context);
    }

    private static void scheduleNextUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long now = new Date().getTime();
        long nextUpdate = now + Long.parseLong(new Prefs(context).getTime());
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Next widget update: " +
                    android.text.format.DateFormat.getTimeFormat(context).format(new Date(nextUpdate)));
        }
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC, nextUpdate, getTimeIntent(context));
        } else {
            alarmManager.set(AlarmManager.RTC, nextUpdate, getTimeIntent(context));
        }
    }

    private static void cancelUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getTimeIntent(context));
    }

    private static PendingIntent getTimeIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_UPDATE_TIME);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
