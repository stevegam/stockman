package com.gamberale.android.stockman;

/**
 * Created by stevegam on 4/18/2015.
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamberale.android.stockman.data.PortfolioContract;
import com.gamberale.android.stockman.data.StockPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DetailFragment extends Fragment  {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    static final String DETAIL_STOCK_POSITION = "STOCK_POSITION";
    static final String CHART_URL = "http://chart.finance.yahoo.com/z?";

    private Global mGlobal = Global.getInstance();

    private static final String SHARE_HASHTAG = " #Stockman";

    private ShareActionProvider mShareActionProvider;
    private String mShareText;
    private Uri mUri;
    private int mChartTypeIndex = 0;
    private boolean mEventMutex = false;
    private static final int DELTA_X = 50;
    private static final int DELTA_Y = 50;
    float mPreviousX = -1;
    float mPreviousY = -1;
    static String[] mChartTypes = {"1d", "5d", "3m", "1y", "5y", "my"};
    AsyncTask mChartAsyncTask = null;
    AsyncTask mDataAsyncTask = null;
    View mLoadingPanel = null;
    View mBottomLayoutView = null;
    StockPosition mStockPosition = null;

    private static final int DETAIL_LOADER = 0;

    private TextView mPositionNameView;
    private TextView mLastView;
    private ImageView mChartImageView;

    private TextToSpeech tts = null;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            mStockPosition = arguments.getParcelable(DetailFragment.DETAIL_STOCK_POSITION);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mBottomLayoutView = (View) rootView.findViewById(R.id.fragment_detail_bottom);
        mChartImageView = (ImageView) rootView.findViewById(R.id.chart);
        mPositionNameView = (TextView) rootView.findViewById(R.id.position_name);
       // mLastView = (TextView) rootView.findViewById(R.id.last);
        mLoadingPanel = rootView.findViewById(R.id.loadingPanel);

        mLoadingPanel.setVisibility(View.GONE);
        mChartImageView.setVisibility(View.GONE);
        mBottomLayoutView.setVisibility(View.GONE);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBottomLayoutView.setVisibility(View.GONE);
        }
        else {
            if (mStockPosition != null && mStockPosition.symbol != null){
            mBottomLayoutView.setVisibility(View.VISIBLE);
        }
        }

        mChartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEventMutex) return;

                mEventMutex = true;
            }
        });

        mChartImageView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPreviousX = event.getX();
                        mPreviousY = event.getY();
                        mEventMutex = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        float currentX = event.getX();
                        float currentY = event.getY();
                        if (mEventMutex) return false;
                        if (currentX > mPreviousX && Math.abs(currentX - mPreviousX) > DELTA_X && Math.abs(currentY - mPreviousY) < DELTA_Y) {
                            mEventMutex = true;
                            mChartTypeIndex--;
                            if (mChartTypeIndex < 0) {
                                mChartTypeIndex = mChartTypes.length - 1;
                            }
                            getStockChart();
                            return true;
                        }
                        if (currentX < mPreviousX && Math.abs(currentX - mPreviousX) > DELTA_X && Math.abs(currentY - mPreviousY) < DELTA_Y) {
                            mEventMutex = true;
                            mChartTypeIndex++;
                            if (mChartTypeIndex >= mChartTypes.length) {
                                mChartTypeIndex = 0;
                            }
                            getStockChart();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mChartImageView.setClickable(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareText != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareText + SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        AsyncTaskCaller asyncTaskCaller = (AsyncTaskCaller)getActivity();

        mLoadingPanel.setVisibility(View.GONE);
        mChartImageView.setVisibility(View.GONE);
        mBottomLayoutView.setVisibility(View.GONE);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBottomLayoutView.setVisibility(View.GONE);
        }
        else {
            if (mStockPosition != null && mStockPosition.symbol != null){
                mBottomLayoutView.setVisibility(View.VISIBLE);
            }
        }

        getStockInfo();

        super.onActivityCreated(savedInstanceState);
    }

    void getStockInfo() {
        if (mStockPosition == null) return;
        String symbol = mStockPosition.symbol;
        mShareText = mStockPosition.toString();
        mPositionNameView.setText(mStockPosition.position_name);
        getStockChart();
    }

    void getStockChart() {
        if (mStockPosition == null) return;
        String symbol = mStockPosition.symbol;
        if (symbol == null) return;
        mChartAsyncTask = getChart(symbol, mChartTypes[mChartTypeIndex], "s", "b", "on", Integer.toString(mChartTypeIndex));
    }

    void onAccountChanged() {
        if (mGlobal.SinglePaneLayout) {
            getActivity().finish();
        }
    }

    public void onTaskCompleted(String params[], Object result) {

        if (result == null) return;
        if (mChartImageView == null) return;
        mChartImageView.setImageBitmap((Bitmap) result);
    }

    public AsyncTask getChart(String symbol, String time, String zsize, String qtype, String log, String chart_id) {

        if (mChartAsyncTask != null) {
            mChartAsyncTask.cancel(true);
        }
        if (mLoadingPanel != null) {
            mLoadingPanel.setVisibility(View.VISIBLE);
        }

        if (mChartImageView != null) {
            mChartImageView.setVisibility(View.GONE);
        }
        String url = Uri.parse(CHART_URL).buildUpon()
                .appendQueryParameter("z", zsize)
                .appendQueryParameter("q", qtype)
                .appendQueryParameter("l", log)
                .appendQueryParameter("s", symbol)
                .appendQueryParameter("t", time)
                .build().toString();

        FetchImageTask fetchImage = new FetchImageTask();

        fetchImage.execute(url);

        return fetchImage;
    }

    public class FetchImageTask extends AsyncTask<String, Void, Bitmap> {

        String[] mParams = null;

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                Thread.sleep(100);
            } catch (Exception e) {}

            mParams = params;

            if (params.length == 0) {
                return null;
            }

            if(isCancelled()) return null;

            String sURL = params[0];

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
        protected void onPostExecute(Bitmap image) {

            if (image == null) return;

            if (mLoadingPanel != null) {
                mLoadingPanel.setVisibility(View.GONE);
            }

            if (mChartImageView != null) {
                mChartImageView.setImageBitmap(image);
                mChartImageView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled(){

        }

    }

    public AsyncTask getStockData(String symbol) {

        if (mDataAsyncTask != null) {
            mDataAsyncTask.cancel(true);
        }

        if (symbol == null) {
            return null;
        }

        String url = Uri.parse(AsyncTaskFragment.STOCK_DATA_URL).buildUpon()
                .appendQueryParameter("s", symbol)
                .build().toString();

        FetchUrlTask fetchUrl = new FetchUrlTask();

        fetchUrl.execute(url);

        return fetchUrl;
    }

    public class FetchUrlTask extends AsyncTask<String, Void, String> {

        String[] mParams = null;

        @Override
        protected String doInBackground(String... params) {

            mParams = params;

            if (params.length == 0) {
                return null;
            }

            if(isCancelled()) return null;

            String sURL = params[0];

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
        protected void onPostExecute(String data) {

            if (data == null) return;

            final String[] tokens = Utility.parseCSV(data);

            if (tokens == null) return;
            if (tokens.length < 6) return;

            StockPosition sp = new StockPosition();

            // s
            sp.symbol = tokens[0];
            // n
            sp.position_name = tokens[1];
            // l1
            try {
                sp.last = Float.parseFloat(tokens[2]);
                // c1
                sp.change = Float.parseFloat(tokens[3]);
                // p
                sp.close = Float.parseFloat(tokens[4]);
                // o
                sp.open = Float.parseFloat(tokens[5]);
                // g
                sp.low = Float.parseFloat(tokens[6]);
                // h
                sp.high = Float.parseFloat(tokens[7]);
                // j
                sp.low52 = Float.parseFloat(tokens[8]);
                // k
                sp.high52 = Float.parseFloat(tokens[9]);
                // v
                sp.volume = Integer.parseInt(tokens[10]);
                //a2
                sp.avg_volume = Integer.parseInt(tokens[11]);
                // r
                sp.pe = Integer.parseInt(tokens[12]);
                // e7
                sp.eps = Float.parseFloat(tokens[13]);
                // y
                sp.yield = Float.parseFloat(tokens[14]);
            } catch (Exception e) {
                return;
            }

            sp.account_id = Long.parseLong(Utility.getPreferredAccount(getActivity()));

            Uri insertedUri = getActivity().getApplicationContext().getContentResolver().insert(
                    PortfolioContract.Position.CONTENT_URI,
                    sp.getValues()
            );

            if (Utility.getTradingSecond() == -1) {
                //  stringBuilder.append("\tThe market is currently closed.");
            }

            Toast.makeText(getActivity(), sp.talk(), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled(){

        }

    }
}
