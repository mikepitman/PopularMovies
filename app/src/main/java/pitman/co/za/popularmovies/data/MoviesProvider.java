package pitman.co.za.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Michael on 2016/04/24.
 */
public class MoviesProvider extends ContentProvider {

    /*
    * Attribution: based foundationally on WeatherProvider class in Sunshine weather app provided by Udacity
    */

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // holds URI patterns for matching incoming request URIs
    private static String LOG_TAG = MoviesProvider.class.getSimpleName();
    private MoviesDbHelper mMoviesDbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_LISTING_TYPE = 101;
    static final int MOVIE_DETAIL = 102;
    static final int MOVIE_ADDITIONAL_INFO = 103;   // queries for trailers/reviews

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // NOTE! Order is important, # comes before *, it would seem
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/", MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", MOVIE_DETAIL);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", MOVIE_LISTING_TYPE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#/*", MOVIE_ADDITIONAL_INFO);

        return matcher;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// Implementations of parent-class abstract methods ////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is, and query the database accordingly.
        Cursor returnCursor;
        int matchValue = buildUriMatcher().match(uri);

        switch (matchValue) {
            // "movie/"
            // Something, somewhere, is calling query with a URI matching this, and I don't know what or where
            case MOVIE: {
                Log.d(LOG_TAG, "MOVIE / 100 " + uri);
                returnCursor = mMoviesDbHelper.getReadableDatabase().query(
                        MoviesContract.FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // "movie/*"
            case MOVIE_LISTING_TYPE: {
                Log.d(LOG_TAG, "MOVIE_LISTING_TYPE / 101");
                returnCursor = mMoviesDbHelper.getReadableDatabase().query(
                        MoviesContract.FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // "movie/#"
            case MOVIE_DETAIL: {
                // get the selected movie detail.
                Log.d(LOG_TAG, "MOVIE_DETAIL / 102");

                returnCursor = mMoviesDbHelper.getReadableDatabase().query(
                        MoviesContract.FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "film_id/# - call to fetch data for either reviews or trailers
            case MOVIE_ADDITIONAL_INFO: {
                Log.d(LOG_TAG, "MOVIE_ADDITIONAL_INFO / 103");

                returnCursor = mMoviesDbHelper.getReadableDatabase().query(
                        MoviesContract.FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    // Defines the tye of cursor returned by each URI - multiple results (directory) or single result
    @Nullable
    @Override
    public String getType(Uri uri) {

        final int uriMatch = sUriMatcher.match(uri);

        switch (uriMatch) {
            case MOVIE_LISTING_TYPE:
                return MoviesContract.FavouritedMovieEntry.CONTENT_TYPE;
            case MOVIE_DETAIL:
                return MoviesContract.FavouritedMovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI!: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

/* Modified code example from Sunshine App */
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                Log.d(LOG_TAG, "inserting for URI matching 'MOVIE'");
                long _id = db.insert(MoviesContract.FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.FavouritedMovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Code from SunShine
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MoviesContract.FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
