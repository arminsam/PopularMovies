package com.arminsam.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<Movie> {

    public ImageAdapter(Context context, List<Movie> movies) {
        super(context, 0, movies);
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
        ImageView posterImage = (ImageView) convertView;

        // if item is not recycled, create a new ImageView object
        if (posterImage == null) {
            posterImage = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        Movie movie = getItem(position);
        Picasso.with(getContext()).load(movie.getPosterPath())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.error_placeholder)
                .into(posterImage);

        return posterImage;
    }
}