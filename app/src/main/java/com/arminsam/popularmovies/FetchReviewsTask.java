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

public class FetchReviewsTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private DetailActivityFragment mCaller;
    private Context mContext;
    private long mMovieId;
    private String mMovieKey;
    private int mInserted;

    public FetchReviewsTask(DetailActivityFragment caller, Context context, long movieId, String movieKey) {
        this.mCaller = caller;
        this.mContext = context;
        this.mMovieId = movieId;
        this.mMovieKey = movieKey;
        this.mInserted = 0;
    }

    private void getReviewsDataFromJson(String reviewsJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String REVIEWS_LIST = "results";
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        JSONObject moviesJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = moviesJson.getJSONArray(REVIEWS_LIST);

        // Insert the new reviews information into the database
        Vector<ContentValues> cVVector = new Vector<>(reviewsArray.length());
        List<Review> resultList = new ArrayList<>();

        for(int i = 0; i < reviewsArray.length(); i++) {
            // Get the JSON object representing the review
            JSONObject reviewJson = reviewsArray.getJSONObject(i);
            // Create a new Review object based on the extracted json data
            Review reviewObj = new Review();
            reviewObj.setReviewId(reviewJson.getString(REVIEW_ID));
            reviewObj.setAuthor(reviewJson.getString(REVIEW_AUTHOR));
            reviewObj.setContent(reviewJson.getString(REVIEW_CONTENT));
            reviewObj.setUrl(reviewJson.getString(REVIEW_URL));
            resultList.add(reviewObj);

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_MOVIE_KEY, mMovieId);
            reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_REVIEW_ID, reviewObj.getReviewId());
            reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_AUTHOR, reviewObj.getAuthor());
            reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_CONTENT, reviewObj.getContent());
            reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_URL, reviewObj.getUrl());

            cVVector.add(reviewValues);
        }

        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mInserted = mContext.getContentResolver()
                    .bulkInsert(PopularMoviesContract.ReviewsEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchReviewsTask Complete. " + mInserted + " Inserted");
    }

    /**
     * Build and connect to API endpoint url, and return json string result.
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        // create the url in which the app requests data from api
        String apiKey = Utility.getProperty("api.moviesdb.key", mContext);

        try {
            final String API_BASE_URL = "http://api.themoviedb.org/3/movie/" + mMovieKey + "/reviews?";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
            Log.v(LOG_TAG, builtUri.toString());
            URL url = new URL(builtUri.toString());
            String reviewsJsonStr = Utility.getResultFromUrl(url);
            try {
                getReviewsDataFromJson(reviewsJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        } catch(MalformedURLException e) {
            Log.e(this.LOG_TAG, "Error building url", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        mCaller.getLoaderManager().initLoader(DetailActivityFragment.REVIEWS_LOADER, null, mCaller);
    }
}
