package com.agentk.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agentk.tictactoe.model.RowItem;

import java.util.List;

/**
 * Created by AgentK on 19/03/2018.
 */

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {


    Context context;

    public CustomListViewAdapter(Context context, int resourceId, //resourceId=your layout
                                 List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lge_view, null);
            holder = new ViewHolder();
            holder.txtTitle =  convertView.findViewById(R.id.text1);
            holder.imageView = convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(rowItem.getTitle());
        holder.imageView.setImageResource(rowItem.getImageId());

        return convertView;
    }
}
