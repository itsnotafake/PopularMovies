package com.example.devin.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Devin on 9/14/2016.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    Context context;
    int layoutResourceId;
    ArrayList<Movie> movies = null;
    private String base_URL = "http://image.tmdb.org/t/p/";
    //Other sizes include w92, w154, w185, w342, w500, w780, or original
    private String image_size = "w342";

    public MovieAdapter(Context c, int layoutRId, ArrayList<Movie> m){
        super(c, layoutRId, m);
        context = c;
        layoutResourceId = layoutRId;
        movies = m;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        MovieHolder holder = null;

        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MovieHolder();
            holder.view = (ImageView)row.findViewById(R.id.grid_item_movie_imageview);

            row.setTag(holder);
        }else{
            holder = (MovieHolder)row.getTag();
        }
        Movie movie = movies.get(position);
        String builtURL = base_URL + image_size + movie.getPosterPath();
        Picasso.with(context)
                .load(builtURL)
                .into(holder.view);

        return row;
    }

    public class MovieHolder{
        ImageView view;
    }
}
