package com.example.devin.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private Movie movie;

        private String base_URL = "http://image.tmdb.org/t/p/";
        //Other sizes include w92, w154, w185, w342, w500, w780, or original
        private String image_size = "w500";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if(intent != null && intent.hasExtra("MyMovie")){
                movie = (Movie)intent.getSerializableExtra("MyMovie");

                String builtURL = base_URL + image_size + movie.getPosterPath();
                Picasso.with(getActivity())
                        .load(builtURL)
                        .into((ImageView) rootView.findViewById(R.id.detail_poster));

                ((TextView) rootView.findViewById(R.id.detail_rating)).setText(movie.getRating());
                ((TextView) rootView.findViewById(R.id.detail_title)).setText(movie.getTitle());
                ((TextView) rootView.findViewById(R.id.detail_release)).setText("Release Date: " + movie.getRelease());
                ((TextView) rootView.findViewById(R.id.detail_synopsis)).setText(movie.getSynopsis());
            }

            return rootView;
        }
    }

}
