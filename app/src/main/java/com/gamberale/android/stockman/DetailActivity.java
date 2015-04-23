package com.gamberale.android.stockman;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.gamberale.android.stockman.data.StockPosition;


public class DetailActivity extends ActionBarActivity implements AsyncTaskListener, AsyncTaskCaller {

    private AsyncTaskFragment mAsyncTaskFragment;
    static final String DETAIL_ASYNC_FRAGMENT_TAG = "DETAIL_ASYNC_FRAGMENT";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            Bundle b = getIntent().getExtras();
            StockPosition stockPosition = b.getParcelable(DetailFragment.DETAIL_STOCK_POSITION);
            arguments.putParcelable(DetailFragment.DETAIL_STOCK_POSITION, stockPosition);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        mAsyncTaskFragment =
                (AsyncTaskFragment) getFragmentManager().findFragmentByTag("DETAIL_ASYNC_FRAGMENT");
        if (mAsyncTaskFragment == null) {
            mAsyncTaskFragment = new AsyncTaskFragment();
            getFragmentManager().beginTransaction()
                    .add(mAsyncTaskFragment, "DETAIL_ASYNC_FRAGMENT").commit();
        }
    //  getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public AsyncTask fetchTextUrl(String request_id, String url, String extra) {

        return mAsyncTaskFragment.fetchTextUrl(request_id, url, extra);
    }

    public AsyncTask fetchImageUrl(String request_id, String url, String extra) {

        return mAsyncTaskFragment.fetchImageUrl(request_id, url, extra);
    }

    public AsyncTask getChart(String symbol, String time, String zsize, String qtype, String log, String chart_id) {

        return mAsyncTaskFragment.getChart(symbol, time, zsize, qtype, log, chart_id);
    }

    @Override
    public void onTaskCompleted(String params[], Object result) {

        PositionListFragment pf = (PositionListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_list);
        if ( null != pf ) {
            pf.onTaskCompleted(params, result);
        }
        DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if ( null != df ) {
            df.onTaskCompleted(params, result);
        }
    }

    @Override
    public void onProgressUpdate(int progress) {}
    @Override
    public void onCancelled() {}
}
