package com.arminsam.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
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

public class FetchTrailersTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private Context mContext;
    private long mMovieId;
    private String mMovieKey;

    public FetchTrailersTask(Context context, long movieId, String movieKey) {
        this.mContext = context;
        this.mMovieId = movieId;
        this.mMovieKey = movieKey;
    }

    private void getTrailersDataFromJson(String trailersJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TRAILERS_LIST = "results";
        final String TRAILER_SOURCE = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_SIZE = "size";

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray trailersArray = trailersJson.getJSONArray(TRAILERS_LIST);

        // Insert the new trailers information into the database
        Vector<ContentValues> cVVector = new Vector<>(trailersArray.length());
        List<Trailer> resultList = new ArrayList<>();

        for(int i = 0; i < trailersArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject movieJson = trailersArray.getJSONObject(i);
            // Create a new Movie object based on the extracted json data
            Trailer trailerObj = new Trailer();
            trailerObj.setSource(movieJson.getString(TRAILER_SOURCE));
            trailerObj.setName(movieJson.getString(TRAILER_NAME));
            trailerObj.setSize(movieJson.getLong(TRAILER_SIZE));
            resultList.add(trailerObj);

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(PopularMoviesContract.TrailersEntry.COLUMN_MOVIE_KEY, mMovieId);
            trailerValues.put(PopularMoviesContract.TrailersEntry.COLUMN_SOURCE, trailerObj.getSource());
            trailerValues.put(PopularMoviesContract.TrailersEntry.COLUMN_SIZE, trailerObj.getSize());
            trailerValues.put(PopularMoviesContract.TrailersEntry.COLUMN_NAME, trailerObj.getName());

            cVVector.add(trailerValues);
        }

        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver()
                    .bulkInsert(PopularMoviesContract.TrailersEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchTrailersTask Complete. " + inserted + " Inserted");
    }

    private String getThumbnailPath() {
        return "http://img.youtube.com/vi/" + mMovieKey + "/0.jpg";
    }

    @Override
    protected Void doInBackground(Void... params) {
        // create the url in which the app requests data from api
        String apiKey = Utility.getProperty("api.moviesdb.key", mContext);

        try {
            final String API_BASE_URL = "http://api.themoviedb.org/3/movie/" + mMovieKey + "/videos?";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            String trailersJsonStr = Utility.getResultFromUrl(url);
            try {
                getTrailersDataFromJson(trailersJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        } catch(MalformedURLException e) {
            Log.e(this.LOG_TAG, "Error building url", e);
        }

        return null;
    }

//    @Override
//    protected void onPostExecute(String result) {
//    }
}
