package com.gamberale.android.stockman;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockSearchActivity extends Activity implements AsyncTaskListener {

    private TextView mTextView;
    private ListView mListView;
    private Activity mThis = this;
    private AsyncTaskFragment mAsyncTaskFragment;

    static final String STOCK_SEARCH = "STOCK_SEARCH";
    static final String STOCK_SEARCH_RESULT = "STOCK_SEARCH_RESULT";
    static final String SEARCH_ASYNC_FRAGMENT_TAG = "SEARCH_ASYNC_FRAGMENT";
    static final String STOCK_SEARCH_URL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?callback=YAHOO.Finance.SymbolSuggest.ssCallback";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mTextView = (TextView) findViewById(R.id.text);
        mListView = (ListView) findViewById(R.id.list);

        mAsyncTaskFragment =
                (AsyncTaskFragment) getFragmentManager().findFragmentByTag(SEARCH_ASYNC_FRAGMENT_TAG);
        if (mAsyncTaskFragment == null) {
            mAsyncTaskFragment = new AsyncTaskFragment();
            getFragmentManager().beginTransaction()
                    .add(mAsyncTaskFragment, SEARCH_ASYNC_FRAGMENT_TAG).commit();
        }
        onSearchRequested();
        //  handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            executeQuery(query);
        }
    }

    private void executeQuery(String query) {

        String url = Uri.parse(STOCK_SEARCH_URL).buildUpon()
                .appendQueryParameter("query", query)
                .build().toString();

        AsyncTask asyncTask = mAsyncTaskFragment.fetchTextUrl(STOCK_SEARCH, url, query);

        //  asyncTask.cancel(true);
    }

    @Override
    public void onTaskCompleted(String params[], Object result) {

        String request_id = params[0];
        String query = params[2];

        if (!request_id.equals(STOCK_SEARCH)) return;
        if (result == null) return;

    //  Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();

        List<StockSearchResult> arrayList = parseSearchResults(result.toString());

        if (arrayList == null) {
            arrayList = new ArrayList<StockSearchResult>();
        }

        ArrayAdapter<StockSearchResult> adapter = new ArrayAdapter<StockSearchResult>(this,
                R.layout.search_list_item, R.id.description, arrayList);

        mListView.setAdapter(adapter);

        int count = arrayList.size();
        if (count == 0) {

            mTextView.setText(getString(R.string.no_results, new Object[]{query}));

        } else {

            mTextView.setText(getResources().getQuantityString(R.plurals.search_results, count, new Object[]{count, query}));

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    StockSearchResult searchResult = (StockSearchResult) mListView.getItemAtPosition(position);

                    //Toast.makeText(getApplicationContext(), searchResult.symbol, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent();
                    intent.putExtra(STOCK_SEARCH_RESULT, searchResult.toString());
                    if (getParent() == null) {

                        setResult(Activity.RESULT_OK, intent);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, intent);
                    }
                    finish();
                }
            });
        }
    }

    @Override
    public void onProgressUpdate(int progress) {}
    @Override
    public void onCancelled() {}

    private ArrayList<StockSearchResult>parseSearchResults(String result) {

        final String WRAPPER = "YAHOO.Finance.SymbolSuggest.ssCallback(";
        if (result == null) return null;

        if (result.length() > WRAPPER.length() + 1) {
            if (result.startsWith(WRAPPER)) {
                result = result.substring(WRAPPER.length());
                result = result.substring(0, result.length()-1);
            }
        }

        ArrayList<StockSearchResult> arrayList = new ArrayList<StockSearchResult>();

        try {

            JSONObject json = new JSONObject(result);

            JSONObject ResultSet = json.getJSONObject("ResultSet");

            String query = ResultSet.getString("Query");

            JSONArray Result = ResultSet.getJSONArray("Result");

            for (int i=0; i < Result.length(); i++) {

                JSONObject item = Result.getJSONObject(i);

                String symbol = item.getString("symbol");
                String name = item.getString("name");
                String exchange = item.getString("exchDisp");

                if (exchange.equals("NASDAQ") || exchange.equals("NYSE")) {
                    arrayList.add(new StockSearchResult(symbol, name));
                }
            }

        } catch (JSONException e) {

            return null;
        }

        return arrayList;
    }
}
