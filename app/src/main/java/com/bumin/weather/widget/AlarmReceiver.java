package com.bumin.weather.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bumin.weather.internet.CheckConnection;
import com.bumin.weather.preferences.Prefs;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            setRecurringAlarm(context);
            getWeather();
        }
        else {
            getWeather();
        }
    }

    private void getWeather() {
        Log.d("Alarm", "Recurring alarm; requesting download service.");
        if (isNetworkAvailable()) {
            new GetWeatherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private boolean isNetworkAvailable() {
        return new CheckConnection(context).isNetworkAvailable();
    }

    public class GetWeatherTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {

                Prefs prefs = new Prefs(context);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            return null;
        }

        protected void onPostExecute(Void v) {
            // Update widgets
            AbstractWidgetProvider.updateWidgets(context);
        }
    }

    public static void setRecurringAlarm(Context context) {
        Intent refresh = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringRefresh = PendingIntent.getBroadcast(context,
                0, refresh, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        long intervalMillis = Long.parseLong(new Prefs(context).getTime());
        if (intervalMillis == 0) {
            // Cancel previous alarm
            alarms.cancel(recurringRefresh);
        } else {
            alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + intervalMillis,
                    intervalMillis,
                    recurringRefresh);
        }
    }
}