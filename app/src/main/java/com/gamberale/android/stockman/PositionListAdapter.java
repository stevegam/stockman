package com.gamberale.android.stockman;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamberale.android.stockman.data.PortfolioContract.Position;

/**
 * Created by stevegam on 4/18/2015.
 */
public class PositionListAdapter extends CursorAdapter {

    public PositionListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // this function is called when the listView needs a new item view

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_position, parent, false);

        // the view holder object gets and holds references to the widgets on the view for later use
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // this function is called when the list view need to bind data to an existing view.

        // since the view exists, there should be a view holder with the widget references in it.
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        // use the viewHolder to get a reference to each widget and set the widget value to the cursor data value

        long dateInMillis = cursor.getLong(Position.PROJECTION_INDEX.DATE);
        TextView dateView = (TextView) viewHolder.date;
//      dateView.setText(Utility.formatDateTime(context, dateInMillis));

        ((TextView) viewHolder.position_name).setText(cursor.getString(Position.PROJECTION_INDEX.POSITION_NAME));

//      double low = cursor.getDouble(Position.PROJECTION_INDEX.LOW);
//      TextView lowView = (TextView) viewHolder.low;
//      lowView.setText(Utility.formatMoney(context, low));
    }

    static class ViewHolder {
        public ViewHolder(View view) {

            symbol = (TextView) view.findViewById(R.id.list_item_symbol_textview);
            position_name = (TextView) view.findViewById(R.id.list_item_position_name_textview);
            last = (TextView) view.findViewById(R.id.list_item_last_textview);
        //    close = (TextView) view.findViewById(R.id.list_item_close_textview);
        //    open = (TextView) view.findViewById(R.id.list_item_open_textview);
            low = (TextView) view.findViewById(R.id.list_item_low_textview);
            high = (TextView) view.findViewById(R.id.list_item_high_textview);
        //    low52 = (TextView) view.findViewById(R.id.list_item_low52_textview);

        //    high52 = (TextView) view.findViewById(R.id.list_item_low52_textview);
        //    volume = (TextView) view.findViewById(R.id.list_item_high52_textview);
        //    avg_volume = (TextView) view.findViewById(R.id.list_item_avg_volume_textview);
        //    pe = (TextView) view.findViewById(R.id.list_item_pe_textview);
        //    eps = (TextView) view.findViewById(R.id.list_item_eps_textview);

        //    yield = (TextView) view.findViewById(R.id.list_item_yield_textview);
        //    date = (TextView) view.findViewById(R.id.list_item_date_textview);

        }
        TextView symbol;
        TextView position_name;
        TextView last;
        TextView close;
        TextView open;
        TextView low;
        TextView high;
        TextView low52;
        TextView high52;
        TextView volume;
        TextView avg_volume;
        TextView pe;
        TextView eps;
        TextView yield;
        TextView date;
    }
}
