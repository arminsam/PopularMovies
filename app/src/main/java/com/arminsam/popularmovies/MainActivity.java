package com.arminsam.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    final String MOVIESFRAGMENT_TAG = "MFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), MOVIESFRAGMENT_TAG)
//                        .commit();
//            }
        } else {
            mTwoPane = false;
        }
    }

    public boolean hasTwoPane() {
        return mTwoPane;
    }
}
