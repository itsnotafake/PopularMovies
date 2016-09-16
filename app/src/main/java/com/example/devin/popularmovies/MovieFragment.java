package com.example.devin.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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
import java.util.ArrayList;

public class MovieFragment extends Fragment {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static ArrayAdapter<Movie> mMovie_Adapter;

    //REMOVE THIS STRING WHEN UPLOADING TO PUBLIC REPOSITORIES

    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovie_Adapter = new MovieAdapter(getActivity(), R.layout.grid_item_movie, new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.main_gridview);
        gridView.setAdapter(mMovie_Adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                Movie m = mMovie_Adapter.getItem(i);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("MyMovie", m);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovies();
    }

    public void updateMovies(){
        SharedPreferences sharedP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        new FetchMovieTask().execute(sharedP.getString(
                getString(R.string.pref_sortBy_key),
                getString(R.string.pref_sortBy_default)));
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        //Remove API_KEY when uploading to a public repository
        private final String API_KEY = getString(R.string.API_KEY);
        private final String LOG_TAG_ALPHA = FetchMovieTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = null;

            String base_URL = getString(R.string.base_URL);
            String APPID = "?api_key=" + API_KEY;

            //category will be either top_rated or popular
            String sortBy = params[0];
            //example =  http://api.themoviedb.org/3/movie/popular?api_key=key...
            String url_string = base_URL + sortBy + APPID;

            try {
                URL url = new URL(url_string);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG_ALPHA, "Error ", e);
            } catch (IOException e) {
                Log.e(LOG_TAG_ALPHA, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG_ALPHA, "Error closing stream ", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            }catch (JSONException e) {
                Log.e(LOG_TAG_ALPHA, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Movie[] movies){
            if (movies != null){
                mMovie_Adapter.clear();
                for(Movie m : movies){
                    mMovie_Adapter.add(m);
                }
            }
        }

        public Movie[] getMovieDataFromJson(String json) throws JSONException{
            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_SYNOPSIS = "overview";
            final String TMDB_RELEASE = "release_date";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_RATING = "vote_average";

            JSONObject movieJson = new JSONObject(json);
            JSONArray movieJsonArray = movieJson.getJSONArray(TMDB_RESULTS);
            Movie[] movieArray = new Movie[movieJsonArray.length()];

            for(int i = 0; i < movieArray.length; i++){
                JSONObject movie = movieJsonArray.getJSONObject(i);

                String title = movie.getString(TMDB_TITLE);
                String synopsis = movie.getString(TMDB_SYNOPSIS);
                String release = movie.getString(TMDB_RELEASE);
                String poster = movie.getString(TMDB_POSTER);
                Log.v(LOG_TAG_ALPHA, Long.toString(movie.getLong(TMDB_RATING)));
                String rating = Long.toString(movie.getLong(TMDB_RATING));

                movieArray[i] = new Movie(title, synopsis, release, poster, rating);
            }
            return movieArray;
        }
    }
}
