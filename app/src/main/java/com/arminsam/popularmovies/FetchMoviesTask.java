package com.arminsam.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.arminsam.popularmovies.data.PopularMoviesContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Context mContext;
    private String mSortBy;

    public FetchMoviesTask(Context context) {
        this.mContext = context;
    }

    /**
     * Take the String representing the complete movies list in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * @param moviesJsonStr
     * @return
     */
    private void getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MOVIE_LIST = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_VOTE_AVERAGE = "vote_average";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(MOVIE_LIST);

        // Insert the new movies information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());
        List<Movie> resultList = new ArrayList<>();

        int isPopular = mSortBy.equals(PopularMoviesContract.SORT_POPULARITY) ? 1 : 0;
        int isHighRated = mSortBy.equals(PopularMoviesContract.SORT_RATE) ? 1 : 0;

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

            ContentValues movieValues = new ContentValues();

            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movieObj.getMovieId());
            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE, movieObj.getOriginalTitle());
            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_OVERVIEW, movieObj.getOverview());
            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movieObj.getReleaseDate());
            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_POSTER_PATH, movieObj.getPosterPath());
            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movieObj.getVoteAverage());
            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_MOST_POPULAR, isPopular);
            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_HIGHEST_RATED, isHighRated);
            movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_FAVORITE, 0);

            cVVector.add(movieValues);
        }

        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            String sortSetting = isPopular == 1 ? PopularMoviesContract.SORT_POPULARITY : PopularMoviesContract.SORT_RATE;
            inserted = mContext.getContentResolver()
                    .bulkInsert(PopularMoviesContract.MoviesEntry.buildMoviesUri(sortSetting), cvArray);
        }

        Log.d(LOG_TAG, "FetchMoviesTask Complete. " + inserted + " Inserted");
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
    protected Void doInBackground(String... params) {
        // create the url in which the app requests data from api
        String apiKey = Utility.getProperty("api.moviesdb.key", mContext);
        mSortBy = params[0];

        try {
            final String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, mSortBy + ".desc")
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            String moviesJsonStr = Utility.getResultFromUrl(url);
            try {
                getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        } catch(MalformedURLException e) {
            Log.e(this.LOG_TAG, "Error building url", e);
        }

        return null;
    }
}
