package com.arminsam.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public static final int TRAILER_ITEM = 1;
    public static final int REVIEW_ITEM = 2;

    @Bind(R.id.movie_title) TextView movieTitle;
    @Bind(R.id.movie_year) TextView movieYear;
    @Bind(R.id.movie_rate) TextView movieRate;
    @Bind(R.id.movie_overview) TextView movieOverview;
    @Bind(R.id.movie_poster) ImageView moviePoster;
    @Bind(R.id.trailers_list) LinearLayout trailersListView;
    @Bind(R.id.reviews_list) LinearLayout reviewsListView;
    private Movie mMovie;
    private ArrayList<Trailer> mTrailers;
    private ArrayList<Review> mReviews;

    public DetailActivityFragment() {
        mTrailers = new ArrayList<>();
        mReviews = new ArrayList<>();
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.mTrailers = trailers;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.mReviews = reviews;
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
            mMovie = bundle.getParcelable(getString(R.string.EXTRA_MOVIE));
            movieTitle.setText(mMovie.getOriginalTitle());
            movieYear.setText(mMovie.getReleaseYear());
            movieRate.setText(mMovie.getVoteAverage());
            movieOverview.setText(mMovie.getOverview());
            Picasso.with(getActivity()).load(mMovie.getPosterPath())
                    .into(moviePoster);
        }

        updateTrailers();
        updateReviews();

        return rootView;
    }

    /**
     * Update the trailers list and display them on the screen.
     */
    private void updateTrailers() {
        FetchTrailersTask trailersTask = new FetchTrailersTask(getActivity(), this);
        trailersTask.execute(mMovie);
    }

    /**
     * Update the reviews list and display them on the screen.
     */
    private void updateReviews() {
        FetchReviewsTask reviewsTask = new FetchReviewsTask(getActivity(), this);
        reviewsTask.execute(mMovie);
    }

    /**
     * Add trailers and reviews to their respective layouts.
     */
    public void addItemsToLayout(int itemType) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (itemType == TRAILER_ITEM) {
            for (int i = 0; i < mTrailers.size(); i++) {
                View view = inflater.inflate(R.layout.trailer_list_item, trailersListView, false);
                ImageView iconView = (ImageView) view.findViewById(R.id.trailer_list_item_icon);
                TextView nameView = (TextView) view.findViewById(R.id.trailer_list_item_name);
                iconView.setImageResource(R.drawable.ic_play);
                final Trailer trailer = mTrailers.get(i);
                nameView.setText(trailer.getName());
                trailersListView.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), trailer.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else if (itemType == REVIEW_ITEM) {
            for (int i = 0; i < mReviews.size(); i++) {
                View view = inflater.inflate(R.layout.review_list_item, reviewsListView, false);
                TextView authorView = (TextView) view.findViewById(R.id.review_list_item_author);
                TextView contentView = (TextView) view.findViewById(R.id.review_list_item_content);
                authorView.setText(mReviews.get(i).getAuthor());
                contentView.setText(mReviews.get(i).getContent());
                reviewsListView.addView(view);
            }
        }
    }
}
