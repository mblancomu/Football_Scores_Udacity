package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;
import android.widget.Toast;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.activity.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.WidgetService;

/**
 * Created by manuel on 25/2/16.
 */
public class FootballCollectionWidget extends AppWidgetProvider {
    public static String CLICK_ACTION = "barqsoft.footballscores.widget.LIST_CLICK";
    private static HandlerThread sWorkerThread;
    private static Handler sWorkerQueue;
    private static ScoreDataProviderObserver sDataObserver;

    public FootballCollectionWidget() {
        sWorkerThread = new HandlerThread("FootballCollectionWidget-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onEnabled(Context context) {
        final ContentResolver r = context.getContentResolver();
        if (sDataObserver == null) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, FootballCollectionWidget.class);
            sDataObserver = new ScoreDataProviderObserver(mgr, cn, sWorkerQueue);
            r.registerContentObserver(DatabaseContract.scores_table.buildScoreWithDate(), true, sDataObserver);
        }
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(CLICK_ACTION)) {
            intent.setClass(ctx, MainActivity.class);
            ctx.startActivity(intent);
        }

        super.onReceive(ctx, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Toast.makeText(context, "widget onupdate", Toast.LENGTH_SHORT).show();
        // Update each of the widgets with the remote adapter

        for (int i = 0; i < appWidgetIds.length; ++i) {
            final Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item);
            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                rv.setRemoteAdapter(R.id.scores_list, intent);
            } else {
                rv.setRemoteAdapter(appWidgetIds[i], R.id.scores_list, intent);
            }

            rv.setEmptyView(R.id.scores_list, R.id.empty_view);

            Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetIds[i]);

            int category = options.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
            boolean isLockScreen = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;

            final Intent onClickIntent = new Intent(context, FootballCollectionWidget.class);
            onClickIntent.setAction(FootballCollectionWidget.CLICK_ACTION);
            onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                    onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.scores_list, onClickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(),
                WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        // Update the widgets via the service
        context.startService(intent);
    }
}

class ScoreDataProviderObserver extends ContentObserver {
    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;

    ScoreDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }

    @Override
    public void onChange(boolean selfChange) {
        mAppWidgetManager.notifyAppWidgetViewDataChanged(
                mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.scores_list);
    }
}
