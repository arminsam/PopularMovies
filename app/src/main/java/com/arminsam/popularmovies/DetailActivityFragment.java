package com.arminsam.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        // Decode movie's detail from bundled data received over intent
        if (intent != null && intent.hasExtra(getString(R.string.EXTRA_MOVIE))) {
            Bundle bundle = intent.getExtras();
            Movie movie = bundle.getParcelable(getString(R.string.EXTRA_MOVIE));
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getOriginalTitle());
            ((TextView) rootView.findViewById(R.id.movie_year)).setText(movie.getReleaseYear());
            ((TextView) rootView.findViewById(R.id.movie_rate)).setText(movie.getVoteAverage());
            ((TextView) rootView.findViewById(R.id.movie_overview)).setText(movie.getOverview());
            Picasso.with(getActivity()).load(movie.getPosterPath())
                    .into((ImageView) rootView.findViewById(R.id.movie_poster));
        }

        return rootView;
    }
}
