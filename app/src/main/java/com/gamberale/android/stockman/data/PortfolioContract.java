/**
 * Created by stevegam on 4/14/2015.
 */
package com.gamberale.android.stockman.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PortfolioContract {

    public static final String CONTENT_AUTHORITY = "com.gamberale.android.stockman";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String DATABASE_NAME = "portfolio";
    public static final int DATABASE_VERSION = 15;

    public static final class Account implements BaseColumns {

        public static final String TABLE_NAME = "account";
        public static final String PATH = TABLE_NAME;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String COLUMN_ACCOUNT_NAME = "account_name";

        public static final Column[] COLUMNS;
        static {

            COLUMNS = new Column[]{
                    new Column(_ID, Column.TYPE_INTEGER, false, true, true, false),
                    new Column(COLUMN_ACCOUNT_NAME, Column.TYPE_TEXT, false, false, false, true),
            };
        }

        public static final String CONSTRAINTS = "UNIQUE (" + COLUMN_ACCOUNT_NAME + ") ON CONFLICT REPLACE";

        public static final String[] PROJECTION = {_ID, COLUMN_ACCOUNT_NAME};
        public static class PROJECTION_INDEX {
            public static final int _ID = 0;
            public static final int ACCOUNT_NAME = 1;
        }
        public static final String SELECTION_ID = TABLE_NAME + "." + BaseColumns._ID + " = ? ";
        public static final String SORT_ORDER = COLUMN_ACCOUNT_NAME + " ASC";

        public static long getId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Uri getUri(long _id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(_id)).build();
        }
    }

    public static final class Position implements BaseColumns {

        public static final String TABLE_NAME = "position";
        public static final String PATH = TABLE_NAME;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_POSITION_NAME = "position_name";
        public static final String COLUMN_LAST = "last";
        public static final String COLUMN_CHANGE = "change";
        public static final String COLUMN_CLOSE = "close";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW52 = "low52";
        public static final String COLUMN_HIGH52 = "high52";
        public static final String COLUMN_VOLUME = "volume";
        public static final String COLUMN_AVG_VOLUME = "avg_volume";
        public static final String COLUMN_PE = "pe";
        public static final String COLUMN_EPS = "eps";
        public static final String COLUMN_YIELD = "yield";
        public static final String COLUMN_DATE = "date";

        public static final Column[] COLUMNS;
        static {

            COLUMNS = new Column[]{
                    new Column(_ID, Column.TYPE_INTEGER, false, true, true, false),
                    new Column(COLUMN_ACCOUNT_ID, Column.TYPE_INTEGER, false, false, false, false),
                    new Column(COLUMN_SYMBOL, Column.TYPE_TEXT, false, false, false, true),
                    new Column(COLUMN_POSITION_NAME, Column.TYPE_TEXT, false, false, false, false),
                    new Column(COLUMN_LAST, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_CHANGE, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_CLOSE, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_OPEN, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_LOW, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_HIGH, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_LOW52, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_HIGH52, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_VOLUME, Column.TYPE_INTEGER, false, false, false, false),
                    new Column(COLUMN_AVG_VOLUME, Column.TYPE_INTEGER, false, false, false, false),
                    new Column(COLUMN_PE, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_EPS, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_YIELD, Column.TYPE_REAL, false, false, false, false),
                    new Column(COLUMN_DATE, Column.TYPE_INTEGER, false, false, false, false)};
        }

        //TODO:
    //  public static final String CONSTRAINTS = "FOREIGN KEY (" + COLUMN_ACCOUNT_ID + ") REFERENCES " + Account.TABLE_NAME + " (" + Account._ID + "), " +
        public static final String CONSTRAINTS = "UNIQUE (" + COLUMN_SYMBOL + ") ON CONFLICT REPLACE";

        public static final String TABLES = TABLE_NAME;
        public static final String[] PROJECTION = {
                _ID,
                COLUMN_ACCOUNT_ID,
                COLUMN_SYMBOL,
                COLUMN_POSITION_NAME,
                COLUMN_LAST,
                COLUMN_CHANGE,
                COLUMN_CLOSE,
                COLUMN_OPEN,
                COLUMN_LOW,
                COLUMN_LOW52,
                COLUMN_HIGH52,
                COLUMN_VOLUME,
                COLUMN_AVG_VOLUME,
                COLUMN_PE,
                COLUMN_EPS,
                COLUMN_YIELD,
                COLUMN_DATE
        };

        public static class PROJECTION_INDEX {
            public static final int _ID = 0;
            public static final int ACCOUNT_ID = 1;
            public static final int SYMBOL = 2;
            public static final int POSITION_NAME = 3;
            public static final int LAST = 4;
            public static final int CHANGE = 5;
            public static final int CLOSE = 6;
            public static final int OPEN = 7;
            public static final int LOW = 8;
            public static final int LOW52 = 9;
            public static final int HIGH52 = 10;
            public static final int VOLUME = 11;
            public static final int AVG_VOLUME = 12;
            public static final int PE = 13;
            public static final int EPS = 14;
            public static final int YIELD = 15;
            public static final int DATE = 16;
        }

        public static final String SELECTION_ID = TABLE_NAME + "." + BaseColumns._ID + " = ? ";
        public static final String SELECTION_ACCOUNT_ID = TABLE_NAME + "." + COLUMN_ACCOUNT_ID + " = ? ";
        public static final String SELECTION_ACCOUNT_ID_AND_POSITION_ID = TABLE_NAME + "." + COLUMN_ACCOUNT_ID + " = ? AND " + TABLE_NAME + "." + BaseColumns._ID + " = ? ";
        public static final String SORT_ORDER = _ID + " ASC";

        public static long getId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Uri getUri(long _id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(_id)).build();
        }

        public static Uri getUri(long account_id, long _id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(account_id)).appendPath(Long.toString(_id)).build();
        }
    }

    public static final class AccountPosition implements BaseColumns {

        public static final String TABLE_NAME = "accountposition";

        public static final String PATH = TABLE_NAME;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static final String COLUMN_ACCOUNT_ID = "account_id";
        public static final String COLUMN_ACCOUNT_NAME = "account_name";
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_POSITION_NAME = "position_name";
        public static final String COLUMN_LAST = "last";
        public static final String COLUMN_CHANGE = "change";
        public static final String COLUMN_CLOSE = "close";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW52 = "low52";
        public static final String COLUMN_HIGH52 = "high52";
        public static final String COLUMN_VOLUME = "volume";
        public static final String COLUMN_AVG_VOLUME = "avg_volume";
        public static final String COLUMN_PE = "pe";
        public static final String COLUMN_EPS = "eps";
        public static final String COLUMN_YIELD = "yield";
        public static final String COLUMN_DATE = "date";

        public static final String[] PROJECTION = {
                Account.TABLE_NAME + "." + Account._ID,
                Position.TABLE_NAME + "." + Position._ID,
                Account.COLUMN_ACCOUNT_NAME,
                Position.COLUMN_SYMBOL,
                Position.COLUMN_POSITION_NAME,
                Position.COLUMN_LAST,
                Position.COLUMN_CHANGE,
                Position.COLUMN_CLOSE,
                Position.COLUMN_OPEN,
                Position.COLUMN_LOW,
                Position.COLUMN_LOW52,
                Position.COLUMN_HIGH52,
                Position.COLUMN_VOLUME,
                Position.COLUMN_AVG_VOLUME,
                Position.COLUMN_PE,
                Position.COLUMN_EPS,
                Position.COLUMN_YIELD,
                Position.COLUMN_DATE
        };

        public static class PROJECTION_INDEX {
            public static final int ACCOUNT_ID = 0;
            public static final int POSITION_ID = 1;
            public static final int ACCOUNT_NAME = 2;
            public static final int SYMBOL = 3;
            public static final int POSITION_NAME = 4;
            public static final int LAST = 5;
            public static final int CHANGE = 6;
            public static final int CLOSE = 7;
            public static final int OPEN = 8;
            public static final int LOW = 9;
            public static final int LOW52 = 10;
            public static final int HIGH52 = 11;
            public static final int VOLUME = 12;
            public static final int AVG_VOLUME = 13;
            public static final int PE = 14;
            public static final int EPS = 15;
            public static final int YIELD = 16;
            public static final int DATE = 17;
        }

        public static final String TABLES = Account.TABLE_NAME + " INNER JOIN " + Position.TABLE_NAME +
                " ON " + Account.TABLE_NAME + "." + Account._ID +
                " = " + Position.TABLE_NAME + "." + Position.COLUMN_ACCOUNT_ID;

        public static final String SELECTION_ACCOUNT_ID = Account.TABLE_NAME + "." + Account._ID + " = ? ";
        public static final String SELECTION_ACCOUNT_ID_AND_POSITION_ID = Account.TABLE_NAME + "." + Account._ID + " = ? AND " + Position._ID + " = ? ";
        public static final String SELECTION_ACCOUNT_ID_AND_POSITION_SYMBOL = Account.TABLE_NAME + "." + Account._ID + " = ? AND " + Position.COLUMN_SYMBOL + " = ? ";
        public static final String SORT_ORDER = Account.TABLE_NAME + "." + Account._ID + ", " + Position.TABLE_NAME + "." + Position._ID + " ASC";

        public static long getAccountId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getPositionId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static String getSymbol(Uri uri) {
            return uri.getPathSegments().get(3);
        }


        public static Uri getUri(long account_id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(account_id)).build();
        }

        public static Uri getUri(long account_id, long position_id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(account_id)).appendPath(Long.toString(position_id)).build();
        }

        public static Uri getUri(long account_id, String symbol) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(account_id)).appendPath(symbol).build();
        }
    }
}
