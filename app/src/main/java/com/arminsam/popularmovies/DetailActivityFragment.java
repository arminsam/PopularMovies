package com.arminsam.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.arminsam.popularmovies.data.PopularMoviesContract;
import com.squareup.picasso.Picasso;
import android.support.v7.widget.ShareActionProvider;

import java.util.Map;

/**
 * A placeholder fragment containing movie details.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesApp";

    private String mShareString;
    private Long mMovieId;
    private String mMovieKey;
    @Bind(R.id.movie_title) TextView movieTitle;
    @Bind(R.id.movie_year) TextView movieYear;
    @Bind(R.id.movie_rate) TextView movieRate;
    @Bind(R.id.movie_overview) TextView movieOverview;
    @Bind(R.id.movie_poster) ImageView moviePoster;
    @Bind(R.id.no_trailers_message) TextView noTrailersMessage;
    @Bind(R.id.no_reviews_message) TextView noReviewsMessage;
    @Bind(R.id.trailers_list) LinearLayout trailersListView;
    @Bind(R.id.reviews_list) LinearLayout reviewsListView;

    public static final int DETAIL_LOADER = 0;
    public static final int TRAILERS_LOADER = 1;
    public static final int REVIEWS_LOADER = 2;

    private static final String[] MOVIE_COLUMNS = {
            PopularMoviesContract.MoviesEntry.TABLE_NAME + "." + PopularMoviesContract.MoviesEntry._ID,
            PopularMoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            PopularMoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE,
            PopularMoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            PopularMoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            PopularMoviesContract.MoviesEntry.COLUMN_POSTER_PATH,
            PopularMoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            PopularMoviesContract.MoviesEntry.COLUMN_FAVORITE
    };

    private static final String[] TRAILERS_COLUMNS = {
            PopularMoviesContract.TrailersEntry.TABLE_NAME + "." + PopularMoviesContract.TrailersEntry._ID,
            PopularMoviesContract.TrailersEntry.COLUMN_MOVIE_KEY,
            PopularMoviesContract.TrailersEntry.COLUMN_NAME,
            PopularMoviesContract.TrailersEntry.COLUMN_SOURCE
    };

    private static final String[] REVIEWS_COLUMNS = {
            PopularMoviesContract.ReviewsEntry.TABLE_NAME + "." + PopularMoviesContract.ReviewsEntry._ID,
            PopularMoviesContract.ReviewsEntry.COLUMN_AUTHOR,
            PopularMoviesContract.ReviewsEntry.COLUMN_CONTENT
    };

    private static final int COL_ID = 0;
    private static final int COL_MOVIE_ID = 1;
    private static final int COL_ORIGINAL_TITLE = 2;
    private static final int COL_RELEASE_DATE = 3;
    private static final int COL_OVERVIEW = 4;
    private static final int COL_POSTER_PATH = 5;
    private static final int COL_VOTE_AVERAGE = 6;
    private static final int COL_FAVORITE = 7;
    private static final int COL_MOVIE_TRAILER_NAME = 2;
    private static final int COL_MOVIE_TRAILER_KEY = 3;
    private static final int COL_MOVIE_REVIEW_AUTHOR = 1;
    private static final int COL_MOVIE_REVIEW_CONTENT = 2;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieId = arguments.getLong(MainActivityFragment.FLAG_MOVIE_ID);
            mMovieKey = arguments.getString(MainActivityFragment.FLAG_MOVIE_KEY);

        } else {
            Intent intent = getActivity().getIntent();
            mMovieId = intent.getExtras().getLong(MainActivityFragment.FLAG_MOVIE_ID);
            mMovieKey = intent.getExtras().getString(MainActivityFragment.FLAG_MOVIE_KEY);
        }
        updateTrailers();
        updateReviews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);
        // Retrieve the share menu item.
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareString != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareString + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateTrailers() {
        FetchTrailersTask trailersTask = new FetchTrailersTask(this, getActivity(), mMovieId, mMovieKey);
        trailersTask.execute();
    }

    private void updateReviews() {
        FetchReviewsTask reviewsTask = new FetchReviewsTask(this, getActivity(), mMovieId, mMovieKey);
        reviewsTask.execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = null;
        String[] columns = {};

        if (id == DETAIL_LOADER) {
            uri = PopularMoviesContract.MoviesEntry.buildMovieUri(mMovieId);
            columns = MOVIE_COLUMNS;
        }
        else if (id == TRAILERS_LOADER) {
            uri = PopularMoviesContract.TrailersEntry.buildTrailerUri(mMovieId);
            columns = TRAILERS_COLUMNS;
        }
        else if (id == REVIEWS_LOADER) {
            uri = PopularMoviesContract.ReviewsEntry.buildReviewUri(mMovieId);
            columns = REVIEWS_COLUMNS;
        }

        return new CursorLoader(getActivity(), uri, columns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == DETAIL_LOADER) {
            updateMovieDetailsFromLoader(data);
        }
        else if (loader.getId() == TRAILERS_LOADER) {
            updateTrailersListFromLoader(data);
        }
        else if (loader.getId() == REVIEWS_LOADER) {
            updateReviewsListFromLoader(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void updateMovieDetailsFromLoader(Cursor data) {
        if (!data.moveToFirst()) {
            (Toast.makeText(getActivity(), "Movie did not load properly.", Toast.LENGTH_LONG)).show();
            return;
        }
        movieTitle.setText(data.getString(COL_ORIGINAL_TITLE));
        movieYear.setText(data.getString(COL_RELEASE_DATE).substring(0, 4));
        movieRate.setText(data.getString(COL_VOTE_AVERAGE));
        movieOverview.setText(data.getString(COL_OVERVIEW));
        Picasso.with(getActivity()).load(data.getString(COL_POSTER_PATH))
                .into(moviePoster);
        mShareString = String.format("%s (%s) - %s ",
                data.getString(COL_ORIGINAL_TITLE),
                data.getString(COL_RELEASE_DATE).substring(0, 4),
                data.getString(COL_VOTE_AVERAGE));
    }

    private void updateTrailersListFromLoader(Cursor data) {
        if (!data.moveToFirst()) {
            noTrailersMessage.setVisibility(View.VISIBLE);
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        do {
            final String movieKey = data.getString(COL_MOVIE_TRAILER_KEY);
            View layout = inflater.inflate(R.layout.trailer_list_item, null);
            String thumbnailPath = "http://img.youtube.com/vi/" + movieKey + "/0.jpg";
            TextView trailerName = (TextView) layout.findViewById(R.id.trailer_list_item_name);
            ImageView trailerThumbnail = (ImageView) layout.findViewById(R.id.trailer_list_item_icon);
            Picasso.with(getActivity()).load(thumbnailPath).into(trailerThumbnail);
            trailerName.setText(data.getString(COL_MOVIE_TRAILER_NAME));
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://www.youtube.com/watch?v=" + movieKey;
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });
            trailersListView.addView(layout);
        } while (data.moveToNext());
    }

    private void updateReviewsListFromLoader(Cursor data) {
        if (!data.moveToFirst()) {
            noReviewsMessage.setVisibility(View.VISIBLE);
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        do {
            View layout = inflater.inflate(R.layout.review_list_item, null);
            TextView reviewAuthor = (TextView) layout.findViewById(R.id.review_list_item_author);
            TextView reviewContent = (TextView) layout.findViewById(R.id.review_list_item_content);
            reviewAuthor.setText(data.getString(COL_MOVIE_REVIEW_AUTHOR));
            reviewContent.setText(data.getString(COL_MOVIE_REVIEW_CONTENT));
            reviewsListView.addView(layout);
        } while (data.moveToNext());
    }
}
