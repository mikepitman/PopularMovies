package pitman.co.za.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import pitman.co.za.popularmovies.Domain.MovieInfo;
import pitman.co.za.popularmovies.Utility.FetchTmdbDataTask;
import pitman.co.za.popularmovies.Utility.MovieGridViewAdapter;
import pitman.co.za.popularmovies.data.MoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private Callbacks mCallbacks;
    private ArrayList<MovieInfo> mMovieInfoArrayList;
    private String preference;
    private static final int CURSOR_LOADER = 0;
    private int selectedMoviePosition = 0;

    public static MovieGridViewAdapter adapter;
    public View rootView;
    private GridView gridView;

    public MainActivityFragment() {
    }

    /*Guidance from Android Programming Ed 2, Big Nerd Ranch Guide in a number of places in this section,
    especially the good coding practice wrt actions vs fragments */
    public interface Callbacks {
        void onMovieSelected(MovieInfo movieInfo);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMovieInfoArrayList != null) {
            outState.putInt("numberOfMovies", mMovieInfoArrayList.size());
            for (int i = 0; i < mMovieInfoArrayList.size(); i++) {
                outState.putParcelable("movie" + i, mMovieInfoArrayList.get(i));
            }
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
        Log.d(LOG_TAG, "1. onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        Log.d(LOG_TAG, "2. onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "3. onCreateView() in MainActivityFragment called");
        rootView = inflater.inflate(R.layout.fragment_main, fragment, false);  // R.layout.fragment_main refers to filename of fragment_main.xml, not an ID

        this.setHasOptionsMenu(true);

        Context context = this.getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preference = preferences.getString(context.getString(R.string.sort_param_key), context.getString(R.string.sort_param_default));

        adapter = new MovieGridViewAdapter(getActivity(), new ArrayList<MovieInfo>());

// formerly from createAndGenerateAdapterAndGridView()
        gridView = (GridView) rootView.findViewById(R.id.film_gridview);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMoviePosition = position;
                MovieInfo movieInfo = (MovieInfo) adapter.getItem(position);
                mCallbacks.onMovieSelected(movieInfo);
            }
        });

        // moved to onStart() for testing
        if (savedInstanceState != null) {
            retrieveState(savedInstanceState);
            Log.d(LOG_TAG, "retrieving state after rotation or similar");
        } else {
            executeQuery();
        }

        if (mMovieInfoArrayList != null) {
            if (mMovieInfoArrayList.size() > 0) {
                Log.d(LOG_TAG, "swapping adapter data");
                adapter.swapData(mMovieInfoArrayList);
            }
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "4. onStart() called");

        // Need to detect if the preference has changed
        Context context = this.getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currentPref = preferences.getString(context.getString(R.string.sort_param_key), context.getString(R.string.sort_param_default));
        Log.d(LOG_TAG, "currentPref is " + currentPref + ", stored pref is " + preference);

        if ((preference == null) || (!preference.equals(currentPref))) {
            Log.d(LOG_TAG, "in onStart() - setting preference to " + currentPref + ", running executeQuery()");

            adapter = new MovieGridViewAdapter(getActivity(), new ArrayList<MovieInfo>());
// for testing start
            gridView = (GridView) rootView.findViewById(R.id.film_gridview);

            gridView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();     // added per reviewer suggestion for rearrangement

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedMoviePosition = position;
                    MovieInfo movieInfo = (MovieInfo) adapter.getItem(position);
                    mCallbacks.onMovieSelected(movieInfo);
                }
            });
