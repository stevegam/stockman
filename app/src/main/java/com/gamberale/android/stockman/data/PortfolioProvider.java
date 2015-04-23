/**
 * Created by stevegam on 4/14/2015.
 */
package com.gamberale.android.stockman.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.gamberale.android.stockman.Utility;
import com.gamberale.android.stockman.data.PortfolioContract.Account;
import com.gamberale.android.stockman.data.PortfolioContract.AccountPosition;
import com.gamberale.android.stockman.data.PortfolioContract.Position;

public class PortfolioProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher;
    private PortfolioDbHelper mOpenHelper;

    static final int ACCOUNT = 100;
    static final int ACCOUNT_ID = 101;

    static final int POSITION = 200;
    static final int POSITION_ID = 201;
    static final int POSITION__ACCOUNT_ID_AND_POSITION_ID = 202;

    static final int ACCOUNT_POSITION = 300;
    static final int ACCOUNT_POSITION_ACCOUNT_ID = 301;
    static final int ACCOUNT_POSITION_ACCOUNT_ID_AND_POSITION_ID = 302;
    static final int ACCOUNT_POSITION_ACCOUNT_ID_AND_SYMBOL = 303;

    static final int INSERT = 0;
    static final int UPDATE = 1;
    static final int DELETE = 2;

    static {

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PortfolioContract.CONTENT_AUTHORITY;

        sUriMatcher.addURI(authority, Account.PATH, ACCOUNT);
        sUriMatcher.addURI(authority, Account.PATH + "/#", ACCOUNT_ID);

        sUriMatcher.addURI(authority, Position.PATH, POSITION);
        sUriMatcher.addURI(authority, Position.PATH + "/#", POSITION_ID);
        sUriMatcher.addURI(authority, Position.PATH + "/#/#", POSITION__ACCOUNT_ID_AND_POSITION_ID);

        sUriMatcher.addURI(authority, AccountPosition.PATH, ACCOUNT_POSITION);
        sUriMatcher.addURI(authority, AccountPosition.PATH + "/#", ACCOUNT_POSITION_ACCOUNT_ID);
        sUriMatcher.addURI(authority, AccountPosition.PATH + "/#/#", ACCOUNT_POSITION_ACCOUNT_ID_AND_POSITION_ID);
        sUriMatcher.addURI(authority, AccountPosition.PATH + "/#/*", ACCOUNT_POSITION_ACCOUNT_ID_AND_SYMBOL);
    }

    private static final SQLiteQueryBuilder sAcountPositionQuery;

    static {
        sAcountPositionQuery = new SQLiteQueryBuilder();
        sAcountPositionQuery.setTables(AccountPosition.TABLES);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PortfolioDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ACCOUNT:
                return Account.CONTENT_DIR_TYPE;
            case ACCOUNT_ID:
                return Account.CONTENT_ITEM_TYPE;
            case POSITION:
                return Position.CONTENT_DIR_TYPE;
            case POSITION_ID:
            case POSITION__ACCOUNT_ID_AND_POSITION_ID:
                return Position.CONTENT_ITEM_TYPE;
            case ACCOUNT_POSITION:
                return AccountPosition.CONTENT_DIR_TYPE;
            case ACCOUNT_POSITION_ACCOUNT_ID_AND_POSITION_ID:
            case ACCOUNT_POSITION_ACCOUNT_ID_AND_SYMBOL:
                return AccountPosition.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case ACCOUNT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Account.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCOUNT_ID: {
                long _id = Long.parseLong(uri.getPathSegments().get(1));
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Account.TABLE_NAME,
                        projection,
                        Account.SELECTION_ID,
                        new String[]{Long.toString(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POSITION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Position.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POSITION_ID: {
                long _id = Long.parseLong(uri.getPathSegments().get(1));
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Position.TABLE_NAME,
                        projection,
                        Position.SELECTION_ID,
                        new String[]{Long.toString(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case POSITION__ACCOUNT_ID_AND_POSITION_ID: {
                long account_id = Long.parseLong(uri.getPathSegments().get(1));
                long _id = Long.parseLong(uri.getPathSegments().get(2));
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Position.TABLE_NAME,
                        projection,
                        Position.SELECTION_ACCOUNT_ID_AND_POSITION_ID,
                        new String[]{Long.toString(account_id), Long.toString(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCOUNT_POSITION: {
                retCursor = sAcountPositionQuery.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCOUNT_POSITION_ACCOUNT_ID_AND_POSITION_ID: {
                long account_id = Long.parseLong(uri.getPathSegments().get(1));
                long position_id = Long.parseLong(uri.getPathSegments().get(2));
                retCursor = sAcountPositionQuery.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        AccountPosition.SELECTION_ACCOUNT_ID_AND_POSITION_ID,
                        new String[]{Long.toString(account_id), Long.toString(position_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCOUNT_POSITION_ACCOUNT_ID_AND_SYMBOL: {
                long account_id = Long.parseLong(uri.getPathSegments().get(1));
                String symbol = uri.getPathSegments().get(2);
                retCursor = sAcountPositionQuery.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        AccountPosition.SELECTION_ACCOUNT_ID_AND_POSITION_SYMBOL,
                        new String[]{Long.toString(account_id), symbol},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id = -1;

        switch (match) {
            case ACCOUNT: {
                if (checkAccountValues(INSERT, values) == 0) {
                    _id = db.insert(Account.TABLE_NAME, null, values);
                }
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(Account.CONTENT_URI, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case POSITION: {
                if (checkPositionValues(INSERT, values) == 0) {
                   _id = db.insert(Position.TABLE_NAME, null, values);
                }
                if (_id > 0)
                    returnUri = ContentUris.withAppendedId(Account.CONTENT_URI, _id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case ACCOUNT:
                rowsDeleted = db.delete(Account.TABLE_NAME, selection, selectionArgs);
                break;
            case POSITION:
                rowsDeleted = db.delete(Position.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case ACCOUNT:
                if (checkAccountValues(UPDATE, values) == 0) {
                    rowsUpdated = db.update(Account.TABLE_NAME, values, selection, selectionArgs);
                }
                break;
            case POSITION:
                if (checkPositionValues(UPDATE, values) == 0) {
                    rowsUpdated = db.update(Position.TABLE_NAME, values, selection, selectionArgs);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String table = null;
        switch (match) {
            case ACCOUNT:
            case POSITION:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        if (match == ACCOUNT) {
                            if (checkAccountValues(INSERT, value) != 0) {
                                continue;
                            }
                            table = Account.TABLE_NAME;
                        } else {
                            table = Position.TABLE_NAME;
                            if (checkPositionValues(INSERT, value) != 0) {
                                continue;
                            }
                        }
                        long _id = db.insert(table, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int checkAccountValues(int action, ContentValues values) {

        return 0;
    }

    private int checkPositionValues(int action, ContentValues values) {

        //TODO: FIX THIS
        if (values.containsKey(Position.COLUMN_DATE)) {
            long dateValue = values.getAsLong(Position.COLUMN_DATE);
            values.put(Position.COLUMN_DATE, Utility.normalizeDate(dateValue));
        }
        return 0;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
