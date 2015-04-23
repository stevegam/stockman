/**
 * Created by stevegam on 4/14/2015.
 */
package com.gamberale.android.stockman.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gamberale.android.stockman.data.PortfolioContract.Account;
import com.gamberale.android.stockman.data.PortfolioContract.Position;


public class PortfolioDbHelper extends SQLiteOpenHelper {

    public PortfolioDbHelper(Context context) {
        super(context, PortfolioContract.DATABASE_NAME, null, PortfolioContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(createTable(Account.TABLE_NAME, Account.COLUMNS, Account.CONSTRAINTS));
        sqLiteDatabase.execSQL(createTable(Position.TABLE_NAME, Position.COLUMNS, Position.CONSTRAINTS));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Account.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Position.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private String createTable(String table_name, Column[] columns, String Constraints) {

        String sql = "CREATE TABLE " + table_name + " (";

        for (int i=0; i < columns.length; i++) {

            Column col = columns[i];

           sql += col.name + " " + col.type;

           if (col.key) {
               sql += " PRIMARY KEY";
           }
           if (col.auto) {
                sql += " AUTOINCREMENT";
           }
           if (col.unique) {
                sql += " UNIQUE";
           }
           if (!col.nulls) {
                sql += " NOT NULL";
           }
           if (i < columns.length-1) {
               sql += ", ";
           }
        }

        if (Constraints != null) {
            sql += ", " + Constraints;
        }

        sql += ");";

        return sql;
    }
}
