package com.gamberale.android.stockman;

import android.os.AsyncTask;

/**
 * Created by stevegam on 4/20/2015.
 */
public interface AsyncTaskCaller {
    AsyncTask fetchTextUrl(String request_id, String url, String extra);
    AsyncTask fetchImageUrl(String request_id, String url, String extra);
    AsyncTask getChart(String symbol, String time, String zsize, String qtype, String log, String chart_id);
}
