package com.agentk.tictactoe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.agentk.tictactoe.PrincipalPage.getDefaults;

/**
 * Created by AgentK on 21/12/2017.
 */

public class LevelGrid extends BaseAdapter {
    private Context mContext;
    private final String[] web;


    public LevelGrid(Context c,String[] web ) {
        mContext = c;

        this.web = web;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.length;
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
        View grid = (View) convertView;
        TextView textView;
        ImageView imageView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_simple, null);
            textView = (TextView) grid.findViewById(R.id.gridText);
            imageView = (ImageView)grid.findViewById(R.id.gridImage);

            textView.setText(web[position]);

        } else {
            textView = (TextView)convertView.findViewById(R.id.gridText);
            imageView = (ImageView)convertView.findViewById(R.id.gridImage);

            textView.setText(web[position]);
        }

        if(position==1)  imageView.setBackgroundResource(R.drawable.level2_g);
        if(position==2)  imageView.setBackgroundResource(R.drawable.level3_g);
        if(position==3)  imageView.setBackgroundResource(R.drawable.level4_g);
        if(position==4)  imageView.setBackgroundResource(R.drawable.level5_g);
        if(position==5)  imageView.setBackgroundResource(R.drawable.level6_g);
        if(position==6)  imageView.setBackgroundResource(R.drawable.level7_g);
        if(position==7)  imageView.setBackgroundResource(R.drawable.level8_g);
        if(position==8)  imageView.setBackgroundResource(R.drawable.level9_g);
        if(position==9)  imageView.setBackgroundResource(R.drawable.level10_g);
        if(position==10)  imageView.setBackgroundResource(R.drawable.level11_g);
        if(position==11)  imageView.setBackgroundResource(R.drawable.level12_g);
        if(position==12)  imageView.setBackgroundResource(R.drawable.level13_g);
        if(position==13)  imageView.setBackgroundResource(R.drawable.level14_g);
        if(position==14)  imageView.setBackgroundResource(R.drawable.level15_g);

        if(position==0&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level1);
        if(position==1&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level2);
        if(position==2&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level3);
        if(position==3&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level4);
        if(position==4&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level5);
        if(position==5&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level6);
        if(position==6&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level7);
        if(position==7&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level8);
        if(position==8&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level9);
        if(position==9&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level10);
        if(position==10&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level11);
        if(position==11&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level12);
        if(position==12&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level13);
        if(position==13&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level14);
        if(position==14&&position<LevelActivity.levelActual)  imageView.setBackgroundResource(R.drawable.level15);


        return grid;
    }
}