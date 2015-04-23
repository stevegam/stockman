package com.gamberale.android.stockman.data;

/**
 * Created by stevegam on 4/18/2015.
 */
import android.content.ContentValues;

public class Account {
    public long account_id;
    public String account_name;

    @Override
    public String toString() {
        return account_name;
    }

    public ContentValues getValues() {

        ContentValues values = new ContentValues();
        values.put(PortfolioContract.Account.COLUMN_ACCOUNT_NAME, account_name);
        return values;
    }
}
