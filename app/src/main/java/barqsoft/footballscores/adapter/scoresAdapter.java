package barqsoft.footballscores.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.utils.Constant;
import barqsoft.footballscores.utils.Utilies;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class scoresAdapter extends CursorAdapter
{

    private int detailMatchId = 0;

    public scoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        itemView.setTag(holder);
        return itemView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.homeName.setText(cursor.getString(Constant.COL_HOME));
        viewHolder.awayName.setText(cursor.getString(Constant.COL_AWAY));
        viewHolder.date.setText(cursor.getString(Constant.COL_MATCHTIME));
        viewHolder.score.setText(Utilies.getScores(cursor.getInt(Constant.COL_HOME_GOALS), cursor.getInt(Constant.COL_AWAY_GOALS)));
        viewHolder.matchId = (int) cursor.getDouble(Constant.COL_ID);
        viewHolder.homeCrest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(Constant.COL_HOME)));
        viewHolder.awayCrest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(Constant.COL_AWAY)
        ));

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = viewHolder.frameLayout;
        if(viewHolder.matchId == detailMatchId) {
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilies.getMatchDay(cursor.getInt(Constant.COL_MATCHDAY),
                    cursor.getInt(Constant.COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilies.getLeague(cursor.getInt(Constant.COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(viewHolder.homeName.getText()+" "
                            +viewHolder.score.getText()+" "+viewHolder.awayName.getText() + " "));
                }
            });
        } else {
            container.removeAllViews();
        }

    }
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + Constant.FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

    public void setDetailMatchId(int detailMatchId) {
        this.detailMatchId = detailMatchId;
    }

    public class ViewHolder
    {
        public TextView homeName;
        public TextView awayName;
        public TextView score;
        public TextView date;
        public ImageView homeCrest;
        public ImageView awayCrest;
        public int matchId;
        public FrameLayout frameLayout;

        public ViewHolder(View view)
        {
            homeName = (TextView) view.findViewById(R.id.home_name);
            awayName = (TextView) view.findViewById(R.id.away_name);
            score     = (TextView) view.findViewById(R.id.score_textview);
            date      = (TextView) view.findViewById(R.id.data_textview);
            homeCrest = (ImageView) view.findViewById(R.id.home_crest);
            awayCrest = (ImageView) view.findViewById(R.id.away_crest);
            frameLayout = (FrameLayout) view.findViewById(R.id.details_fragment_container);
        }
    }
}
