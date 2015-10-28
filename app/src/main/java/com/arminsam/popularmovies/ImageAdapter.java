package com.arminsam.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> mMovies;
    private LayoutInflater mLayoutInflater;

    public ImageAdapter(Context context, List<Movie> movies) {
        mContext = context;
        mMovies = movies;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * Return the number of items in the Adapter
     *
     * @return
     */
    @Override
    public int getCount() {
        return mMovies.size();
    }

    /**
     * Return the item in the given position
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return mMovies.get(position);
    }

    /**
     * Return the id of the item in the given position
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return mMovies.get(position).getMovieId();
    }

    /**
     * Return an ImageView object for each item referenced by the adapter
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;

        // if item is not recycled, create a new ImageView object
        if (imageView == null) {
            imageView = (ImageView) mLayoutInflater.inflate(R.layout.grid_item, null);
        }
        Movie movie = mMovies.get(position);
        Picasso.with(mContext).load(movie.getPosterPath()).into(imageView);

        return imageView;
    }

    /**
     * Clear the list.
     */
    public void clear() {
        mMovies.clear();
    }

    /**
     * Add new movie to the list.
     *
     * @param movie
     */
    public void add(Movie movie) {
        mMovies.add(movie);
    }
}