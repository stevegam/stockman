package com.gamberale.android.stockman;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.gamberale.android.stockman.data.PortfolioContract;
import com.gamberale.android.stockman.data.PortfolioDbHelper;
import com.gamberale.android.stockman.data.StockPosition;


/**
 * Created by stevegam on 4/18/2015.
 */
public class TestHarness {

    public static String addAccount (Context context) {

        long id = 1;
        String name = "MyAccount";

        Cursor cursor = context.getContentResolver().query(
                PortfolioContract.Account.CONTENT_URI,
                PortfolioContract.Account.PROJECTION,
                PortfolioContract.Account.SELECTION_ID,
                new String[]{Long.toString(id)},
                null);

        if (cursor.moveToFirst()) {

            id = cursor.getLong(cursor.getColumnIndex(PortfolioContract.Account._ID));
            name = cursor.getString(cursor.getColumnIndex(PortfolioContract.Account.COLUMN_ACCOUNT_NAME));
            Log.d("Test:addAccount", Long.toString(id) + " " + name);

            return "retrieved " + Long.toString(id) + name;
        }
        else {

            ContentValues values = new ContentValues();

            values.put(PortfolioContract.Account.COLUMN_ACCOUNT_NAME, name);

            Uri insertedUri = context.getContentResolver().insert(
                    PortfolioContract.Account.CONTENT_URI,
                    values
            );

            id = ContentUris.parseId(insertedUri);

            cursor.close();

            return Long.toString(id);
        }
    }

    public static String addPosition (Context context) {

        StockPosition p = new StockPosition();
        p.id = 2;
        p.account_id = 1;
        p.symbol = "LMT";
        p.position_name = "Lockeed Martin";
        p.last = 0;
        p.close = 0;
        p.open = 0;
        p.low = 0;
        p.high = 0;
        p.low52 = 0;
        p.high52 = 0;
        p.volume = 0;
        p.avg_volume = 0;
        p.pe = 0;
        p.eps = 0;
        p.yield = 0;
        p.date = 0;

        Cursor cursor = context.getContentResolver().query(
                PortfolioContract.Position.CONTENT_URI,
                PortfolioContract.Position.PROJECTION,
                PortfolioContract.Position.SELECTION_ID,
                new String[]{Long.toString(p.id)},
                null);

        if (cursor.moveToFirst()) {

            p.id = cursor.getLong(cursor.getColumnIndex(PortfolioContract.Position._ID));
            String name = cursor.getString(cursor.getColumnIndex(PortfolioContract.Position.COLUMN_POSITION_NAME));
            Log.d("Test:addPosition", Long.toString(p.id) + " " + name);

            return "retrieved " + Long.toString(p.id) + name;
        }
        else {

            ContentValues values = p.getValues();

            Uri insertedUri = context.getContentResolver().insert(
                    PortfolioContract.Position.CONTENT_URI,
                    values
            );

            p.id = ContentUris.parseId(insertedUri);

            cursor.close();

            return Long.toString(p.id);
        }
    }

    public static String getAccountPosition (Context context) {

        long id = 1;
        String name;
        String buffer = "";

        PortfolioDbHelper db = new PortfolioDbHelper(context);
/*
        Cursor cursor = null;
        try {

            PortfolioContract.Position.CONTENT_URI,
                    PortfolioContract.Position.PROJECTION,
                    PortfolioContract.Position.SELECTION_ACCOUNT_ID,
                    new String[]{"1"},
                    PortfolioContract.Position.SORT_ORDER);

            cursor = db.getReadableDatabase().rawQuery("SELECT * FROM ACCOUNT WHERE _id = ?", new String[]{"1"});

            //INNER JOIN position on account._id = position._id WHERE account._id = ?", new String[]{"1"});
        }
        catch (android.database.sqlite.SQLiteException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
         }
*/
        Cursor cursor = context.getContentResolver().query(
                PortfolioContract.Position.CONTENT_URI,
                PortfolioContract.Position.PROJECTION,
                PortfolioContract.Position.SELECTION_ACCOUNT_ID,
                new String[]{Long.toString(id)},
                PortfolioContract.Position.SORT_ORDER);
/*
        Cursor cursor = context.getContentResolver().query(
                PortfolioContract.AccountPosition.CONTENT_URI,
                new String[]{"symbol", "account_name", "position_name"},
                PortfolioContract.AccountPosition.SELECTION_ACCOUNT_ID,
                new String[]{Long.toString(id)},
                null);
*/
        if (cursor.moveToFirst()) {

            //buffer += Long.toString(cursor.getLong(cursor.getColumnIndex(PortfolioContract.Account._ID))) + "\n";
  //        buffer += cursor.getString(cursor.getColumnIndex(PortfolioContract.Position.COLUMN_ACCOUNT_NAME));
            buffer += cursor.getString(cursor.getColumnIndex(PortfolioContract.Position.COLUMN_SYMBOL));
            buffer += cursor.getString(cursor.getColumnIndex(PortfolioContract.Position.COLUMN_POSITION_NAME));
            Log.d("Test:getAccountPosition", Long.toString(id) + " " + buffer);

            return buffer;
        }

        return "no data";
    }
}
