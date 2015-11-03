package com.arminsam.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ImageAdapter mImageAdapter;
    private ArrayList<Movie> mMovies;
    private SharedPreferences mPrefs;
    private String mCurrentSortPref;

    @Bind(R.id.movies_list) GridView gridView;

    public MainActivityFragment() {
    }

    public ImageAdapter getImageAdapter() {
        return this.mImageAdapter;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.mMovies = movies;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mMovies = new ArrayList<>();
            updateMovies();
        }
        else {
            mMovies = savedInstanceState.getParcelableArrayList("movies");
        }
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mCurrentSortPref = mPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mImageAdapter = new ImageAdapter(getActivity(), mMovies);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        gridView.setAdapter(mImageAdapter);

        return rootView;
    }

    @OnItemClick(R.id.movies_list)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Movie movie = mImageAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class)
                .putExtra(getString(R.string.EXTRA_MOVIE), movie);
        startActivity(intent);
    }

    /**
     * Called when the Fragment is visible to the user.
     */
    @Override
    public void onStart() {
        super.onStart();
        String newPref = mPrefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));

        // If sorting preference has changed, reload the data
        if (mCurrentSortPref != newPref) {
            mCurrentSortPref = newPref;
            updateMovies();
        }
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
        outState.putParcelableArrayList("movies", mMovies);
        super.onSaveInstanceState(outState);
    }

    /**
     * Update the movies list and display them on the screen.
     */
    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));

        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity(), this);
        moviesTask.execute(sortBy);
    }

}
