package com.gamberale.android.stockman;

/**
 * Created by stevegam on 4/20/2015.
 */

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncTaskFragment extends Fragment implements AsyncTaskListener {

    private final String LOG_TAG = AsyncTaskFragment.class.getSimpleName();

    public static final String CHART_LOADER = "CHART_LOADER";
    public static final String CHART_URL = "http://chart.finance.yahoo.com/z?";
    public static final String STOCK_DATA_URL = "http://download.finance.yahoo.com/d/quotes.csv?f=snl1c1poghjkva2re7yd1t1"; //  snl1c1p2va2
    public static final String STOCK_SEARCH_URL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?callback=YAHOO.Finance.SymbolSuggest.ssCallback";

    public AsyncTaskFragment() {

    }
    public AsyncTask fetchTextUrl(String request_id, String url, String extra) {

        AsyncFetchTextUrlTask asyncTask = new AsyncFetchTextUrlTask(this);
        asyncTask.execute(request_id, url, extra);
        return asyncTask;
    }
    public AsyncTask fetchImageUrl(String request_id, String url, String extra) {

        AsyncFetchImageUrlTask asyncTask = new AsyncFetchImageUrlTask(this);
        asyncTask.execute(request_id, url, extra);
        return asyncTask;
    }
    public AsyncTask getChart(String symbol, String time, String zsize, String qtype, String log, String chart_id) {

        String url = Uri.parse(CHART_URL).buildUpon()
                .appendQueryParameter("z", zsize)
                .appendQueryParameter("q", qtype)
                .appendQueryParameter("l", log)
                .appendQueryParameter("s", symbol)
                .appendQueryParameter("t", time)
                .build().toString();

        return fetchImageUrl(CHART_LOADER, url, chart_id);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public void onTaskCompleted(String params[], Object result) {

        AsyncTaskListener listener = (AsyncTaskListener)getActivity();
        if (listener != null) {
            listener.onTaskCompleted(params, result);
        }
    }
    @Override
    public void onProgressUpdate(int progress) {}
    @Override
    public void onCancelled() {}

    public class AsyncFetchImageUrlTask extends AsyncTask<String, Integer, Object> {

        private String[] mParams = null;
        private AsyncTaskListener mListener;

        public AsyncFetchImageUrlTask(AsyncTaskListener listener) {
            mListener = listener;
        }
        @Override
        protected Object doInBackground(String... params) {

            mParams = params;

            if (params.length == 0) {
                return null;
            }

            if(isCancelled()) return null;

            String sURL = params[1];

            publishProgress(0);

            Bitmap image = null;
            URL url = null;

            try {
                image = BitmapFactory.decodeStream((InputStream) new URL(mParams[0]).getContent());

            } catch (Exception e) {

            }
            if (isCancelled()) return null;

            return image;
        }
        @Override
        protected void onPostExecute(Object result) {

            mListener.onTaskCompleted(mParams, result);
        }
        protected void onProgressUpdate(Integer... progress) {
            mListener.onProgressUpdate(progress[0]);
        }
        @Override
        protected void onCancelled(){
            mListener.onCancelled();
        }
    }

    public class AsyncFetchTextUrlTask extends AsyncTask<String, Integer, Object> {

        private String[] mParams = null;
        private AsyncTaskListener mListener;

        public AsyncFetchTextUrlTask(AsyncTaskListener listener) {
            mListener = listener;
        }
        @Override
        protected Object doInBackground(String... params) {

            mParams = params;

            if (params.length == 0) {
                return null;
            }

            if(isCancelled()) return null;

            String sURL = params[1];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {

                URL url = new URL(sURL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    return null;
                }
                return buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error:", e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error:", e);
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object result) {

            mListener.onTaskCompleted(mParams, result);
        }
        protected void onProgressUpdate(Integer... progress) {
            mListener.onProgressUpdate(progress[0]);
        }
        @Override
        protected void onCancelled(){
            mListener.onCancelled();
        }
    }
}
