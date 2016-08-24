package pitman.co.za.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import pitman.co.za.popularmovies.Domain.MovieInfo;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callbacks {
// ActionBarActivity includes actionBar with menu/options at top - FragmentActivity doesn't include actionBar (as standard)

    private String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("LAYOUT_RES_ID", getLayoutResId());
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onMovieSelected(MovieInfo movieInfo) {

        /*Guidance in part from Android Programming 2nd Ed, The Big Nerd Ranch pg 307+,
        as well as a lot of other tutorial sites, which I failed to notarise at the time.*/
        if (findViewById(R.id.twopane_activity) == null) {
            // phone
            Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
            movieDetailIntent.putExtra("selectedMovie", movieInfo);
            startActivity(movieDetailIntent);
        } else {
            // tablet
            Bundle arguments = new Bundle();
            arguments.putParcelable("selectedMovie", movieInfo);

            Fragment movieDetailFragment = new MovieDetailActivityFragment();
            movieDetailFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, movieDetailFragment).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "mainActivity being created");
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new MainActivityFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(LOG_TAG, "item.getItemId(): " + id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // lifecycle methods added for Settings/Preference debugging
//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d(LOG_TAG, "onStart() called");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d(LOG_TAG, "onPause() called");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d(LOG_TAG, "onResume() called");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d(LOG_TAG, "onStop() called");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(LOG_TAG, "onDestroy() called");
//    }
}
