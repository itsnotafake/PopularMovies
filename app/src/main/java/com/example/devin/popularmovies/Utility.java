package com.example.devin.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Devin on 10/13/2016.
 */

public class Utility {

    private final static String LOG_TAG = Utility.class.getSimpleName();

    public static int getCurrentCategory(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String category = prefs.getString(context.getString(R.string.pref_sortBy_key),
                context.getString(R.string.pref_sortBy_default));
        if(category.equals(context.getString(R.string.pref_sortBy_toprated))){
            return 1;
        }else if(category.equals(context.getString(R.string.pref_sortBy_popular))) {
            return 2;
        }else if(category.equals(context.getString(R.string.pref_sortBy_favorite))){
            return 3;
        }else{
            Log.d(LOG_TAG, "Category is " + category);
            return 4;
        }
    }
}