// for testing end


            preference = currentPref;
            executeQuery();
        }
    }

    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "5. onResume()");
    }

    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "6. onPause()");
    }

    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "7. onStop()");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "8. onDestroyView()");
    }

    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        Log.d(LOG_TAG, "10. onDetach() - fragment about to be destroyed");
    }

    private void retrieveState(Bundle savedInstanceState) {
        ArrayList<MovieInfo> unparceledMovies = new ArrayList<>();
        // This is clunky, but getParcelableArrayList caused endless grief
        for (int i = 0; i < savedInstanceState.getInt("numberOfMovies"); i++) {
            MovieInfo movie = savedInstanceState.getParcelable("movie" + i);
            unparceledMovies.add(movie);
        }
        mMovieInfoArrayList = unparceledMovies;
        Log.d(LOG_TAG, "state retrieved from parcelable object");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    public void executeQuery() {
        if (preference.equals("favourites")) {
            Uri movieListingUri = MoviesContract.buildMovieListing(preference);
            Log.d(LOG_TAG, "favourites - fetching data from database");
            Cursor cur = getActivity().getContentResolver().query(movieListingUri, null, null, null, null);
            if (cur != null) {
                getLoaderManager().restartLoader(CURSOR_LOADER, null, MainActivityFragment.this);
                cur.close();
            }
        } else {
            if (isNetworkAvailable()) {
                Log.d(LOG_TAG, "NOT favourites - fetching data from tmdb");
                new FetchTmdbDataTask(this).execute(preference);
            } else {
                appMessageToast("No network connection available!");
            }
        }
    }

    // determines whether network connection is available.
    private boolean isNetworkAvailable() {
        /* http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html */
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connectivity = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        if (!connectivity) {
            appMessageToast("No network connection! You need to be online!");
        }
        return connectivity;
    }

    // Applicable app status messages passed to user in form on toast
    private void appMessageToast(String message) {
        Context context = getActivity();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    public void createAndGenerateAdapterAndGridView(ArrayList<MovieInfo> objectArray) {

        if (adapter == null) {
            Log.d(LOG_TAG, "adapter is null");
            adapter = new MovieGridViewAdapter(getActivity(), objectArray);
        } else {
            Log.d(LOG_TAG, "new object array passed into adapter, array length: " + objectArray.size());
            adapter.swapData(objectArray);
        }

        mMovieInfoArrayList = objectArray;

//        http://stackoverflow.com/questions/7746140/android-problems-using-fragmentactivity-loader-to-update-fragmentstatepagera
        handler.sendEmptyMessage(1);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ((getActivity().findViewById(R.id.twopane_activity) != null) && (mMovieInfoArrayList.size() > 0)) {
                selectedMoviePosition = (selectedMoviePosition >= mMovieInfoArrayList.size() ? mMovieInfoArrayList.size() - 1 : selectedMoviePosition);
                mCallbacks.onMovieSelected(mMovieInfoArrayList.get(selectedMoviePosition));
            }
        }
    };

    // Example code from Sunshine II app, and implementation guidance from https://github.com/Vane101/Vmovie
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "OnCreateLoader called, to return new Loader");
        Uri movieListingUri = MoviesContract.buildMovieListing(preference);
        return new CursorLoader(getActivity(),
                movieListingUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<MovieInfo> movieList = new ArrayList<>();
        if (preference.equals("favourites")) {
            if (data.getCount() > 0) {//check if cursor not empty
                data.moveToFirst();
                do {
                    MovieInfo movie = new MovieInfo();
                    movie.setMovieId(data.getString(1));
                    movie.setOriginalTitle(data.getString(2));
                    movie.setReleaseDate(data.getString(3));
                    movie.setViewerRating(data.getString(4));
                    movie.setPosterPath(data.getString(5));
                    movie.setOverview(data.getString(6));
                    movie.setFavourite(true);

                    Log.d(LOG_TAG, "loaded from database! "
                            + movie.getMovieId() + "  "
                            + movie.getOriginalTitle());

                    movieList.add(movie);
                    data.moveToNext();
                } while (!data.isAfterLast());
            } else {
                // Cursor was empty - no movies in 'Favourites' database
                appMessageToast("You don't have any 'Favourite' movies! Select movies from 'Highest Rated' or 'Popular' listings to add to 'Favourites'!");
            }

            mMovieInfoArrayList = movieList;
        }

        createAndGenerateAdapterAndGridView(mMovieInfoArrayList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
