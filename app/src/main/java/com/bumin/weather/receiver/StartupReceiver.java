package com.bumin.weather.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bumin.weather.widget.LargeWidgetProvider;
import com.bumin.weather.widget.SmallWidgetProvider;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent2 = new Intent(context, LargeWidgetProvider.class);
            intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LargeWidgetProvider.class));
            intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent2);

            intent2 = new Intent(context, LargeWidgetProvider.class);
            intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, SmallWidgetProvider.class));
            intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent2);


        }
        else
            Log.i("No" , "Boot");
    }
}

