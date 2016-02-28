package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.activity.MainActivity;
import barqsoft.footballscores.utils.Constant;
import barqsoft.footballscores.utils.Utilies;

/**
 * Created by manuel on 23/2/16.
 */
public class FootballWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.scores_list_item);

            views.setTextViewText(R.id.home_name, "Sevilla");
            views.setTextViewText(R.id.away_name, "Levante");
            views.setTextViewText(R.id.score_textview, "5 - 0");
            views.setTextViewText(R.id.data_textview, "2016-2-29");
            views.setImageViewResource(R.id.home_crest, R.drawable.ic_launcher);
            views.setImageViewResource(R.id.away_crest, R.drawable.ic_launcher);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
