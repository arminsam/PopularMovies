package com.arminsam.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.arminsam.popularmovies.data.PopularMoviesContract;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends CursorAdapter {

    public ImageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public static class ViewHolder {
        public final ImageView posterImage;
        public final ImageView favoriteButton;

        public ViewHolder(View view) {
            posterImage = (ImageView) view.findViewById(R.id.grid_thumbnail);
            favoriteButton = (ImageView) view.findViewById(R.id.favorite_button);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        final boolean isFavorite = cursor.getInt(MainActivityFragment.COL_FAVORITE) == 1;
        viewHolder.favoriteButton.setBackgroundResource(
                isFavorite ? R.mipmap.favorite_button_active : R.mipmap.favorite_button);
        viewHolder.favoriteButton.setTag(cursor.getString(MainActivityFragment.COL_MOVIE_ID));
        Picasso.with(context).load(cursor.getString(MainActivityFragment.COL_POSTER_PATH))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.error_placeholder)
                .into(viewHolder.posterImage);

        viewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();
                cv.put(PopularMoviesContract.MoviesEntry.COLUMN_FAVORITE, !isFavorite);
                String movieQuery = PopularMoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ";
                String[] queryParams = new String[]{view.getTag().toString()};
                context.getContentResolver()
                        .update(PopularMoviesContract.MoviesEntry.CONTENT_URI, cv, movieQuery, queryParams);
            }
        });
    }
}