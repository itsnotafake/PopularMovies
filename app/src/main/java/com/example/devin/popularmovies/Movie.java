package com.example.devin.popularmovies;

import java.io.Serializable;

/**
 * Created by Devin on 9/14/2016.
 */
public class Movie implements Serializable{
    public String title;
    public String synopsis;
    public String release;
    public String poster_path;
    public String rating;

    public Movie(String t, String s, String re, String p_p, String ra){
        super();
        title = t;
        synopsis = s;
        release = re;
        poster_path = p_p;
        rating = ra;
    }

    public String toString(){
        return ("Title: " + title +
        ", Synopsis: " + synopsis +
        ", Release: " + release +
        ", Poster_Path: " + poster_path +
        ", Rating: " + rating);
    }

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

    public String getRating(){
        return rating;
    }
}
