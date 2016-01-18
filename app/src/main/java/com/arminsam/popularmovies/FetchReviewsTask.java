package com.arminsam.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchReviewsTask extends AsyncTask<Movie, Void, List<Review>> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private DetailActivityFragment mCaller;
    private Context mContext;

    public FetchReviewsTask(Context context, DetailActivityFragment caller) {
        this.mContext = context;
        this.mCaller = caller;
    }

    /**
     * Take the String representing the complete movie review list in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * @param reviewsJsonStr
     * @return
     */
    private List<Review> getReviewsDataFromJson(String reviewsJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String REVIEW_LIST = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(REVIEW_LIST);

        List<Review> resultList = new ArrayList<>();

        for(int i = 0; i < reviewsArray.length(); i++) {
            // Get the JSON object representing the each review
            JSONObject reviewJson = reviewsArray.getJSONObject(i);
            // Create a new Review object based on the extracted json data
            Review reviewObj = new Review();
            reviewObj.setReviewId(reviewJson.getString(REVIEW_ID));
            reviewObj.setAuthor(reviewJson.getString(REVIEW_AUTHOR));
            reviewObj.setContent(reviewJson.getString(REVIEW_CONTENT));
            reviewObj.setUrl(reviewJson.getString(REVIEW_URL));
            resultList.add(reviewObj);
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
    protected List<Review> doInBackground(Movie... params) {
        // create the url in which the app requests data from api
        String apiKey = Utility.getProperty("api.moviesdb.key", mContext);

        try {
            final String API_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0].getMovieId() + "/reviews?";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            String reviewsJsonStr = Utility.getResultFromUrl(url);
            try {
                return getReviewsDataFromJson(reviewsJsonStr);
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
     * @param reviews
     */
    @Override
    protected void onPostExecute(List<Review> reviews) {
        if (reviews != null) {
            mCaller.setReviews((ArrayList) reviews);
            mCaller.addItemsToLayout(DetailActivityFragment.REVIEW_ITEM);
        }
    }
}
