/**
 * Created by stevegam on 4/14/2015.
 */
package com.gamberale.android.stockman;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Utility {

    public static String getPreferredAccount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_account_id_key), context.getString(R.string.pref_account_id_default));
    }

    public static int listViewPerformClick(ListView listView, int position) {
        if (position == ListView.INVALID_POSITION) return ListView.INVALID_POSITION;
        if (position < 0) return ListView.INVALID_POSITION;
        if (position >= listView.getAdapter().getCount()) return ListView.INVALID_POSITION;
        listView.performItemClick(
                listView.getChildAt(position),
                position,
                listView.getAdapter().getItemId(position));
        listView.smoothScrollToPosition(position);
        return position;
    }

    public static Cursor listViewGetItemCursor(ListView listView, int position) {
        if (position == ListView.INVALID_POSITION) return null;
        if (position < 0) return null;
        if (position >= listView.getAdapter().getCount()) return null;
        Cursor cursor = (Cursor) listView.getItemAtPosition(position);
        if (cursor != null) return null;
        return cursor;
    }

    public static int getListViewItem(ListView listView, float x, float y) {

        Rect rect = new Rect();
        int childCount = listView.getChildCount();
        int[] lvCoords = new int[2];
        listView.getLocationOnScreen(lvCoords);
        int ix = (int) x - lvCoords[0];
        int iy = (int) y - lvCoords[1];
        int position = -1;
        View child;
        for (int i = 0; i < childCount; i++) {
            child = listView.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains(ix, iy)) {
                position = listView.getPositionForView(child);
                break;
            }
        }
        return position;
    }

    public static String formatDateTime(Context context, long value) {

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

        return dateTimeFormat.format(value);

    }

    public static String formatPercent(Context context, double value) {

        return context.getString(R.string.format_percent, value);
    }

    public static String formatPercent(Context context, long value) {

        return context.getString(R.string.format_percent, value);
    }


    public static String formatMoney(Context context, double value) {

        return context.getString(R.string.format_money, value);
    }

    public static String formatMoney(Context context, long value) {

        return context.getString(R.string.format_money, value);
    }

    public static String formatMoney(Context context, float value) {

        return context.getString(R.string.format_money, value);
    }

    public static String formatInteger(Context context, double value) {

        return context.getString(R.string.format_integer, value);
    }

    public static String formatInteger(Context context, long value) {

        return context.getString(R.string.format_integer, value);
    }

    public static String formatFloat(float value)
    {
        return new DecimalFormat("###.##").format(value);
    }

    public static long normalizeDate(long value) {

        return value;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static float precentOfTradingDay() {

        Long ts = getTradingSecond();
        return (ts == -1)?1:(float)(ts)/(390*60);
    }

    public static int dayOfWeek(long ltime) {

        long day = 86400000;
        // Jan 1 1970 was a Thursday
        long ldow = ltime + 4 * day;
        ldow = ldow - ((long)(ldow/(day*7)))*(day*7);
        ldow = ((long)((ldow)/day));
        return (int)ldow;
    }

    public static long getTradingSecond() {

        String markertTimeZoneID = "America/New_York";

        // 9.5 hours in milliseconds (9:30am)
        long tradeStartTime = 95 * 60 * 60 * 100;

        // 390 minutes trading day in milliseconds
        long tradeDuration = 390 * 60 * 1000;

        // one day in milliseconds
        long day = 86400000;

        Date date = new Date();
        long utcTime = date.getTime();

        int timeZoneOffset = TimeZone.getTimeZone(markertTimeZoneID).getOffset(utcTime);

        // adjust for local market time zone
        long localMarketTime = utcTime += timeZoneOffset;

        int dow = dayOfWeek(localMarketTime);

        if (dow < 1 || dow > 5) {

            // market is closed
            return -1;
        }

        // adjust to milliseconds since the start of the day
        long currentTimer = localMarketTime - ((long)(localMarketTime/day))*day;

        // milliseconds since the start of trading
        long tradeElapsedTime = currentTimer - tradeStartTime;

        if (tradeElapsedTime < 0 || tradeElapsedTime > tradeDuration) {

            // market is closed
            return -1;
        }

        return (long)(tradeElapsedTime/1000L);
    }

    public static String[] parseCSV(String csv) {

        int i = 0;
        int count = 0;
        String c = null;
        String buffer = "";
        String quote  = "\"";
        String space = " ";
        String delim = ",";
        boolean inQuote = false;

        if (csv == null) {
            return null;
        }

        ArrayList<String> list = new ArrayList<String>();

        for (i=0; i < csv.length(); i++) {

            c = csv.substring(i, i+1);

            if (c.equals(quote)) {
                if (inQuote) {
                    i++;
                    if (i >= csv.length()) {
                        continue;
                    }
                    c = csv.substring(i, i+1);
                    if (!c.equals(quote)) {
                        inQuote = false;
                    }
                }
                else {
                    inQuote = true;
                    continue;
                }
            }
            if (c.equals(delim)) {
                if (!inQuote) {
                    count++;
                    list.add(buffer);
                    buffer="";
                    continue;
                }
            }
            buffer += c;
        }

        if (buffer.length() > 0) {
            list.add(buffer);
        }
        if (count >= list.size()) {
            list.add("");
        }

        String[] tokens = new String[list.size()];
        list.toArray(tokens);

        return tokens;
    }


}
