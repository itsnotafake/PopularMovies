package com.example.devin.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.devin.popularmovies.R;
import com.example.devin.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Devin on 10/12/2016.
 */

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter{
    public final static String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 600;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/2;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private final String API_KEY = getContext().getString(R.string.API_KEY);

    public MovieSyncAdapter(Context context, boolean autoInitialize){
        super(context,autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient content, SyncResult syncResult){
        //Delete all of our entries in the movie database
        //getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        String base_URL = getContext().getString(R.string.base_URL);
        String APPID = "?api_key=" + API_KEY;

        //example =  http://api.themoviedb.org/3/movie/popular?api_key=key...
        String url_string_popular = base_URL + "popular" + APPID;
        String url_string_toprated = base_URL + "top_rated" + APPID;

        //Updated movie database with popular movies
        try {
            URL url = new URL(url_string_popular);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return;
            }
            movieJsonStr = buffer.toString();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }
        try {
            getMovieDataFromJson(movieJsonStr, "popular");
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //update movie database with movies from top_rated
        try {
            URL url = new URL(url_string_toprated);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return;
            }
            movieJsonStr = buffer.toString();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }
        try {
            getMovieDataFromJson(movieJsonStr, "top_rated");
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return;
    }

    public void getMovieDataFromJson(String json, String category) throws JSONException{
        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "original_title";
        final String TMDB_SYNOPSIS = "overview";
        final String TMDB_RELEASE = "release_date";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_RATING = "vote_average";

        int category_number = 0;
        if(category.equals("popular")){
            category_number = 2;
        }else if(category.equals("top_rated")){
            category_number = 1;
        }

        JSONObject movieJson = new JSONObject(json);
        JSONArray movieJsonArray = movieJson.getJSONArray(TMDB_RESULTS);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieJsonArray.length());

        for(int i = 0; i < movieJsonArray.length(); i++){
            JSONObject movie = movieJsonArray.getJSONObject(i);

            int movie_id = movie.getInt(TMDB_ID);
            String title = movie.getString(TMDB_TITLE);
            String synopsis = movie.getString(TMDB_SYNOPSIS);
            String release = movie.getString(TMDB_RELEASE);
            String poster = movie.getString(TMDB_POSTER);
            String rating = Long.toString(movie.getLong(TMDB_RATING));

            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID ,movie_id);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, synopsis);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE, release);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, poster);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, rating);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_CATEGORY, category_number);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, 0);

            Uri uri = MovieContract.MovieEntry.buildMovieUriWithMovieId(movie_id);
            Cursor c = getContext().getContentResolver().query(
                    uri,
                    null,
                    null,
                    null,
                    null
            );
            if(!c.moveToNext()) {
                cVVector.add(cv);
            }
            c.close();
        }
        if(cVVector.size() > 0){
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
