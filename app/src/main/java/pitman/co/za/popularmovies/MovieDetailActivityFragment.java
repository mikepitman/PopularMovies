package pitman.co.za.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pitman.co.za.popularmovies.Domain.MovieInfo;
import pitman.co.za.popularmovies.Domain.Review;
import pitman.co.za.popularmovies.Domain.Trailer;
import pitman.co.za.popularmovies.Utility.FetchAdditionalInfoTask;
import pitman.co.za.popularmovies.Utility.UrlBuilder;
import pitman.co.za.popularmovies.data.MoviesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private static String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();
    private Context mContext;
    public static ArrayAdapter<String> trailerAdapter;
    public static ArrayAdapter<String> reviewAdapter;
    public View rootView;
    public MovieInfo movieInfo;

    public MovieDetailActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selectedMovie", movieInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Log.d(LOG_TAG, "onCreateView() in MovieDetailActivityFragment called");

        if (savedInstanceState != null) {
            movieInfo = savedInstanceState.getParcelable("selectedMovie");
            Log.d(LOG_TAG, "movie retrieved from savedInstanceState");

        } else {
            Intent intent = getActivity().getIntent();
            if ((intent != null)) {
                movieInfo = intent.getParcelableExtra("selectedMovie");
            }

            // master-detail layout for a tablet -- change this to be more accurate/resilient
            if (movieInfo == null) {
                Bundle arguments = this.getArguments();
                movieInfo = arguments.getParcelable("selectedMovie");
            }
        }

        new FetchAdditionalInfoTask(this).execute(movieInfo.getMovieId());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_thumbnail);
        Picasso.with(getActivity())
                .load((UrlBuilder.BASE_URL + movieInfo.getPosterPath()))
                .resize(250, 300)
                .into(imageView);

        TextView textView = (TextView) rootView.findViewById(R.id.movie_title);
        textView.setText(movieInfo.getOriginalTitle());

        textView = (TextView) rootView.findViewById(R.id.movie_overview);
        textView.setText(movieInfo.getOverview());

        textView = (TextView) rootView.findViewById(R.id.release_date);
        textView.setText(movieInfo.getReleaseDate());

        textView = (TextView) rootView.findViewById(R.id.viewer_rating);
        String viewerRating = movieInfo.getViewerRating() + " / 10";
        textView.setText(viewerRating);

        // http://stackoverflow.com/questions/6091194/how-to-handle-button-clicks-using-the-xml-onclick-within-fragments
        final Button favouritesButton = (Button) rootView.findViewById(R.id.favourited_movie);

        movieInfo.setFavourite(isMovieFavourited(movieInfo.getMovieId()));
        if (movieInfo.isFavourite()) {
            favouritesButton.setText(getString(R.string.remove_from_favourites_button_text));
            favouritesButton.setTag(1);
        } else {
            favouritesButton.setText(getString(R.string.add_to_favourites_button_text));
            favouritesButton.setTag(0 );
        }

        favouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int status = (Integer) view.getTag();
                if (status == 1) {
                    favouritesButton.setText(getString(R.string.add_to_favourites_button_text));
                    view.setTag(0);
                    toggleFavourites();
                } else {
                    favouritesButton.setText(R.string.remove_from_favourites_button_text);
                    view.setTag(1);
                    toggleFavourites();
                }
            }
        });

        return rootView;
    }

    public void setAdditionalInfo(final List<Trailer> trailerList, List<Review> reviewList) {

        if (!trailerList.isEmpty()) {

            // As per reviewer suggestion
//            trailerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_trailer, R.id.list_item_trailer_textview, new ArrayList<String>());
            trailerAdapter = new ArrayAdapter<String>(mContext, R.layout.list_item_trailer, R.id.list_item_trailer_textview, new ArrayList<String>());
            trailerAdapter.clear();

            for (Trailer trailer : trailerList) {
                trailerAdapter.add("  " + trailer.getName());
            }

            ListView listView = (ListView) rootView.findViewById(R.id.trailer_list);
            listView.setAdapter(trailerAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Trailer trailer = trailerList.get(position);

                    // retrieved from http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                    Intent intent;
                    try {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
                    } catch (ActivityNotFoundException ex) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                    }
//                    https://www.youtube.com/watch?v=HGElAW224dE
//                    https://developer.android.com/guide/components/intents-filters.html#ExampleSend
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Log.d(LOG_TAG, "Intent unable to be started, no app capable of handling intent");
                    }
                }
            });
        }

        if (!reviewList.isEmpty()) {

//            reviewAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_review, R.id.list_item_review_textview, new ArrayList<String>());
            reviewAdapter = new ArrayAdapter<String>(mContext, R.layout.list_item_review, R.id.list_item_review_textview, new ArrayList<String>());
            reviewAdapter.clear();

            for (Review review : reviewList) {
                String reviewText = "Review by " + review.getAuthor() + " - " + review.getContent();
                reviewAdapter.add(reviewText);
            }

            ListView listView = (ListView) rootView.findViewById(R.id.review_list);
            listView.setAdapter(reviewAdapter);
        }
    }

    private boolean isMovieFavourited(String movieId) {
        Uri selectedMovieUri = MoviesContract.buildSelectedMovie(movieId);
        Cursor cur = getActivity().getContentResolver().query(selectedMovieUri,
                new String[]{MoviesContract.FavouritedMovieEntry.COLUMN_MOVIE_ID},
                MoviesContract.FavouritedMovieEntry.COLUMN_MOVIE_ID + "=" + movieId,
                null,
                null);

        try {
            while (cur.moveToNext()) {
                String cursorMovieId = cur.getString(cur.getColumnIndex("movie_id"));
                if (movieId.equals(cursorMovieId)) {
                    return true;
                }
            }
        } finally {
            cur.close();
        }

        return false;
    }

    // User has clicked the "Favourites" button - either add to database, or remove
    public void toggleFavourites() {
        boolean isMovieFavourited = isMovieFavourited(movieInfo.getMovieId());
        Log.d(LOG_TAG, "Toggling Favourites - either add/remove movie to/from database");

        if (isMovieFavourited) {
            int numberOfMoviesDeleted = getActivity().getContentResolver().delete(MoviesContract.FavouritedMovieEntry.CONTENT_URI, "movie_id = ?", new String[]{movieInfo.getMovieId()});

            if (numberOfMoviesDeleted != 0) {
                Log.d(LOG_TAG, "Movie with id " + movieInfo.getMovieId() + " was deleted from database.");
            } else {
                Log.d(LOG_TAG, "Removal of movie with id " + movieInfo.getMovieId() + " failed! " + numberOfMoviesDeleted);
            }
        } else {
            saveFavouriteMovie(movieInfo);
        }
    }

    // insert data into database using ContentValues (data object for ContentResolver to process)
    private void saveFavouriteMovie(MovieInfo movieInfo) {

        ContentValues movieContentValuesObject = new ContentValues();
        movieContentValuesObject.put(MoviesContract.FavouritedMovieEntry.COLUMN_MOVIE_ID, movieInfo.getMovieId());
        movieContentValuesObject.put(MoviesContract.FavouritedMovieEntry.COLUMN_ORIGINAL_TITLE, movieInfo.getOriginalTitle());
        movieContentValuesObject.put(MoviesContract.FavouritedMovieEntry.COLUMN_POSTER_PATH, movieInfo.getPosterPath());
        movieContentValuesObject.put(MoviesContract.FavouritedMovieEntry.COLUMN_OVERVIEW, movieInfo.getOverview());
        movieContentValuesObject.put(MoviesContract.FavouritedMovieEntry.COLUMN_RELEASE_DATE, movieInfo.getReleaseDate());
        movieContentValuesObject.put(MoviesContract.FavouritedMovieEntry.COLUMN_VIEWER_RATING, movieInfo.getViewerRating());

        // bulkInsert ContentValues array
        Log.d(LOG_TAG, "adding favourited movie to database");
            Uri uri = getActivity().getContentResolver().insert(MoviesContract.FavouritedMovieEntry.CONTENT_URI, movieContentValuesObject);
        if (uri == null) {
            Log.d(LOG_TAG, "Uri was null - this shouldn't really happen(?)");
        } else {
            Log.d(LOG_TAG, "Movie with id " + movieInfo.getMovieId() + " was added to database of favourites. " + uri);
        }
    }
}

