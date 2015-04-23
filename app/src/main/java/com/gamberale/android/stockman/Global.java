package com.gamberale.android.stockman;

/**
 * Created by stevegam on 4/18/2015.
 */
public class Global {
    private static Global mInstance = null;

    public boolean SinglePaneLayout;
    public String Text;
    public boolean DEBUG = false;

    protected Global(){}

    public static synchronized Global getInstance(){
        if(null == mInstance){
            mInstance = new Global();
        }
        return mInstance;
    }
}
