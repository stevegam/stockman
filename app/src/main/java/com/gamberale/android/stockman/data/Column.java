package com.gamberale.android.stockman.data;

/**
 * Created by stevegam on 4/18/2015.
 */
public class Column {
    public static final String TYPE_INTEGER = "integer";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_REAL = "real";
    public static final String TYPE_BLOB = "blob";

    Column(String Name, String Type, boolean Nulls, boolean Auto, boolean Key, boolean Unique) {
        name = Name;
        type = Type;
        nulls = Nulls;
        auto = Auto;
        key = Key;
        unique = Unique;
    }
    public String name;
    public String type;
    public boolean nulls;
    public boolean auto;
    public boolean key;
    public boolean unique;
}
