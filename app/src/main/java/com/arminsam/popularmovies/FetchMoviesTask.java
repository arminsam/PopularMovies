package com.arminsam.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v4.app.Fragment;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MainActivityFragment mCaller;
    private Context mContext;

    public FetchMoviesTask(Context context, MainActivityFragment caller) {
        this.mContext = context;
        this.mCaller = caller;
    }

    /**
     * Take the String representing the complete movies list in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * @param moviesJsonStr
     * @return
     */
    private List<Movie> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_VOTE_AVERAGE = "vote_average";

        JSONObject forecastJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = forecastJson.getJSONArray(MOVIE_LIST);

        List<Movie> resultList = new ArrayList<>();

        for(int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject movieJson = moviesArray.getJSONObject(i);
            // Create a new Movie object based on the extracted json data
            Movie movieObj = new Movie();
            movieObj.setMovieId(movieJson.getLong(MOVIE_ID));
            movieObj.setOriginalTitle(movieJson.getString(MOVIE_ORIGINAL_TITLE));
            movieObj.setOverview(movieJson.getString(MOVIE_OVERVIEW));
            movieObj.setReleaseDate(movieJson.getString(MOVIE_RELEASE_DATE));
            movieObj.setPosterPath(this.getPosterThumbPath(movieJson.getString(MOVIE_POSTER_PATH)));
            movieObj.setVoteAverage(movieJson.getString(MOVIE_VOTE_AVERAGE));
            resultList.add(movieObj);
        }

        return resultList;
    }

    private String getPosterThumbPath(String imageHash) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "/w185";

        return BASE_URL + IMAGE_SIZE + imageHash;
    }

    /**
     * Build and connect to API endpoint url, and return json string result.
     *
     * @param params
     * @return
     */
    @Override
    protected List<Movie> doInBackground(String... params) {
        // create the url in which the app requests data from api
        String apiKey = Utility.getProperty("api.moviesdb.key", mContext);

        try {
            final String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0] + ".desc")
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            String moviesJsonStr = Utility.getResultFromUrl(url);
            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        } catch(MalformedURLException e) {
            Log.e(this.LOG_TAG, "Error building url", e);
        }

        return null;
    }

    /**
     * Update the UI when doInBackground method returns a result
     *
     * @param movies
     */
    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (movies != null) {
            mCaller.setMovies((ArrayList) movies);
            mCaller.getImageAdapter().clear();
            mCaller.getImageAdapter().addAll(movies);
            mCaller.getImageAdapter().notifyDataSetChanged();
        }
    }
}