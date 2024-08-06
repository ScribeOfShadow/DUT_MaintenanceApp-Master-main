package com.example.dutmaintenance;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class DutMaintenceWidgetprovider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds){
            Intent intent = new Intent(context,SplashScreen.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,intent,0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.maintanence_widget);
            views.setOnClickPendingIntent(R.id.btn_Widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
