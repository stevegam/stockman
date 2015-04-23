package com.gamberale.android.stockman;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gamberale.android.stockman.data.PortfolioContract;
import com.gamberale.android.stockman.data.StockPosition;

import java.util.Locale;


public class MainActivity extends ActionBarActivity implements AsyncTaskListener, AsyncTaskCaller {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    static final int STOCK_SEARCH_REQUEST = 1;
    static final String STOCK_SEARCH_RESULT = "STOCK_SEARCH_RESULT";
    private String mAccount_id = null;
    private Global mGlobal = Global.getInstance();
    private TextToSpeech mTTS = null;
    private AsyncTaskFragment mAsyncTaskFragment;
    static final String MAIN_ASYNC_FRAGMENT_TAG = "MAIN_ASYNC_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccount_id = Utility.getPreferredAccount(this);

        if (mGlobal.DEBUG) {
            setContentView(R.layout.activity_test_harness);
        }
        else {
            setContentView(R.layout.activity_main);
        }

        if (findViewById(R.id.fragment_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mGlobal.SinglePaneLayout = false;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mGlobal.SinglePaneLayout = true;
        }

        mAsyncTaskFragment =
                (AsyncTaskFragment) getFragmentManager().findFragmentByTag("MAIN_ASYNC_FRAGMENT");
        if (mAsyncTaskFragment == null) {
            mAsyncTaskFragment = new AsyncTaskFragment();
            getFragmentManager().beginTransaction()
                    .add(mAsyncTaskFragment, "MAIN_ASYNC_FRAGMENT").commit();
        }

    //  getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onStart() {
        super.onStart();

        View view = findViewById(R.id.testharness);

        if (view != null) {
            TextView output = (TextView) view.findViewById(R.id.output);

            output.append(TestHarness.addPosition(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        String account_id = Utility.getPreferredAccount( this );

        if (account_id != null && !account_id.equals(mAccount_id)) {
            PositionListFragment pf = (PositionListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_list);
            if ( null != pf ) {
                pf.onAccountChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onAccountChanged();
            }
            mAccount_id = account_id;
        }
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
    public void onProgressUpdate(int progress) {}
    @Override
    public void onCancelled() {}

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

    public void onItemSelected(Uri contentUri, StockPosition stockPosition) {
        if (Global.getInstance().SinglePaneLayout) {

            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);

            intent.putExtra(DetailFragment.DETAIL_STOCK_POSITION, stockPosition);

            startActivity(intent);

        } else {

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);
            args.putParcelable(DetailFragment.DETAIL_STOCK_POSITION, stockPosition);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == STOCK_SEARCH_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String result = data.getExtras().getString(STOCK_SEARCH_RESULT);

                if (result != null) {

                    StockPosition position = new StockPosition(result);

                    position.account_id = Long.parseLong(Utility.getPreferredAccount( this ));

                    Uri insertedUri = getApplicationContext().getContentResolver().insert(
                            PortfolioContract.Position.CONTENT_URI,
                            position.getValues()
                    );

                    mGlobal.Text = position.position_name;

                    mTTS = new TextToSpeech(this,

                            new TextToSpeech.OnInitListener() {

                                @Override
                                public void onInit(int status) {
                                    // TODO Auto-generated method stub
                                    if(status == TextToSpeech.SUCCESS){
                                        int result = mTTS.setLanguage(Locale.US);
                                        if(result==TextToSpeech.LANG_MISSING_DATA ||
                                                result==TextToSpeech.LANG_NOT_SUPPORTED){
                                            Log.e("error", "This Language is not supported");
                                        }
                                        else {
                                            mTTS.speak(mGlobal.Text, TextToSpeech.QUEUE_ADD, null);
                                        }
                                    }
                                    else
                                        Log.e("error", "Initialization Failed!");
                                }
                            });
                }

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

            }
        }
    }
}
