package com.lachlanpage.gnss_spoofing_detector;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Debug;
import android.view.View;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Fragment Related Variables
    private static final int NUMBER_OF_FRAGMENTS = 3;
    private static final int FRAGMENT_OVERVIEW = 0;
    private static final int FRAGMENT_MAPS = 1;
    private static final int FRAGMENT_DEBUG = 2;
    private Fragment[] mFragments;

    // Permission Related Variables
    private static final int LOCATION_REQUEST_ID = 1;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (hasPermissions(this))
        {
            setupFragments();
        }

        else
        {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, LOCATION_REQUEST_ID);
            setupFragments();
        }
    }

    private boolean hasPermissions(Activity activity)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permissions granted at install time.
            return true;
        }
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void setupFragments()
    {
        mFragments = new Fragment[NUMBER_OF_FRAGMENTS];
        OverviewFragment overviewFragment = new OverviewFragment();
        mFragments[FRAGMENT_OVERVIEW] =  overviewFragment;

        MapsFragment mapsFragment = new MapsFragment();
        mFragments[FRAGMENT_MAPS] = mapsFragment;

        DebugFragment debugFragment = new DebugFragment();
        mFragments[FRAGMENT_DEBUG] = debugFragment;

        //... add more fragments here

        // setup gnss manager
        GNSSDetector mDetector = new GNSSDetector(this, overviewFragment, mapsFragment, debugFragment);
        overviewFragment.setDetector(mDetector);

        // Setup view pager for fragment contents
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(NUMBER_OF_FRAGMENTS);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        // Tab Layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }


    /* View Pager is responsible for changing the fragments that are shown to the user.
       On App startup all the fragments are created and then stored in memory.
       This can eventually be moved to it's own file if needed for cleanliness
     */
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FRAGMENT_OVERVIEW:
                    return mFragments[FRAGMENT_OVERVIEW];
                case FRAGMENT_MAPS:
                    return mFragments[FRAGMENT_MAPS];
                case FRAGMENT_DEBUG:
                    return mFragments[FRAGMENT_DEBUG];
                default:
                    throw new IllegalArgumentException("Invalid section: " + position);
            }
        }

        @Override
        public int getCount() {
            // Show total pages.
            return NUMBER_OF_FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale locale = Locale.getDefault();
            switch (position) {
                case FRAGMENT_OVERVIEW:
                    return getString(R.string.overview_fragment_label).toUpperCase(locale);
                case FRAGMENT_MAPS:
                    return getString(R.string.maps_fragment_label).toUpperCase(locale);
                case FRAGMENT_DEBUG:
                    return getString(R.string.debug_fragment_label).toUpperCase(locale);
                default:
                    return super.getPageTitle(position);
            }
        }
    }
}
