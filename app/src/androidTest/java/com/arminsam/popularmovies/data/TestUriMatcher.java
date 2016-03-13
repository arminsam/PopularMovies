package com.arminsam.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    // content://com.arminsam.popularmovies/movies/popularity
    private static final Uri TEST_MOVIES_DIR = PopularMoviesContract.MoviesEntry.CONTENT_URI;
    private static final Uri TEST_MOST_POPULAR_MOVIES_DIR = PopularMoviesContract.MoviesEntry
            .buildMoviesUri(PopularMoviesContract.SORT_POPULARITY);
    private static final Uri TEST_HIGHEST_RATED_MOVIES_DIR = PopularMoviesContract.MoviesEntry
            .buildMoviesUri(PopularMoviesContract.SORT_RATE);
    private static final Uri TEST_FAVORITE_MOVIES_DIR = PopularMoviesContract.MoviesEntry
            .buildMoviesUri(PopularMoviesContract.SORT_FAVORITE);
    private static final Uri TEST_MOVIE_DIR = PopularMoviesContract.MoviesEntry
            .buildMovieUri(1);

    public void testUriMatcher() {
        UriMatcher testMatcher = PopularMoviesProvider.buildUriMatcher();

        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIES_DIR), PopularMoviesProvider.MOVIES);
        assertEquals("Error: The MOST POPULAR MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_MOST_POPULAR_MOVIES_DIR), PopularMoviesProvider.MOVIES_MOST_POPULAR);
        assertEquals("Error: The HIGHEST RATED MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_HIGHEST_RATED_MOVIES_DIR), PopularMoviesProvider.MOVIES_HIGHEST_RATED);
        assertEquals("Error: The MOST POPULAR MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_FAVORITE_MOVIES_DIR), PopularMoviesProvider.MOVIES_FAVORITE);
        assertEquals("Error: The MOVIE DETAIL URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), PopularMoviesProvider.SINGLE_MOVIE);
    }
}
