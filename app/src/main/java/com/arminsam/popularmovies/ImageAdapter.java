package com.arminsam.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<Integer> mThumbIds;
    private LayoutInflater mInflater;

    public ImageAdapter(Context context, List<Integer> ids) {
        this.mContext = context;
        this.mThumbIds = ids;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Return the number of items in the Adapter
     *
     * @return
     */
    @Override
    public int getCount() {
        return this.mThumbIds.size();
    }

    /**
     * Return the item in the given position
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return this.mThumbIds.get(position);
    }

    /**
     * Return the id of the item in the given position
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return this.mThumbIds.get(position);
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

        // if item is not recycled, load item from its layout
        if (imageView == null) {
            imageView = (ImageView) this.mInflater.inflate(R.layout.grid_item, null);
        }

        imageView.setImageResource(this.mThumbIds.get(position));

        return imageView;
    }
}
