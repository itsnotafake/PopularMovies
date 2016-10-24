package com.example.devin.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.example.devin.popularmovies.data.MovieContract;
import com.example.devin.popularmovies.sync.MovieSyncAdapter;

import java.util.ArrayList;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private MovieAdapter mMovie_Adapter;

    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final int FORECAST_LOADER = 0;

    private static final String[] MOVIE_COLUMNS= {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
            MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE
    };

    static final int COL_MOVIE__ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_SYNOPSIS = 3;
    static final int COL_MOVIE_RELEASE = 4;
    static final int COL_MOVIE_POSTER = 5;
    static final int COL_MOVIE_RATING = 6;
    static final int COL_MOVIE_FAVORITE = 7;

    public interface Callback {

        //A callback interface that all activities containing this fagrment must
        //implement. This mechanism allows activities to be notified of item selections.
        public void onItemSelected(Movie m);
    }

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovie_Adapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.main_gridview);
        mGridView.setAdapter(mMovie_Adapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null) {
                    Movie movie = new Movie(
                            cursor.getInt(COL_MOVIE_ID),
                            cursor.getString(COL_MOVIE_TITLE),
                            cursor.getString(COL_MOVIE_SYNOPSIS),
                            cursor.getString(COL_MOVIE_RELEASE),
                            cursor.getString(COL_MOVIE_POSTER),
                            cursor.getLong(COL_MOVIE_RATING),
                            cursor.getInt(COL_MOVIE_FAVORITE),
                            MovieContract.ReviewEntry.buildReviewUriWithId(
                                    cursor.getInt(COL_MOVIE_ID)),
                            MovieContract.TrailerEntry.buildTrailerUriWithId(
                                    cursor.getInt(COL_MOVIE_ID))
                            );
                    ((Callback) getActivity())
                            .onItemSelected(movie);
                }
                mPosition = position;
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        updateMovies();
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        //updateMovies();
    }

    @Override
    public void onResume(){
        super.onResume();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        if(mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void updateMovies(){
        ConnectivityManager cm = (ConnectivityManager) super.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            MovieSyncAdapter.syncImmediately(getActivity());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        int category = Utility.getCurrentCategory(getActivity());
        if(category == 4){
            throw new RuntimeException("Bad category " + category);
        }else if(category == 3){
            Uri movieWithFavoriteUri = MovieContract.MovieEntry.buildMovieUriWithFavorite();

            return new CursorLoader(getActivity(),
                    movieWithFavoriteUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }else {
            Uri movieWithCategoryUri = MovieContract.MovieEntry.buildMovieUriWithCategory(category);

            return new CursorLoader(getActivity(),
                    movieWithCategoryUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mMovie_Adapter.swapCursor(data);
        if(mPosition != GridView.INVALID_POSITION){
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){ mMovie_Adapter.swapCursor(null);}
}
