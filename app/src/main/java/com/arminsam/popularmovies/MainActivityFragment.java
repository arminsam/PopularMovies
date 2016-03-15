package com.arminsam.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.arminsam.popularmovies.data.PopularMoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String FLAG_MOVIE_ID = "movie_id";
    public static final String FLAG_MOVIE_KEY = "movie_key";
    private static final int MOVIES_LOADER = 0;

    private ImageAdapter mImageAdapter;
    private SharedPreferences mPrefs;
    private String mCurrentSortPref;

    // For the movies view we're showing only a small subset of the stored data.
    private static final String[] MOVIE_COLUMNS = {
            PopularMoviesContract.MoviesEntry.TABLE_NAME + "." + PopularMoviesContract.MoviesEntry._ID,
            PopularMoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            PopularMoviesContract.MoviesEntry.COLUMN_POSTER_PATH,
            PopularMoviesContract.MoviesEntry.COLUMN_FAVORITE,
            PopularMoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            PopularMoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE
    };
    // These indices are tied to MOVIE_COLUMNS. If MOVIE_COLUMNS changes, these must change.
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_POSTER_PATH = 2;
    static final int COL_FAVORITE = 3;
    static final int COL_VOTE_AVERAGE = 4;
    static final int COL_ORIGINAL_TITLE = 5;

    @Bind(R.id.movies_list) GridView gridView;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mCurrentSortPref = mPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));
        // Update local database data whenever the launches for the first time
        updateMovies();
    }

    @Override
    public void onResume() {
        super.onResume();
        String newSortPref = mPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));

        // If sorting preference has changed, reload the data
        if (!newSortPref.equals(mCurrentSortPref)) {
            mCurrentSortPref = newSortPref;
            updateMovies();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mImageAdapter = new ImageAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        gridView.setAdapter(mImageAdapter);

        // We'll call our MainActivity
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity.hasTwoPane()) {
                        // update detail fragment's layout_weight to 1
                        View mainFt = mainActivity.findViewById(R.id.movie_detail_container);
                        mainFt.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                        // In two-pane mode, show the detail view in this activity by
                        // adding or replacing the detail fragment using a
                        // fragment transaction.
                        Bundle args = new Bundle();
                        args.putLong(FLAG_MOVIE_ID, cursor.getLong(COL_ID));
                        args.putString(FLAG_MOVIE_KEY, cursor.getString(COL_MOVIE_ID));

                        DetailActivityFragment fragment = new DetailActivityFragment();
                        fragment.setArguments(args);

                        mainActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment, mainActivity.MOVIESFRAGMENT_TAG)
                                .commit();
                    } else {
                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra(FLAG_MOVIE_ID, cursor.getLong(COL_ID))
                                .putExtra(FLAG_MOVIE_KEY, cursor.getString(COL_MOVIE_ID));
                        startActivity(intent);
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Update the movies list and display them on the screen.
     */
    private void updateMovies() {
        if (!mCurrentSortPref.equals(getString(R.string.pref_sort_favorite))) {
            FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
            moviesTask.execute(mCurrentSortPref);
        }
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortBy = PopularMoviesContract.SORT_FAVORITE;
        // get current sort preference to build the uri
        if (mCurrentSortPref.equals(getString(R.string.pref_sort_popularity))) {
            sortBy = PopularMoviesContract.SORT_POPULARITY;
        }
        else if (mCurrentSortPref.equals(getString(R.string.pref_sort_rate))) {
            sortBy = PopularMoviesContract.SORT_RATE;
        }
        // build the content uri based on the sort preference
        Uri moviesUri = PopularMoviesContract.MoviesEntry.buildMoviesUri(sortBy);

        return new CursorLoader(getActivity(), moviesUri, MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Show a message for empty result - i.e. no favorite movie yet
        if (!data.moveToFirst()) {
            (Toast.makeText(getActivity(), "No movies found!", Toast.LENGTH_LONG)).show();
        }
        mImageAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageAdapter.swapCursor(null);
    }

}
