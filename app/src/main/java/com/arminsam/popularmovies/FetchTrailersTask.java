package com.arminsam.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchTrailersTask extends AsyncTask<Movie, Void, List<Trailer>> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private DetailActivityFragment mCaller;
    private Context mContext;

    public FetchTrailersTask(Context context, DetailActivityFragment caller) {
        this.mContext = context;
        this.mCaller = caller;
    }

    /**
     * Take the String representing the complete movie trailer list in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * @param trailersJsonStr
     * @return
     */
    private List<Trailer> getTrailersDataFromJson(String trailersJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TRAILER_LIST = "youtube";
        final String TRAILER_ID = "id";
        final String TRAILER_SOURCE = "source";
        final String TRAILER_NAME = "name";
        final String TRAILER_SIZE = "size";

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray trailersArray = trailersJson.getJSONArray(TRAILER_LIST);

        List<Trailer> resultList = new ArrayList<>();

        for(int i = 0; i < trailersArray.length(); i++) {
            // Get the JSON object representing the each trailer
            JSONObject trailerJson = trailersArray.getJSONObject(i);
            // Create a new Trailer object based on the extracted json data
            Trailer trailerObj = new Trailer();
            trailerObj.setSource(trailerJson.getString(TRAILER_SOURCE));
            trailerObj.setName(trailerJson.getString(TRAILER_NAME));
            trailerObj.setSize(trailerJson.getString(TRAILER_SIZE));
            resultList.add(trailerObj);
        }

        return resultList;
    }

    /**
     * Build and connect to API endpoint url, and return json string result.
     *
     * @param params
     * @return
     */
    @Override
    protected List<Trailer> doInBackground(Movie... params) {
        // create the url in which the app requests data from api
        String apiKey = Utility.getProperty("api.moviesdb.key", mContext);

        try {
            final String API_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0].getMovieId() + "/trailers?";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            String trailersJsonStr = Utility.getResultFromUrl(url);
            try {
                return getTrailersDataFromJson(trailersJsonStr);
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
     * @param trailers
     */
    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if (trailers != null) {
            mCaller.setTrailers((ArrayList) trailers);
            mCaller.addItemsToLayout(DetailActivityFragment.TRAILER_ITEM);
        }
    }
}