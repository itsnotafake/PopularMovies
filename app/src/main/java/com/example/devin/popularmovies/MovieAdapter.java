package com.example.devin.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Devin on 9/14/2016.
 */
public class MovieAdapter extends CursorAdapter {

    private String base_URL = "http://image.tmdb.org/t/p/";
    //Other sizes include w92, w154, w185, w342, w500, w780, or original
    private String image_size = "w342";

   public static class ViewHolder {
       public final ImageView thumbnail;

       public ViewHolder(View view) {
           thumbnail = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);
       }
   }

    public MovieAdapter(Context context, Cursor c, int flags){super(context, c, flags);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        int layoutId = R.layout.grid_item_movie;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String posterPath = cursor.getString(MovieFragment.COL_MOVIE_POSTER);
        String builtURL = base_URL + image_size + posterPath;
        Picasso.with(context)
                .load(builtURL)
                .into(viewHolder.thumbnail);
    }
}
