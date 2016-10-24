package com.example.devin.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.example.devin.popularmovies.data.MovieContract;
import com.example.devin.popularmovies.service.FetchReviewAndTrailerService;
import com.example.devin.popularmovies.service.UpdateFavoriteService;

import java.util.ArrayList;

/**
 * Created by Devin on 10/15/2016.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public Movie mMovie;
    private RecyclerView mRecyclerView;
    private DetailAdapter mDetailAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> mTrailerTitleList;
    private ArrayList<String> mTrailerWatchList;
    private ArrayList<String> mReviewUserList;
    private ArrayList<String> mReviewReviewList;

    private static final int REVIEW_LOADER = 0;
    private static final int TRAILER_LOADER = 1;

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_USER,
            MovieContract.ReviewEntry.COLUMN_REVIEW_REVIEW
    };
    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_USER = 1;
    static final int COL_REVIEW_REVIEW = 2;

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.COLUMN_TRAILER_WATCH
    };
    static final int COL_TRAILER_ID = 0;
    static final int COL_TRAILER_NAME = 1;
    static final int COL_TRAILER_WATCH = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null){
            mMovie = arguments.getParcelable("MyMovie");
        }else{
            Intent intent = getActivity().getIntent();
            if(intent != null && intent.hasExtra("MyMovie")){
                mMovie = intent.getParcelableExtra("MyMovie");
            }else{
                Log.e(LOG_TAG, "Either intent not found, or doenst have Extra 'MyMovie'");
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_detail_recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mTrailerTitleList = new ArrayList<String>();
        mTrailerWatchList = new ArrayList<String>();
        mReviewUserList = new ArrayList<String>();
        mReviewReviewList = new ArrayList<String>();

        if(mMovie != null) {
            mRecyclerView.setLayoutManager(mLayoutManager);
            mDetailAdapter = new DetailAdapter(
                    getActivity(),
                    mMovie, mTrailerTitleList,
                    mTrailerWatchList,
                    mReviewUserList,
                    mReviewReviewList
            );
            mRecyclerView.setAdapter(mDetailAdapter);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(TRAILER_LOADER, null, this);
        getLoaderManager().restartLoader(REVIEW_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void updateReviewAndTrailerData() {
        Intent intent = new Intent(getActivity(), FetchReviewAndTrailerService.class);
        intent.putExtra(FetchReviewAndTrailerService.MOVIE_ID_EXTRA, mMovie.getId());
        getActivity().startService(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(mMovie != null) {
            if (i == REVIEW_LOADER) {
                updateReviewAndTrailerData();
                Uri uri = MovieContract.ReviewEntry.buildReviewUriWithId(mMovie.getId());

                return new CursorLoader(getActivity(),
                        uri,
                        REVIEW_COLUMNS,
                        null,
                        null,
                        null
                );
            } else if (i == TRAILER_LOADER) {
                Uri uri = MovieContract.TrailerEntry.buildTrailerUriWithId(mMovie.getId());

                return new CursorLoader(getActivity(),
                        uri,
                        TRAILER_COLUMNS,
                        null,
                        null,
                        null
                );
            } else {
                Log.e(LOG_TAG, "Passed in invalid Loader tag");
                return null;
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
            if (loader.getId() == REVIEW_LOADER) {
                while (data.moveToNext()) {
                    mReviewUserList.add(data.getString(COL_REVIEW_USER));
                    mReviewReviewList.add(data.getString(COL_REVIEW_REVIEW));
                }
                mDetailAdapter = new DetailAdapter(
                        getActivity(),
                        mMovie,
                        mTrailerTitleList,
                        mTrailerWatchList,
                        mReviewUserList,
                        mReviewReviewList
                );
                mRecyclerView.setAdapter(mDetailAdapter);

            } else if (loader.getId() == TRAILER_LOADER) {
                while (data.moveToNext()) {
                    mTrailerTitleList.add(data.getString(COL_TRAILER_NAME));
                    mTrailerWatchList.add(data.getString(COL_TRAILER_WATCH));
                }
                mDetailAdapter = new DetailAdapter(
                        getActivity(),
                        mMovie,
                        mTrailerTitleList,
                        mTrailerWatchList,
                        mReviewUserList,
                        mReviewReviewList
                );
                mRecyclerView.setAdapter(mDetailAdapter);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}