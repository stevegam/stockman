package com.gamberale.android.stockman;

/**
 * Created by stevegam on 4/20/2015.
 */
public interface AsyncTaskListener {
    void onTaskCompleted(String params[], Object result);
    void onProgressUpdate(int progress);
    void onCancelled();
}
