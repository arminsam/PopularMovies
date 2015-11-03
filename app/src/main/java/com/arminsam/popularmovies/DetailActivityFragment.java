package com.arminsam.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    @Bind(R.id.movie_title) TextView movieTitle;
    @Bind(R.id.movie_year) TextView movieYear;
    @Bind(R.id.movie_rate) TextView movieRate;
    @Bind(R.id.movie_overview) TextView movieOverview;
    @Bind(R.id.movie_poster) ImageView moviePoster;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        ButterKnife.bind(this, rootView);

        // Decode movie's detail from bundled data received over intent
        if (intent != null && intent.hasExtra(getString(R.string.EXTRA_MOVIE))) {
            Bundle bundle = intent.getExtras();
            Movie movie = bundle.getParcelable(getString(R.string.EXTRA_MOVIE));
            movieTitle.setText(movie.getOriginalTitle());
            movieYear.setText(movie.getReleaseYear());
            movieRate.setText(movie.getVoteAverage());
            movieOverview.setText(movie.getOverview());
            Picasso.with(getActivity()).load(movie.getPosterPath())
                    .into(moviePoster);
        }

        return rootView;
    }
}
