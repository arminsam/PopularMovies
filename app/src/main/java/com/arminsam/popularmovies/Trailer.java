package com.arminsam.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    private String source;
    private String name;
    private long size;

    public Trailer() {}

    private Trailer(Parcel in) {
        source = in.readString();
        name = in.readString();
        size = in.readLong();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(source);
        dest.writeString(name);
        dest.writeLong(size);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
