package com.arminsam.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute("popularity");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

        /**
         * Build and connect to API endpoint url, and return json string result.
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            // create the url in which the app requests data from api
            String apiKey = Utility.getProperty("api.moviesdb.key", getActivity());

            try {
                final String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";
                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0] + ".desc")
                        .appendQueryParameter(API_KEY_PARAM, apiKey)
                        .build();
                URL url = new URL(builtUri.toString());
                return Utility.getResultFromUrl(url);
            } catch(MalformedURLException e) {
                Log.e(this.LOG_TAG, "Error building url", e);
            }

            return null;
        }
    }
}
