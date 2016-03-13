package com.arminsam.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(PopularMoviesDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(PopularMoviesContract.MoviesEntry.TABLE_NAME);
        tableNameHashSet.add(PopularMoviesContract.TrailersEntry.TABLE_NAME);
        tableNameHashSet.add(PopularMoviesContract.ReviewsEntry.TABLE_NAME);

        SQLiteDatabase db = new PopularMoviesDbHelper(this.mContext)
                .getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without all required tables.",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + PopularMoviesContract.MoviesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry._ID);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_MOST_POPULAR);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_HIGHEST_RATED);
        movieColumnHashSet.add(PopularMoviesContract.MoviesEntry.COLUMN_FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required movie
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns.",
                movieColumnHashSet.isEmpty());
        db.close();
    }

    public void testMoviesTable() {
        // First step: Get reference to writable database
        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createTestMovieValues();
        long movieRowId = TestUtilities.insertTestMovieValues(this.mContext);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                PopularMoviesContract.MoviesEntry.TABLE_NAME, // Table to query
                null, // all columns
                null, // columns for the "where" clause
                null, // values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row
        assertTrue("Error: No records returned from movie query", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Movie query validation failed",
                cursor, testValues);
        assertFalse("Error: More than one record returned from movie query",
                cursor.moveToNext());

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }
//
//    /*
//        Students:  Here is where you will build code to test that we can insert and query the
//        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
//        where you can use the "createWeatherValues" function.  You can
//        also make use of the validateCurrentRecord function from within TestUtilities.
//     */
//    public void testWeatherTable() {
//        // First insert the location, and then use the locationRowId to insert
//        // the weather. Make sure to cover as many failure cases as you can.
//        // First step: Get reference to writable database
//        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
//        long locationRowId = insertLocation(db, testValues);
//        assertTrue(locationRowId != -1);
//
//        // Create ContentValues of what you want to insert
//        // (you can use the createWeatherValues TestUtilities function if you wish)
//        testValues = TestUtilities.createWeatherValues(locationRowId);
//
//        // Insert ContentValues into database and get a row ID back
//        long weatherRowId;
//        weatherRowId = db.insert(PopularMoviesContract.WeatherEntry.TABLE_NAME, null, testValues);
//        assertTrue(weatherRowId != -1);
//
//        // Query the database and receive a Cursor back
//        Cursor cursor = db.query(
//                PopularMoviesContract.WeatherEntry.TABLE_NAME, // Table to query
//                null, // all columns
//                null, // columns for the "where" clause
//                null, // values for the "where" clause
//                null, // columns to group by
//                null, // columns to filter by row groups
//                null // sort order
//        );
//
//        // Move the cursor to a valid database row
//        assertTrue("Error: No records returned from weather query", cursor.moveToFirst());
//
//        // Validate data in resulting Cursor with the original ContentValues
//        // (you can use the validateCurrentRecord function in TestUtilities to validate the
//        // query if you like)
//        TestUtilities.validateCurrentRecord("Error: Weather query validation failed",
//                cursor, testValues);
//        assertFalse("Error: More than one record returned from location query",
//                cursor.moveToNext());
//
//        // Finally, close the cursor and database
//        cursor.close();
//        db.close();
//    }
//
//
//    /*
//        Students: This is a helper method for the testWeatherTable quiz. You can move your
//        code from testLocationTable to here so that you can call this code from both
//        testWeatherTable and testLocationTable.
//     */
//    public long insertLocation(SQLiteDatabase db, ContentValues testValues) {
//        // Insert ContentValues into database and get a row ID back
//        long locationRowId;
//        locationRowId = db.insert(PopularMoviesContract.LocationEntry.TABLE_NAME, null, testValues);
//
//        return locationRowId;
//    }
}
