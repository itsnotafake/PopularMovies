package com.example.devin.popularmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

/**
 * Created by Devin on 10/20/2016.
 */

public class FetchReviewAndTrailerService extends IntentService {

    private final String LOG_TAG = FetchReviewAndTrailerService.class.getSimpleName();
    public static final String MOVIE_ID_EXTRA = "mie";
    private int mMovie_Id = -1;



    public FetchReviewAndTrailerService(){
        super("FetchReviewAndTrailerService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        mMovie_Id = intent.getIntExtra(MOVIE_ID_EXTRA, -1);
        if(mMovie_Id == -1){
            Log.e(LOG_TAG, "onHandleIntent received bad movieId");
            return;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewJsonStr = null;
        String trailerJsonStr = null;

        String base_URL = getString(R.string.base_URL);
        String APPID = "?api_key=" + getString(R.string.API_KEY);

        String url_string_review = (
                base_URL +
                        mMovie_Id + "/" +
                        getString(R.string.requestReviews) +
                        APPID);
        String url_string_trailer = (
                base_URL +
                        mMovie_Id + "/" +
                        getString(R.string.requestTrailers) +
                        APPID);

        //Send GET request to /movie/{movie_id}/reviews
        /*if query using these details returns null then add a new value
        * to Reviews Database*/
        try {
            URL url = new URL(url_string_review);
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
            reviewJsonStr = buffer.toString();
            try {
                updateDataFromJson(reviewJsonStr, true);
            }catch(JSONException e){
                Log.e(LOG_TAG, "JSONException " + e);
            }
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

        //Send GET request to /movie/{movie_id}/videos
        /*if query using these details returns null then add a new value
        * to Trailers Database*/
        try {
            URL url = new URL(url_string_trailer);
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
            trailerJsonStr = buffer.toString();
            try {
                updateDataFromJson(trailerJsonStr, false);
            }catch(JSONException e){
                Log.e(LOG_TAG, "JSONException " + e);
            }
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
        return;
    }

    private void updateDataFromJson(String json, boolean isReview)throws JSONException{
        final String TMDB_RESULTS = "results";

        //for reviews
        final String TMDB_REVIEW_AUTHOR = "author";
        final String TMDB_REVIEW_CONTENT = "content";

        //for trailers
        final String TMDB_TRAILER_NAME = "name";
        final String TMDB_TRAILER_WATCH = "key";


        // how we get data if this was a review request
        if(isReview){
            Uri reviewUri =
                    MovieContract.ReviewEntry.buildReviewUriWithId(mMovie_Id);
            Cursor cursor = getContentResolver().query(
                    reviewUri,
                    null,
                    null,
                    null,
                    null
            );
            //reviews for this movie have already been added so we use the DB to get review info
            if(cursor.moveToNext()){
                cursor.close();
                return;
            }
            //reviews have not yet been added so must be added and then queried
            else{
                JSONObject reviewJson = new JSONObject(json);
                JSONArray reviewJsonArray = reviewJson.getJSONArray(TMDB_RESULTS);

                for(int i = 0; i < reviewJsonArray.length(); i++){
                    JSONObject review = reviewJsonArray.getJSONObject(i);

                    String review_author = review.getString(TMDB_REVIEW_AUTHOR);
                    String review_content = review.getString(TMDB_REVIEW_CONTENT);

                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, mMovie_Id);
                    cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_USER, review_author);
                    cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_REVIEW, review_content);
                    Log.w(LOG_TAG, "Inserting cv belong to " + review_author);
                    getContentResolver().insert(
                            MovieContract.ReviewEntry.CONTENT_URI,
                            cv
                    );
                }
            }

            //how we get data if this was a trailer request
        }else{
            Uri trailerUri =
                    MovieContract.TrailerEntry.buildTrailerUriWithId(mMovie_Id);
            Cursor cursor = getContentResolver().query(
                    trailerUri,
                    null,
                    null,
                    null,
                    null
            );
            //trailers for this movie have already been added so we use the DB to get review info
            if(cursor.moveToNext()){
                cursor.close();
                return;
            }
            //reviews have not yet been added so must be added and then queried
            else{
                JSONObject trailerJson = new JSONObject(json);
                JSONArray trailerJsonArray = trailerJson.getJSONArray(TMDB_RESULTS);

                for(int i = 0; i < trailerJsonArray.length(); i++){
                    JSONObject trailer = trailerJsonArray.getJSONObject(i);

                    String trailer_name = trailer.getString(TMDB_TRAILER_NAME);
                    String trailer_watch = trailer.getString(TMDB_TRAILER_WATCH);

                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, mMovie_Id);
                    cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer_name);
                    cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_WATCH, trailer_watch);
                    Log.w(LOG_TAG, "Inserting cv named " + trailer_name);
                    getContentResolver().insert(
                            MovieContract.TrailerEntry.CONTENT_URI,
                            cv
                    );
                }
            }
        }
    }
}
