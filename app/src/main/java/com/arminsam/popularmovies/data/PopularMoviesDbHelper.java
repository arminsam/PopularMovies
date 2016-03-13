package com.arminsam.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.arminsam.popularmovies.data.PopularMoviesContract.MoviesEntry;
import com.arminsam.popularmovies.data.PopularMoviesContract.TrailersEntry;
import com.arminsam.popularmovies.data.PopularMoviesContract.ReviewsEntry;

/**
 * Manages a local database for movies data.
 */
public class PopularMoviesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "popular_movies.db";

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create movies table
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                MoviesEntry.COLUMN_MOST_POPULAR + " INTEGER, " +
                MoviesEntry.COLUMN_HIGHEST_RATED + " INTEGER, " +
                MoviesEntry.COLUMN_FAVORITE + " INTEGER, " +
                "UNIQUE (" + MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";
        // Create trailers table
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + TrailersEntry.TABLE_NAME + " (" +
                TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailersEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                TrailersEntry.COLUMN_SOURCE + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_NAME + " TEXT, " +
                TrailersEntry.COLUMN_SIZE + " TEXT, " +
                "UNIQUE (" + TrailersEntry.COLUMN_SOURCE + ") ON CONFLICT IGNORE, " +
                "FOREIGN KEY (" + TrailersEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + " (" + MoviesEntry._ID + ")" +
                ")";
        // Create reviews table
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewsEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_CONTENT + " TEXT, " +
                ReviewsEntry.COLUMN_URL + " TEXT, " +
                "UNIQUE (" + ReviewsEntry.COLUMN_REVIEW_ID + ") ON CONFLICT IGNORE, " +
                "FOREIGN KEY (" + ReviewsEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + " (" + MoviesEntry._ID + ")" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
