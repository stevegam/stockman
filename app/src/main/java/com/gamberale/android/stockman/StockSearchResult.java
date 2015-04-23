package com.gamberale.android.stockman;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stevegam on 4/19/2015.
 */
public class StockSearchResult  implements Parcelable {
    StockSearchResult(String Symbol, String Name) {
        symbol = Symbol;
        name = Name;
    }
    String symbol;
    String name;
    @Override
    public String toString() {

        return symbol + "\t" + name;
    }

    // Parcelling part
    public StockSearchResult(Parcel in){
        String[] data = new String[2];

        this.symbol = data[2];
        this.name = data[1];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                        this.symbol,
                        this.name
                }
        );
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public StockSearchResult createFromParcel(Parcel in) {
            return new StockSearchResult(in);
        }

        public StockSearchResult[] newArray(int size) {
            return new StockSearchResult[size];
        }
    };
}
