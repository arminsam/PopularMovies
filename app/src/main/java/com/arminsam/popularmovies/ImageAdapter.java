package com.arminsam.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends CursorAdapter {

    public ImageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Cache of the children views for a movie list item.
     */
    public static class ViewHolder {
        public final ImageView posterImage;

        public ViewHolder(View view) {
            posterImage = (ImageView) view.findViewById(R.id.grid_thumbnail);
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
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Picasso.with(context).load(cursor.getString(MainActivityFragment.COL_POSTER_PATH))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.error_placeholder)
                .into(viewHolder.posterImage);
    }
}