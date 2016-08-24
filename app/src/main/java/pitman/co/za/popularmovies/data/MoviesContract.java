package pitman.co.za.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;


/**
 * Created by Michael on 2016/04/04.
 */
public class MoviesContract {

    private static String LOG_TAG = MoviesContract.class.getSimpleName();

    /* Based on contentProvider-related code in WeatherContract from Udacity Sunshine example app
     */
    public static final String CONTENT_AUTHORITY = "pitman.co.za.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    // Inner class defining mapping of MovieInfo object into database table
    public static final class FavouritedMovieEntry implements BaseColumns {

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/";

//      Database table name
        public static final String FAVOURITE_MOVIES_TABLE = "favourited_movies";
//      Database tables
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VIEWER_RATING = "viewer_rating";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static Uri buildMovieUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

//    Additional table definitions
    public static final class MovieTrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie_trailers";

        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_TRAILER_LANGUAGE = "trailer_language";
        public static final String COLUMN_TRAILER_KEY = "trailer_key";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER_SITE = "trailer_site";
        public static final String COLUMN_TRAILER_SIZE = "trailer_size";
        public static final String COLUMN_TRAILER_TYPE = "trailer_type";
        public static final String COLUMN_TRAILER_MOVIE_KEY = "trailer_movie";
    }

//    Additional table definitions
    public static final class MovieReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie_reviews";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";
        public static final String COLUMN_REVIEW_URL = "review_url";
        public static final String COLUMN_REVIEW_MOVIE_KEY = "review_movie";
    }

    public static Uri buildMovieListing(String listingType) {
        Uri builtUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(listingType).build();
        Log.d(LOG_TAG, "listing uri constructed: " + builtUri);
        return builtUri;
    }

    public static Uri buildSelectedMovie(String movieId) {
        Uri builtUri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(movieId).build();
        Log.d(LOG_TAG, "selected movie uri constructed: " + builtUri);
        return builtUri;
    }

    public static String getMovieId(Uri uri) {
        String movieId = uri.getPathSegments().get(0);
        Log.d(LOG_TAG, "movieId extracted from URI: " + movieId);
        return movieId;
    }

    public static String getMovieListingType(Uri uri) {
        String movieListingType1 = uri.getPathSegments().get(0);
        String movieListingType2 = uri.getPathSegments().get(1);
        Log.d(LOG_TAG, "uri received: " + uri);
        Log.d(LOG_TAG, "movie listing type extracted from URI: " + movieListingType1 + "  " + movieListingType2);
        return movieListingType1;
    }
}
