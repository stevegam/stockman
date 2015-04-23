package com.gamberale.android.stockman.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stevegam on 4/15/2015.
 */
public class StockPosition implements Parcelable {

    public long id;
    public long account_id;
    // s
    public String symbol;
    // n
    public String position_name;
    // l1
    public float last;
    // c1
    public float change;
    // p
    public float close;
    // o
    public float open;
    // g
    public float low;
    // h
    public float high;
    // j
    public float low52;
    // k
    public float high52;
    // v
    public long volume;
    //a2
    public long avg_volume;
    // r
    public long pe;
    // e7
    public float eps;
    // y
    public float yield;
    // d1t1
    public long date;

    public StockPosition() {
        id = 0;
        account_id = 0;
        symbol = "";
        position_name = "";
        last = 0;
        change = 0;
        close = 0;
        open = 0;
        low = 0;
        high = 0;
        low52 = 0;
        high52 = 0;
        volume = 0;
        avg_volume = 0;
        pe = 0;
        eps = 0;
        yield = 0;
        date = 0;
    }

    public StockPosition(String position) {
        String[] aPosition = position.split("\t", -1);
        if (aPosition != null && aPosition.length > 1) {
            symbol = aPosition[0];
            position_name = aPosition[1];
        }
    }

    @Override
    public String toString() {
        return symbol + "\t" + position_name;
    }

    public String talk() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(position_name);
        stringBuilder.append("\t");

        stringBuilder.append(symbol);
        stringBuilder.append("\t");

        stringBuilder.append(last);
        stringBuilder.append("\t");

        if (change > 0) {
            stringBuilder.append("up\t" + Float.toString(change));
        } else {
            stringBuilder.append("down\t" + Float.toString(change));
        }

        stringBuilder.append("\tor\t" + Float.toString(change/last*100) + "%");

        stringBuilder.append("\ton volume of\t" + volume + "\tshares");

        return stringBuilder.toString();
    }

    public ContentValues getValues() {

        ContentValues values = new ContentValues();
        values.put(PortfolioContract.Position.COLUMN_ACCOUNT_ID, account_id);
        values.put(PortfolioContract.Position.COLUMN_SYMBOL, symbol);
        values.put(PortfolioContract.Position.COLUMN_POSITION_NAME, position_name);
        values.put(PortfolioContract.Position.COLUMN_LAST, last);
        values.put(PortfolioContract.Position.COLUMN_CHANGE, change);
        values.put(PortfolioContract.Position.COLUMN_CLOSE, close);
        values.put(PortfolioContract.Position.COLUMN_OPEN, open);
        values.put(PortfolioContract.Position.COLUMN_LOW, low);
        values.put(PortfolioContract.Position.COLUMN_HIGH, high);
        values.put(PortfolioContract.Position.COLUMN_LOW52, low52);
        values.put(PortfolioContract.Position.COLUMN_HIGH52, high52);
        values.put(PortfolioContract.Position.COLUMN_VOLUME, volume);
        values.put(PortfolioContract.Position.COLUMN_AVG_VOLUME, avg_volume);
        values.put(PortfolioContract.Position.COLUMN_PE, pe);
        values.put(PortfolioContract.Position.COLUMN_EPS, eps);
        values.put(PortfolioContract.Position.COLUMN_YIELD, yield);
        values.put(PortfolioContract.Position.COLUMN_DATE, date);
        return values;
    }

    // Parcelling part
    public StockPosition(Parcel in){
        String[] data = new String[18];

        in.readStringArray(data);
        this.id = Long.parseLong(data[0]);
        this.account_id = Long.parseLong(data[1]);
        this.symbol = data[2];
        position_name = data[3];
        this.last = Float.parseFloat(data[4]);
        this.change = Float.parseFloat(data[5]);
        this.close = Float.parseFloat(data[6]);
        this.open = Float.parseFloat(data[7]);
        this.low = Float.parseFloat(data[8]);
        this.high = Float.parseFloat(data[9]);
        this.low52 = Float.parseFloat(data[10]);
        this.high52 = Float.parseFloat(data[11]);
        this.volume = Long.parseLong(data[12]);
        this.avg_volume = Long.parseLong(data[13]);
        this.pe = Long.parseLong(data[14]);
        this.eps = Float.parseFloat(data[15]);
        this.yield = Float.parseFloat(data[16]);
        this.date = Long.parseLong(data[17]);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                Long.toString(this.id),
                Long.toString(this.account_id),
                symbol,
                position_name,
                Float.toString(last),
                Float.toString(change),
                Float.toString(close),
                Float.toString(open),
                Float.toString(low),
                Float.toString(high),
                Float.toString(low52),
                Float.toString(high52),
                Long.toString(volume),
                Long.toString(avg_volume),
                Long.toString(pe),
                Float.toString(eps),
                Float.toString(yield),
                Long.toString(date)}
        );
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public StockPosition createFromParcel(Parcel in) {
            return new StockPosition(in);
        }

        public StockPosition[] newArray(int size) {
            return new StockPosition[size];
        }
    };
}
