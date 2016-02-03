package com.arminsam.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PopularMoviesProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopularMoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_MOST_POPULAR = 101;
    static final int MOVIES_HIGHEST_RATED = 102;
    static final int MOVIES_FAVORITE = 103;
    static final int MOVIE_WITH_TRAILERS_AND_REVIEWS = 104;
    static final int TRAILERS = 105;
    static final int REVIEWS = 106;

    private static final SQLiteQueryBuilder sMoviesQueryBuilder;
    private static final SQLiteQueryBuilder sMovieWithTrailersAndReviewsQueryBuilder;

    static {
        sMoviesQueryBuilder = new SQLiteQueryBuilder();

        sMoviesQueryBuilder.setTables(PopularMoviesContract.MoviesEntry.TABLE_NAME);
    }

    static {
        sMovieWithTrailersAndReviewsQueryBuilder = new SQLiteQueryBuilder();

        sMovieWithTrailersAndReviewsQueryBuilder.setTables(
                PopularMoviesContract.MoviesEntry.TABLE_NAME +
                " INNER JOIN " + PopularMoviesContract.TrailersEntry.TABLE_NAME +
                " ON " + PopularMoviesContract.MoviesEntry.TABLE_NAME +
                "." + PopularMoviesContract.MoviesEntry._ID +
                " = " + PopularMoviesContract.TrailersEntry.TABLE_NAME +
                "." + PopularMoviesContract.TrailersEntry.COLUMN_MOVIE_KEY +
                " INNER JOIN " +PopularMoviesContract.ReviewsEntry.TABLE_NAME +
                " ON " + PopularMoviesContract.MoviesEntry.TABLE_NAME +
                "." + PopularMoviesContract.MoviesEntry._ID +
                " = " + PopularMoviesContract.ReviewsEntry.TABLE_NAME +
                "." + PopularMoviesContract.ReviewsEntry.COLUMN_MOVIE_KEY);
    }

    private static final String sMoviesSortByPopularitySelection =
            PopularMoviesContract.MoviesEntry.TABLE_NAME +
            "." + PopularMoviesContract.MoviesEntry.COLUMN_MOST_POPULAR + " = 1 ";

    private static final String sMoviesSortByRateSelection =
            PopularMoviesContract.MoviesEntry.TABLE_NAME +
            "." + PopularMoviesContract.MoviesEntry.COLUMN_HIGHEST_RATED + " = 1 ";

    private static final String sMoviesSortByFavoriteSelection =
            PopularMoviesContract.MoviesEntry.TABLE_NAME +
            "." + PopularMoviesContract.MoviesEntry.COLUMN_FAVORITE + " = 1 ";

    private static final String sMovieWithTrailersAndReviewsSelection =
            PopularMoviesContract.MoviesEntry.TABLE_NAME +
                    "." + PopularMoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMoviesBySortSetting(Uri uri, String[] projection, String sortOrder) {
        String sortSetting = PopularMoviesContract.MoviesEntry.getSortSettingFromUri(uri);
        String selection;

        switch (sortSetting) {
            case PopularMoviesContract.SORT_POPULARITY: {
                selection = sMoviesSortByPopularitySelection;
                break;
            }
            case PopularMoviesContract.SORT_RATE: {
                selection = sMoviesSortByRateSelection;
                break;
            }
            case PopularMoviesContract.SORT_FAVORITE: {
                selection = sMoviesSortByFavoriteSelection;
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported sort option: " + sortSetting);
            }
        }

        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieWithTrailersAndReviews(Uri uri, String[] projection, String sortOrder) {
        String movieId = PopularMoviesContract.MoviesEntry.getMovieIdFromUri(uri);

        return sMovieWithTrailersAndReviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieWithTrailersAndReviewsSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMoviesContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // PopularMoviesContract to help define the types to the UriMatcher.
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIES + "/" + PopularMoviesContract.SORT_POPULARITY,
                MOVIES_MOST_POPULAR);
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIES + "/" + PopularMoviesContract.SORT_RATE,
                MOVIES_HIGHEST_RATED);
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIES + "/" + PopularMoviesContract.SORT_FAVORITE,
                MOVIES_FAVORITE);
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_TRAILERS_AND_REVIEWS);
        matcher.addURI(authority, PopularMoviesContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, PopularMoviesContract.PATH_REVIEWS, REVIEWS);

        // 3) Return the new matcher!
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES_MOST_POPULAR:
            case MOVIES_HIGHEST_RATED:
            case MOVIES_FAVORITE:
                return PopularMoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_TRAILERS_AND_REVIEWS:
                return PopularMoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // e.g. "movies/highest_rated"
            case MOVIES_MOST_POPULAR:
            case MOVIES_HIGHEST_RATED:
            case MOVIES_FAVORITE: {
                retCursor = getMoviesBySortSetting(uri, projection, sortOrder);
                break;
            }
            // e.g. "movie/12"
            case MOVIE_WITH_TRAILERS_AND_REVIEWS: {
                retCursor = getMovieWithTrailersAndReviews(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES_HIGHEST_RATED:
            case MOVIES_MOST_POPULAR: {
                long _id = db.insert(PopularMoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PopularMoviesContract.MoviesEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_WITH_TRAILERS_AND_REVIEWS: {
                String movieId = PopularMoviesContract.MoviesEntry.getMovieIdFromUri(uri);
                long _id1 = db.insert(PopularMoviesContract.TrailersEntry.TABLE_NAME, null, values);
                long _id2 = db.insert(PopularMoviesContract.ReviewsEntry.TABLE_NAME, null, values);
                if (_id1 > 0 && _id2 > 0)
                    returnUri = PopularMoviesContract.MoviesEntry.buildMovieUri(Long.parseLong(movieId));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIES: {
                rowsDeleted = db.delete(PopularMoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRAILERS: {
                rowsDeleted = db.delete(PopularMoviesContract.TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEWS: {
                rowsDeleted = db.delete(PopularMoviesContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MOVIE_WITH_TRAILERS_AND_REVIEWS: {
                rowsDeleted = db.delete(PopularMoviesContract.TrailersEntry.TABLE_NAME, selection, selectionArgs)
                        + db.delete(PopularMoviesContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
            case MOVIES_HIGHEST_RATED:
            case MOVIES_MOST_POPULAR: {
                rowsUpdated = db.update(PopularMoviesContract.MoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES_HIGHEST_RATED:
            case MOVIES_MOST_POPULAR: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PopularMoviesContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
