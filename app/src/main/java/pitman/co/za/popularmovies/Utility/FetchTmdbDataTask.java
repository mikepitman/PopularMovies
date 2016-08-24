package pitman.co.za.popularmovies.Utility;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import pitman.co.za.popularmovies.Domain.MovieInfo;
import pitman.co.za.popularmovies.MainActivityFragment;

/**
 * Created by Michael on 2016/02/15.
 */
    public class FetchTmdbDataTask extends AsyncTask<String, Void, ArrayList<MovieInfo>> {

    private String LOG_TAG = FetchTmdbDataTask.class.getSimpleName();
    private MainActivityFragment mMainActivityFragment;

    public FetchTmdbDataTask(MainActivityFragment mainActivityFragment) {
        this.mMainActivityFragment = mainActivityFragment;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieInfo> s) {
        super.onPostExecute(s);
        Log.d(LOG_TAG, "passing arraylist back in onPostExecute()");
        mMainActivityFragment.createAndGenerateAdapterAndGridView(s);
    }

    @Override
    protected ArrayList<MovieInfo> doInBackground(String... params) {

        String sort_mode = params[0];
        ArrayList<MovieInfo> moviesArray = null;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sort_mode)
                .appendQueryParameter("api_key", UrlBuilder.API_KEY);
        String myUrl = builder.build().toString();
        Log.d(LOG_TAG, myUrl);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieListing = null;

        try {
            URL url = new URL(myUrl);

            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.d(LOG_TAG, "network query being made");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;    // Nothing to do.
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newli ne isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;    // Stream was empty.  No point in parsing.
            }
            movieListing = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;        // If the code didn't successfully get the weather data, there's no point in attempting to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        if (movieListing.isEmpty()) {
            Log.d(LOG_TAG, "movieListing string is empty");
        }

        try {
            moviesArray = getMovieDataFromJson(movieListing);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException caught: " + e.getMessage());
        }

        return moviesArray;
    }

    /**
     * Parse returned JSON-encoded movie data and populate objects.
     * Based on Udacity-supplied JSON gist from Developing Android Apps: Fundamentals 'Project Sunshine' app
     */
    private ArrayList<MovieInfo> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String JSON_RESULTS = "results";
        final String JSON_MOVIE_ID = "id";
        final String JSON_POSTER_PATH = "poster_path";
        final String JSON_OVERVIEW = "overview";
        final String JSON_RELEASE_DATE = "release_date";
        final String JSON_ORIG_TITLE = "original_title";
        final String JSON_VOTE_AVERAGE = "vote_average";

        JSONObject jsonMovieObject = new JSONObject(movieJsonStr);
        JSONArray moviesArray = jsonMovieObject.getJSONArray(JSON_RESULTS);

        ArrayList<MovieInfo> movieInfoArray = new ArrayList<>();
        for (int i = 0; i < moviesArray.length(); i++) {

            MovieInfo movie = new MovieInfo();
            JSONObject jsonMovie = moviesArray.getJSONObject(i);

            movie.setMovieId(jsonMovie.getString(JSON_MOVIE_ID) == null ? "" : jsonMovie.getString(JSON_MOVIE_ID));
            movie.setPosterPath(jsonMovie.getString(JSON_POSTER_PATH) == null ? "" : jsonMovie.getString(JSON_POSTER_PATH));
            movie.setOverview(jsonMovie.getString(JSON_OVERVIEW) == null ? "" : jsonMovie.getString(JSON_OVERVIEW));
            movie.setReleaseDate(jsonMovie.getString(JSON_RELEASE_DATE) == null ? "" : jsonMovie.getString(JSON_RELEASE_DATE));
            movie.setOriginalTitle(jsonMovie.getString(JSON_ORIG_TITLE) == null ? "" : jsonMovie.getString(JSON_ORIG_TITLE));
            movie.setViewerRating(jsonMovie.getString(JSON_VOTE_AVERAGE) == null ? "" : jsonMovie.getString(JSON_VOTE_AVERAGE));

            movieInfoArray.add(movie);
        }

        return movieInfoArray;
    }
}