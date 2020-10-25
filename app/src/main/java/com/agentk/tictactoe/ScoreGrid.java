package com.agentk.tictactoe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import static com.agentk.tictactoe.PrincipalPage.getDefaults;

/**
 * Created by AgentK on 21/12/2017.
 */

public class ScoreGrid extends BaseAdapter {
    private Context mContext;
    private final List<Score> score;


    public ScoreGrid(Context c,List<Score> score ) {
        mContext = c;
        this.score = score;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return score.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid = convertView;
        TextView won,draw,lost,textView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Score sc;
        String nLevel;
        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_score, null);
            textView = grid.findViewById(R.id.gridText);
            won = grid.findViewById(R.id.textView2);
            draw = grid.findViewById(R.id.textView4);
            lost = grid.findViewById(R.id.textView6);

            sc=score.get(position);
            nLevel= mContext.getResources().getString(R.string.lvl)+" "+String.valueOf(sc.getID());
            textView.setText(nLevel);
            won.setText(sc.getWon());
            draw.setText(sc.getDraw());
            lost.setText(sc.getLost());

        } else {
            textView = grid.findViewById(R.id.gridText);
            won = grid.findViewById(R.id.textView2);
            draw = grid.findViewById(R.id.textView4);
            lost = grid.findViewById(R.id.textView6);

            sc=score.get(position);
            nLevel= mContext.getResources().getString(R.string.lvl)+" "+String.valueOf(sc.getID());
            textView.setText(nLevel);
            won.setText(sc.getWon());
            draw.setText(sc.getDraw());
            lost.setText(sc.getLost());
        }
        return grid;
    }
}