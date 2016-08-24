package pitman.co.za.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pitman.co.za.popularmovies.data.MoviesContract.FavouritedMovieEntry;
import pitman.co.za.popularmovies.data.MoviesContract.MovieTrailerEntry;
import pitman.co.za.popularmovies.data.MoviesContract.MovieReviewEntry;

/**
 * Created by Michael on 2016/04/04.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    // Framework based on example code in Udacity course
    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE + " (" +
                FavouritedMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                FavouritedMovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                FavouritedMovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavouritedMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavouritedMovieEntry.COLUMN_VIEWER_RATING + " TEXT NOT NULL, " +
                FavouritedMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavouritedMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL)";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieTrailerEntry.TABLE_NAME + " (" +
                MovieTrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MovieTrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                MovieTrailerEntry.COLUMN_TRAILER_LANGUAGE + " TEXT NOT NULL, " +
                MovieTrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +
                MovieTrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                MovieTrailerEntry.COLUMN_TRAILER_SITE + " TEXT NOT NULL, " +
                MovieTrailerEntry.COLUMN_TRAILER_SIZE + " TEXT NOT NULL, " +
                MovieTrailerEntry.COLUMN_TRAILER_TYPE + " TEXT NOT NULL, " +
                MovieTrailerEntry.COLUMN_TRAILER_MOVIE_KEY + " INTEGER NOT NULL, " +

                // Set up the trailer movie column as a foreign key to favourited movie table.
                " FOREIGN KEY (" + MovieTrailerEntry.COLUMN_TRAILER_MOVIE_KEY + ") REFERENCES " +
                FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE + " (" + FavouritedMovieEntry._ID + "))";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieReviewEntry.TABLE_NAME + " (" +
                MovieReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MovieReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                MovieReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                MovieReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                MovieReviewEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, " +
                MovieReviewEntry.COLUMN_REVIEW_MOVIE_KEY + " INTEGER NOT NULL, " +

                // Set up the review movie column as a foreign key to favourited movie table.
                " FOREIGN KEY (" + MovieReviewEntry.COLUMN_REVIEW_MOVIE_KEY + ") REFERENCES " +
                FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE + " (" + FavouritedMovieEntry._ID + "))";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouritedMovieEntry.FAVOURITE_MOVIES_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieTrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
