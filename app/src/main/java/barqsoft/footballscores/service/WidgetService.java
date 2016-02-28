package barqsoft.footballscores.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.provider.SyncStateContract;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.Constant;
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by manuel on 25/2/16.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScoreListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

 class ScoreListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;

    public ScoreListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public int getCount() {
        if (mCursor != null) return mCursor.getCount();
        return 0;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }

        Date fragmentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] selectionArgs = new String[]{mFormat.format(fragmentDate)};

        final long token = Binder.clearCallingIdentity();
        try {
            mCursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                    null,null, selectionArgs,null);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) mCursor.close();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Get the data for this position from the content provider

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item);

        if (mCursor.moveToPosition(position)) {

            rv.setTextViewText(R.id.home_name, mCursor.getString(Constant.COL_HOME));
            rv.setTextViewText(R.id.away_name, mCursor.getString(Constant.COL_AWAY));
            rv.setTextViewText(R.id.data_textview, mCursor.getString(Constant.COL_DATE));
            rv.setTextViewText(R.id.score_textview, Utilies.getScores(mCursor.getInt(Constant.COL_HOME_GOALS), mCursor.getInt(Constant.COL_AWAY_GOALS)));
            rv.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                    mCursor.getString(Constant.COL_HOME)));
            rv.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                    mCursor.getString(Constant.COL_AWAY)));
            rv.setImageViewResource(R.id.home_crest, R.drawable.ic_launcher);
            rv.setImageViewResource(R.id.away_crest, R.drawable.ic_launcher);

        }

            // Set the click intent so that we can handle it and show a toast message
            final Intent fillInIntent = new Intent();
            rv.setOnClickFillInIntent(R.id.widget, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
