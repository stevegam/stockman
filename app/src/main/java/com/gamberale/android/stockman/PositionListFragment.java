package com.gamberale.android.stockman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gamberale.android.stockman.data.PortfolioContract;
import com.gamberale.android.stockman.data.PortfolioContract.Position;
import com.gamberale.android.stockman.data.StockPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by stevegam on 4/18/2015.
 */

public class PositionListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PositionListFragment.class.getSimpleName();
    static final int STOCK_SEARCH_REQUEST = 1;
    static final String STOCK_SEARCH_RESULT = "STOCK_SEARCH_RESULT";
    private static final String SELECTED_KEY = "selected_position";

    private Global mGlobal = Global.getInstance();

    private static final int LIST_LOADER = 0;
    private static final int INSERT_LOADER = 1;
    private static final int UPDATE_LOADER = 2;
    private static final int DELETE_LOADER = 3;

    private static final int DELTA_X = 50;
    private static final int DELTA_Y = 50;

    int mPosition = ListView.INVALID_POSITION;
    int mPositionToRemove = ListView.INVALID_POSITION;
    int mRelativePositionToRemove = ListView.INVALID_POSITION;
    float mPreviousX = -1;
    float mPreviousY = -1;
    boolean mEventMutex = false;
    ListView mListView;
    PositionListAdapter mPositionListAdapter;
    View mChildView = null;
    int mColor = Color.TRANSPARENT;
    private TextToSpeech mTTS = null;
    private String mText = null;
    private PositionListFragment mThat = this;
    private AsyncTask mAsyncTask = null;

    public PositionListFragment() {
    }


    @Override
    public Loader<Cursor> onCreateLoader(int action, Bundle bundle) {

        String account_id = Utility.getPreferredAccount(getActivity());
        if (action == LIST_LOADER) {

            return new CursorLoader(getActivity(),
                    Position.CONTENT_URI,
                    Position.PROJECTION,
                    Position.SELECTION_ACCOUNT_ID,
                    new String[]{account_id},
                    Position.SORT_ORDER);
        }
        return null;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPositionListAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPositionListAdapter.swapCursor(data);
        if (mPosition == ListView.INVALID_POSITION || mPosition == 0) {

            if (!mGlobal.SinglePaneLayout) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mPosition = Utility.listViewPerformClick(mListView, 0);
                    }
                }, 1000);
            }
        }
        else {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    public void onTaskCompleted(String params[], Object result) {

    }

    void onAccountChanged() {
    //TODO: fix this
    //  updatePortfolio();
        getLoaderManager().restartLoader(LIST_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_positionlistfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {

            Intent intent = new Intent(getActivity(), StockSearchActivity.class);
            getActivity().startActivityForResult(intent, STOCK_SEARCH_REQUEST);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mPositionListAdapter = new PositionListAdapter(getActivity(), null, 0);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.positionListView);
        mListView.setAdapter(mPositionListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (mEventMutex) return;

                mEventMutex = true;

                mPosition = listViewSetItemSelected(mListView, position);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (mEventMutex) return false;

                mEventMutex = true;

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    getStockQuote(cursor.getString(Position.PROJECTION_INDEX.SYMBOL));
                }

                return true;
            }
        });

        mListView.setLongClickable(true);


        mListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ListView listView = (ListView) v;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPreviousX = event.getX();
                        mPreviousY = event.getY();
                        mPositionToRemove = Utility.getListViewItem(listView, event.getRawX(), event.getRawY());
                        mEventMutex = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mEventMutex) return false;
                        if (mPositionToRemove == -1) return false;
                        mRelativePositionToRemove = mPositionToRemove-mListView.getFirstVisiblePosition();
                        if (Math.abs(event.getX() - mPreviousX) > DELTA_X && Math.abs(event.getY() - mPreviousY) < DELTA_Y) {
                            mEventMutex = true;
                            if (mGlobal.SinglePaneLayout) {
                                mChildView = listView.getChildAt(mRelativePositionToRemove);
                             // mChildView = listView.getChildAt(mPositionToRemove);
                                if (mChildView != null) {
                                    Drawable background = mChildView.getBackground();
                                    if (background instanceof ColorDrawable) {
                                        mColor = ((ColorDrawable) background).getColor();
                                    }
                                    mChildView.setBackgroundColor(getActivity().getApplicationContext().getResources().getColor(R.color.light_blue));
                                }
                            }
                            else {
                                listViewSetItemSelected(mListView, mPositionToRemove);
                            }
                            Cursor cursor = (Cursor)mListView.getItemAtPosition(mPositionToRemove);
                            String symbol = cursor.getString(Position.PROJECTION_INDEX.SYMBOL);
                            String position_name = cursor.getString(Position.PROJECTION_INDEX.POSITION_NAME);

                            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                            adb.setTitle("Delete?");
                            adb.setMessage(symbol + " - " + position_name);
                            adb.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mGlobal.SinglePaneLayout) {
                                        //mListView.setItemChecked(mPositionToRemove, false);
                                        //mListView.clearChoices();
                                        //mListView.requestLayout();
                                        // mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);

                                        if (mChildView != null) {
                                            mChildView.setBackgroundColor(mColor);
                                        }
                                    }
                                }
                            });
                            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // MyDataObject.remove(mPositionToRemove);
                                    // Adapter adapter = mListView.getAdapter();
                                    // adapter.notifyDataSetChanged();
                                    if (mChildView != null) {
                                        mChildView.setBackgroundColor(mColor);
                                    }
                                    int count = mListView.getAdapter().getCount();
                                    Cursor cursor = (Cursor)mListView.getItemAtPosition(mPositionToRemove);
                                    Uri uri = Position.getUri(cursor.getLong(Position.PROJECTION_INDEX._ID));
                                    String account_id = Utility.getPreferredAccount(getActivity());
                                    mThat.getActivity().getContentResolver().delete(
                                            Position.CONTENT_URI, Position.SELECTION_ACCOUNT_ID_AND_POSITION_ID,
                                            new String[]{account_id, Long.toString(cursor.getLong(Position.PROJECTION_INDEX._ID))});
                                    if (count == 1) {
                                        listViewSetItemSelected(mListView, ListView.INVALID_POSITION);
                                    }
                                    else
                                    {
                                        listViewSetItemSelected(mListView, 0);
                                    }
                                }
                            });
                            adb.show();
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    private int listViewSetItemSelected(ListView listView, int position) {
            String account_id = Utility.getPreferredAccount(listView.getContext());
            Context context = listView.getContext().getApplicationContext();
            StockPosition stockPosition = new StockPosition();
            stockPosition.account_id = Long.parseLong(account_id);
        Uri uri = null;
        Cursor cursor = null;

        if (position == ListView.INVALID_POSITION || position < 0 || position >= listView.getAdapter().getCount()) {
            ((MainActivity)listView.getContext()).onItemSelected(null, null);
            return ListView.INVALID_POSITION;
        }

        listView.setSelection(position);
        listView.setItemChecked(position, true);
        listView.smoothScrollToPosition(position);

        cursor = (Cursor)listView.getItemAtPosition(position);

        if (cursor != null) {
            stockPosition.id = cursor.getLong(Position.PROJECTION_INDEX._ID);
            stockPosition.symbol = cursor.getString(Position.PROJECTION_INDEX.SYMBOL);
            stockPosition.position_name = cursor.getString(Position.PROJECTION_INDEX.POSITION_NAME);
            uri = PortfolioContract.Position.getUri(
                    Long.parseLong(account_id), cursor.getLong(Position.PROJECTION_INDEX._ID));

        }
        ((MainActivity)listView.getContext()).onItemSelected(uri, stockPosition);
        return position;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public AsyncTask getStockQuote(String symbol) {

        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
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

            StringBuilder stringBuilder = new StringBuilder();

            final String[] tokens = Utility.parseCSV(data); //data.split(Pattern.quote(","));

            if (tokens == null) return;
            if (tokens.length < 11) return;

            stringBuilder.append(tokens[1]);
            stringBuilder.append("\t");

            stringBuilder.append(tokens[0].toUpperCase());
            stringBuilder.append("\t");

            stringBuilder.append(tokens[2]);
            stringBuilder.append("\t");

            try {
                float last = Float.parseFloat(tokens[2]);
                float change = Float.parseFloat(tokens[3]);
                float close = Float.parseFloat(tokens[4]);
                float pchange = 100;
                close = last - change;
                if (close != 0) {
                    pchange = (change / close) * 100;
                }
                if (change > 0) {
                    stringBuilder.append("up\t" + tokens[3]);
                } else {
                    stringBuilder.append("down\t" + tokens[3]);
                }
                stringBuilder.append("\tor\t" + Utility.formatFloat(pchange) + "%");

            } catch (Exception e) {
                return;
            }

            stringBuilder.append("\ton volume of\t" + tokens[10] + "\tshares");

            if (Utility.getTradingSecond() == -1) {
            //  stringBuilder.append("\tThe market is currently closed.");
            }

            //snl1c1p2va2

            final String textToSpeak = stringBuilder.toString();

            mTTS = new TextToSpeech(getActivity(),

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
                                    mTTS.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
                                }
                            }
                            else
                                Log.e("error", "Initialization Failed!");
                        }
                    });

        //  Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled(){

        }

    }
}