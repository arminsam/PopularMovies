package com.arminsam.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import com.arminsam.popularmovies.Movie;

/**
 * Defines table and column names for the popular_movies database.
 */
public class PopularMoviesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.arminsam.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.arminsam.popularmovies/movies/ is a valid path for
    // looking at movies data. content://com.arminsam.popularmovies/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_REVIEW = "review";

    // Different sort criteria
    public static final String SORT_POPULARITY = "popularity";
    public static final String SORT_RATE = "vote_average";
    public static final String SORT_FAVORITE = "favorite";

    /**
     * Inner class that defines the table contents of the movies table.
     */
    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME               = "movies";
        public static final String COLUMN_ID                = "_id";
        public static final String COLUMN_MOVIE_ID          = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE    = "original_title";
        public static final String COLUMN_OVERVIEW          = "overview";
        public static final String COLUMN_RELEASE_DATE      = "release_date";
        public static final String COLUMN_POSTER_PATH       = "poster_path";
        public static final String COLUMN_VOTE_AVERAGE      = "vote_average";
        // next three columns are boolean fields which will be used for sorting the movies
        public static final String COLUMN_MOST_POPULAR      = "most_popular";
        public static final String COLUMN_HIGHEST_RATED     = "highest_rated";
        public static final String COLUMN_FAVORITE          = "favorite";

        // uri to get the data for a single movie
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMoviesUri(String sortSetting) {
            return CONTENT_URI.buildUpon().appendPath(sortSetting).build();
        }
        public static String getSortSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**
     * Inner class that defines the table contents of the trailers table.
     */
    public static final class TrailersEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SIZE = "size";

        public static Uri buildTrailerUri(long id) {
            return MoviesEntry.buildMovieUri(id).buildUpon().appendPath(PATH_TRAILERS).build();
        }
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /**
     * Inner class that defines the table contents of the reviews table.
     */
    public static final class ReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static Uri buildReviewUri(long id) {
            return MoviesEntry.buildMovieUri(id).buildUpon().appendPath(PATH_REVIEWS).build();
        }
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
