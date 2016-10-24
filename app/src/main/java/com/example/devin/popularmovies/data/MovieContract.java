package com.example.devin.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Devin on 10/11/2016.
 */

public class MovieContract {

    private static final String LOG_TAG = MovieContract.class.getSimpleName();

    public static final String CONTENT_AUTHORITY = "com.example.devin.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String MOVIE_ID_PATH = "movieId";
        public static final String MOVIE_FAVORITE_PATH = "movieFavorite";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";

        //Id of the Movie
        public static final String COLUMN_MOVIE_ID = "movie_id";
        //Movie Title attribute
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        //Movie Synopsis attribute
        public static final String COLUMN_MOVIE_SYNOPSIS = "movie_synopsis";
        //Movie release attribute
        public static final String COLUMN_MOVIE_RELEASE = "movie_release";
        //Movie poster_path attribute
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";
        //Movie rating attribute
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        //Movie category attribute. 1=TopRated, 2=Popular (there can be movies with 2 entries, one for toprated and one for popular)
        public static final String COLUMN_MOVIE_CATEGORY = "movie_category";
        //Movie boolean that will affirm whether or not the movie is a favorite 0=not fav, 1 = fav
        public static final String COLUMN_MOVIE_FAVORITE = "movie_favorite";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieUriWithCategory(int i){
            String category_string = String.valueOf(i);
            return CONTENT_URI.buildUpon().appendPath(category_string).build();
        }
        public static Uri buildMovieUriWithMovieId(int i){
            return CONTENT_URI.buildUpon().appendPath(MOVIE_ID_PATH).appendPath(String.valueOf(i)).build();
        }
        public static Uri buildMovieUriWithFavorite(){
            return CONTENT_URI.buildUpon().appendPath(MOVIE_FAVORITE_PATH).appendPath("1").build();
        }

        public static String getCategoryFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getMovieIdFromUri(Uri uri){ return uri.getPathSegments().get(2); }
        public static String getMovieFavoriteFromUri(Uri uri){ return uri.getPathSegments().get(2); }
    }

    public static final class ReviewEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "reviews";

        //Movie title attribute
        public static final String COLUMN_REVIEW_ID = "review_id";
        //User that create the review
        public static final String COLUMN_REVIEW_USER = "review_user";
        //Movie review
        public static final String COLUMN_REVIEW_REVIEW = "review_review";

        public static Uri buildReviewUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewUriWithId(int id){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
        public static String getIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TrailerEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static final String TABLE_NAME = "trailers";

        //Name of the movie associated with the trailer
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        //Youtube's title for the trailer
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        //watch Id for the youtube video
        public static final String COLUMN_TRAILER_WATCH = "trailer_watch";

        public static Uri buildTrailerUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerUriWithId(int id){
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        }
        public static String getIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
