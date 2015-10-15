package com.arminsam.popularmovies;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    /**
     * Get the value of the given property key, stored in local.properties file in root project directory
     *
     * @param key
     * @return
     */
    public static String getProperty(String key, Context context) {
        Properties properties = new Properties();

        try {
            properties.load(context.getResources().getAssets().open("config.properties"));
            return properties.getProperty(key);
        } catch (Exception e) {
            Log.e(Utility.LOG_TAG, "Unable to read from config.properties file.", e);
        }

        return null;
    }

    /**
     * Connect to the given url and return the response in string format.
     *
     * @param url
     * @return
     */
    public static String getResultFromUrl(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // raw response as string
        String resultStr = null;

        try {
            // create a connection and send request to the movie db api
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // read the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // nothing to do
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            resultStr = buffer.toString();
        } catch(IOException e) {
            Log.e(Utility.LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting to parse it.
            resultStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(Utility.LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return resultStr;
    }
}