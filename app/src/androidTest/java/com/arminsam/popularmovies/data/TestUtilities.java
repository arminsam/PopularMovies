package com.arminsam.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import com.arminsam.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createTestMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_MOVIE_ID, "127UX7");
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE, "The Revenant");
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_OVERVIEW, "A frontiersman on a fur trading expedition...");
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_POSTER_PATH, "images/M/MV5BMjU4NDExNDM1NF5BMl5BanBnXkFtZTgwMDIyMTgxNzE@._V1_SX640_SY720_.jpg");
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, "8.3");
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, "2015");
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_MOST_POPULAR, 1);
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_HIGHEST_RATED, 0);
        movieValues.put(PopularMoviesContract.MoviesEntry.COLUMN_FAVORITE, 0);

        return movieValues;
    }

    static ContentValues createTestTrailerValues(long movieRowId) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(PopularMoviesContract.TrailersEntry.COLUMN_MOVIE_KEY, movieRowId);
        trailerValues.put(PopularMoviesContract.TrailersEntry.COLUMN_NAME, "Official Trailer");
        trailerValues.put(PopularMoviesContract.TrailersEntry.COLUMN_SIZE, "120");
        trailerValues.put(PopularMoviesContract.TrailersEntry.COLUMN_SOURCE, "LoebZZ8K5N0");

        return trailerValues;
    }

    static ContentValues createTestReviewValues(long movieRowId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_MOVIE_KEY, movieRowId);
        reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_REVIEW_ID, "12318731878919276");
        reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_AUTHOR, "John Doe");
        reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_CONTENT, "One of the best movies of 2015.");
        reviewValues.put(PopularMoviesContract.ReviewsEntry.COLUMN_URL, "");

        return reviewValues;
    }

    static long insertTestMovieValues(Context context) {
        // insert our test records into the database
        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testMovieValues = TestUtilities.createTestMovieValues();

        long movieRowId, trailerRowId, reviewRowId;
        movieRowId = db.insert(PopularMoviesContract.MoviesEntry.TABLE_NAME, null, testMovieValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert test movie values", movieRowId != -1);

        // insert test trailer and review
        ContentValues testTrailerValues = TestUtilities.createTestTrailerValues(movieRowId);
        ContentValues testReviewValues = TestUtilities.createTestReviewValues(movieRowId);
        trailerRowId = db.insert(PopularMoviesContract.TrailersEntry.TABLE_NAME, null, testTrailerValues);
        reviewRowId = db.insert(PopularMoviesContract.ReviewsEntry.TABLE_NAME, null, testReviewValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert test trailer values", trailerRowId != -1);
        assertTrue("Error: Failure to insert test review values", reviewRowId != -1);

        return movieRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
