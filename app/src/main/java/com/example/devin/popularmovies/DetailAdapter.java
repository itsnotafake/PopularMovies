package com.example.devin.popularmovies;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devin.popularmovies.service.UpdateFavoriteService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Devin on 10/18/2016.
 */

public class DetailAdapter extends RecyclerView.Adapter {
    private static final String LOG_TAG = DetailAdapter.class.getSimpleName();
    private Context mContext;
    private Activity mActivity;

    private Movie mMovie;
    private ArrayList<String> mTrailerTitleList;
    private ArrayList<String> mTrailerWatchList;
    private ArrayList<String> mReviewUserList;
    private ArrayList<String> mReviewReviewList;
    private static final int VIEW_TYPE_MAIN = 0;
    private static final int VIEW_TYPE_TRAILERS = 1;
    private static final int VIEW_TYPE_REVIEWS = 2;

    private int mButtonPosition;

    private String base_URL = "http://image.tmdb.org/t/p/";
    //Other sizes include w92, w154, w185, w342, w500, w780, or original
    private String image_size = "w500";

    public static class ViewHolderMain extends RecyclerView.ViewHolder{
        ImageView movie_Poster;
        TextView movie_Title;
        TextView movie_Release;
        TextView movie_Rating;
        TextView movie_Synopsis;
        Button movie_Favorite;

        public ViewHolderMain(View view){
            super(view);
            movie_Poster = (ImageView)view.findViewById(R.id.detail_poster);
            movie_Title = (TextView)view.findViewById(R.id.detail_title);
            movie_Release = (TextView)view.findViewById(R.id.detail_release);
            movie_Rating = (TextView)view.findViewById(R.id.detail_rating);
            movie_Synopsis = (TextView)view.findViewById(R.id.detail_synopsis);
            movie_Favorite = (Button)view.findViewById(R.id.detail_favorite);
        }
    }

    public static class ViewHolderTrailer extends RecyclerView.ViewHolder{
        Button trailer_Title;

        public ViewHolderTrailer(View v){
            super(v);
            trailer_Title = (Button)v.findViewById(R.id.trailer_button);
        }
    }

    public static class ViewHolderReview extends RecyclerView.ViewHolder{
        TextView review_User;
        TextView review_Review;

        public ViewHolderReview(View v){
            super(v);
            review_User = (TextView) v.findViewById(R.id.review_user);
            review_Review = (TextView) v.findViewById(R.id.review_review);
        }
    }

    public DetailAdapter(Activity a,
                         Movie m,
                         ArrayList<String> tTL, ArrayList<String> tWL,
                         ArrayList<String> rUL, ArrayList<String> rRL){
        mActivity = a;
        mMovie = m;
        mTrailerTitleList = tTL;
        mTrailerWatchList = tWL;
        mReviewUserList = rUL;
        mReviewReviewList = rRL;
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return VIEW_TYPE_MAIN;
        }else if((0 < position) && (position < mTrailerTitleList.size()+1)){
            return VIEW_TYPE_TRAILERS;
        }else{
            return VIEW_TYPE_REVIEWS;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        mContext = parent.getContext();
        switch(viewType){
            case VIEW_TYPE_MAIN:
                View main_View = LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.fragment_detail_main, parent, false);
                ViewHolderMain viewHolder_Main = new ViewHolderMain(main_View);
                return viewHolder_Main;
            case VIEW_TYPE_TRAILERS:
                View trailer_View = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_detail_trailer, parent, false);
                ViewHolderTrailer viewHolder_Trailer = new ViewHolderTrailer(trailer_View);
                return viewHolder_Trailer;
            case VIEW_TYPE_REVIEWS:
                View review_View = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_detail_review, parent, false);
                ViewHolderReview viewHolder_Review = new ViewHolderReview(review_View);
                return viewHolder_Review;
            default:
                throw new UnsupportedOperationException("onCreateViewHolder view type doest not match any cases");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        int switch_position = getItemViewType(position);
        switch (switch_position){
            case VIEW_TYPE_MAIN:
                String builtURL = base_URL + image_size + mMovie.getPosterPath();
                Picasso.with(mContext)
                        .load(builtURL)
                        .into(((ViewHolderMain) vh).movie_Poster);
                ((ViewHolderMain) vh).movie_Title.setText(mMovie.getTitle());
                ((ViewHolderMain) vh).movie_Release.setText("Released: " + mMovie.getRelease());
                ((ViewHolderMain) vh).movie_Rating.setText("Rating: " + String.valueOf(mMovie.getRating()));
                ((ViewHolderMain) vh).movie_Synopsis.setText(mMovie.getSynopsis());

                Intent intent = new Intent(mActivity, UpdateFavoriteService.class);
                intent.putExtra(UpdateFavoriteService.MOVIE_ID_EXTRA, mMovie.getId());
                if(mMovie.getFavorite() == 1){
                    ((ViewHolderMain) vh).movie_Favorite.setBackgroundColor(0xFF00FFFF);
                }else{
                    ((ViewHolderMain) vh).movie_Favorite.setBackgroundColor(0xFFBFBFBF);
                }
                ((ViewHolderMain) vh).movie_Favorite.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(mMovie.getFavorite() == 0){
                            mMovie.setFavorite(1);
                            Intent intent = new Intent(v.getContext(), UpdateFavoriteService.class);
                            intent.putExtra(UpdateFavoriteService.MOVIE_ID_EXTRA, mMovie.getId());
                            intent.putExtra(UpdateFavoriteService.MOVIE_FAVORITE_EXTRA, mMovie.getFavorite());
                            v.getContext().startService(intent);
                            //new UpdateFavoriteTask(mContext).execute(mMovie);
                            v.setBackgroundColor(0xFF00FFFF);
                        }else if(mMovie.getFavorite() == 1){
                            mMovie.setFavorite(0);
                            Intent intent = new Intent(v.getContext(), UpdateFavoriteService.class);
                            intent.putExtra(UpdateFavoriteService.MOVIE_ID_EXTRA, mMovie.getId());
                            intent.putExtra(UpdateFavoriteService.MOVIE_FAVORITE_EXTRA, mMovie.getFavorite());
                            v.getContext().startService(intent);
                            //new UpdateFavoriteTask(mContext).execute(mMovie);
                            v.setBackgroundColor(0xFFBFBFBF);
                        }else{
                            Log.e(LOG_TAG, "mMovie.getFavorite returned a bad number");
                        }
                    }
                });
                break;
            case VIEW_TYPE_TRAILERS:
                ((ViewHolderTrailer) vh).trailer_Title.setText(mTrailerTitleList.get(position-1));
                mButtonPosition = position-1;
                ((ViewHolderTrailer) vh).trailer_Title.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        String watch = mTrailerWatchList.get(mButtonPosition);
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + watch));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + watch));
                        try {
                            mContext.startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            mContext.startActivity(webIntent);
                        }
                    }
                });
                break;
            case VIEW_TYPE_REVIEWS:
                int adjusted_position = position - (1+ mTrailerTitleList.size());
                ((ViewHolderReview) vh).review_User.setText(mReviewUserList.get(adjusted_position));
                ((ViewHolderReview) vh).review_Review.setText(mReviewReviewList.get(adjusted_position));
                break;
            default:
                throw new UnsupportedOperationException("onBindViewHolder position does not match any case");
        }
        return;
    }

    @Override
    public int getItemCount(){
        return 1 + mTrailerTitleList.size() + mReviewReviewList.size();
    }
}
