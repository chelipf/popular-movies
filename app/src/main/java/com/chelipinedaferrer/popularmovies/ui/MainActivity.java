package com.chelipinedaferrer.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.chelipinedaferrer.popularmovies.R;
import com.chelipinedaferrer.popularmovies.ui.fragments.FavoritesFragment;
import com.chelipinedaferrer.popularmovies.ui.fragments.MoviesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private static final String IS_FRAGMENT_SET = "is_fragment_set";
    private boolean isFragmentSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_movies:
                                selectedFragment = new MoviesFragment();
                                break;
                            case R.id.action_favorites:
                                selectedFragment = new FavoritesFragment();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        if (savedInstanceState != null && savedInstanceState.containsKey(IS_FRAGMENT_SET)) {
            isFragmentSet = savedInstanceState.getBoolean(IS_FRAGMENT_SET);
        } else {
            isFragmentSet = getSupportFragmentManager().findFragmentById(R.id.frame_layout) != null;
        }

        //Manually displaying the first fragment - one time only
        if (!isFragmentSet) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new MoviesFragment());
            transaction.commit();

            isFragmentSet = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_FRAGMENT_SET, isFragmentSet);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}