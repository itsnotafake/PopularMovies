package com.example.devin.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.devin.popularmovies.data.MovieContract;

/**
 * Created by Devin on 10/18/2016.
 */

public class UpdateFavoriteTask extends AsyncTask<Object, Void, Void> {

    private final String LOG_TAG = UpdateFavoriteTask.class.getSimpleName();
    private Context mContext;
    private Movie mMovie;

    public UpdateFavoriteTask(Context context){ mContext = context;}

    @Override
    public Void doInBackground(Object... params){
        mMovie = (Movie)params[0];

        Uri uri = MovieContract.MovieEntry.buildMovieUriWithMovieId(mMovie.getId());
        Log.w(LOG_TAG, "Uri is: " + uri);
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, mMovie.getFavorite());
        int rowsUpdated = mContext.getContentResolver().update(
                uri,
                cv,
                null,
                null
        );
        Log.w(LOG_TAG, "RowsUpdate: "+ rowsUpdated);
        return null;
    }
}
