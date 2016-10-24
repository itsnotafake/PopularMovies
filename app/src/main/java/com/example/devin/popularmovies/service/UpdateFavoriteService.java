package com.example.devin.popularmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.devin.popularmovies.data.MovieContract;

/**
 * Created by Devin on 10/20/2016.
 */

public class UpdateFavoriteService extends IntentService {

    private final String LOG_TAG = UpdateFavoriteService.class.getSimpleName();
    public static final String MOVIE_ID_EXTRA = "mie";
    public static final String MOVIE_FAVORITE_EXTRA = "mfe";
    private int mMovie_Id = -1;
    private int mMovie_Favorite = -1;

    public UpdateFavoriteService(){
        super("UpdateFavoriteService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        mMovie_Id = intent.getIntExtra(MOVIE_ID_EXTRA, -1);
        mMovie_Favorite = intent.getIntExtra(MOVIE_FAVORITE_EXTRA, -1);
        if(mMovie_Id == -1){
            Log.e(LOG_TAG, "Bad movie id sent to onBindIntent");
            return;
        }if(mMovie_Favorite == -1){
            Log.e(LOG_TAG, "Bad movie favorite value");
            return;
        }
        Uri uri = MovieContract.MovieEntry.buildMovieUriWithMovieId(mMovie_Id);
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, mMovie_Favorite);
        int rowsUpdated = getContentResolver().update(
                uri,
                cv,
                null,
                null
        );
        return;
    }
}
