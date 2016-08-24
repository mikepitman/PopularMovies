package pitman.co.za.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

public class MovieDetailActivity extends ActionBarActivity {

    private static String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.detail_fragment_container);

        if (fragment == null) {
            fragment = new MovieDetailActivityFragment();
            fm.beginTransaction().add(R.id.detail_fragment_container, fragment).commit();
        }
    }
}
