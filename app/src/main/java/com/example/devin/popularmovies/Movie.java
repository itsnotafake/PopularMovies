package com.example.devin.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Devin on 9/14/2016.
 */
public class Movie implements Parcelable{
    public int id;
    public String title;
    public String synopsis;
    public String release;
    public String poster_path;
    public long rating;
    public int favorite;

    public String movieReviewUri;
    public String movieTrailerUri;

    Movie(int i, String t, String s, String re, String pp, long ra, int fa, Uri mru, Uri mtu){
        id = i;
        title = t;
        synopsis = s;
        release = re;
        poster_path = pp;
        rating = ra;
        favorite = fa;

        movieReviewUri = mru.toString();
        movieTrailerUri = mtu.toString();
    }

    Movie (Parcel in){
        this.id = in.readInt();
        this.title = in.readString();
        this.synopsis = in.readString();
        this.release = in.readString();
        this.poster_path = in.readString();
        this.rating = in.readLong();
        this.favorite = in.readInt();

        this.movieReviewUri = in.readString();
        this.movieTrailerUri = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeString(release);
        dest.writeString(poster_path);
        dest.writeLong(rating);
        dest.writeInt(favorite);
        dest.writeString(movieReviewUri);
        dest.writeString(movieTrailerUri);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>(){

        public Movie createFromParcel(Parcel in){
            return new Movie(in);
        }

        public Movie[] newArray(int size){
            return new Movie[size];
        }
    };

    public String toString(){
        return ("Title: " + title + ", Id: " + id +
        ", Synopsis: " + synopsis +
        ", Release: " + release +
        ", Poster_Path: " + poster_path +
        ", Rating: " + rating +
        ", movieTrailerUri: " + movieTrailerUri +
        ", movieReviewUri: " + movieReviewUri);
    }

    public int getId() { return id; }

    public String getTitle(){
        return title;
    }

    public String getSynopsis(){
        return synopsis;
    }

    public String getRelease(){
        return release;
    }

    public String getPosterPath(){
        return poster_path;
    }

    public Long getRating(){
        return rating;
    }

    public int getFavorite() { return favorite; }

    public void setFavorite(int i){
        favorite = i;
    }

    public String getMovieTrailerUri() { return movieTrailerUri; }

    public String getMovieReviewUri() { return movieReviewUri; }
}
