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
import java.util.List;

import pitman.co.za.popularmovies.Domain.Review;
import pitman.co.za.popularmovies.Domain.Trailer;
import pitman.co.za.popularmovies.MovieDetailActivityFragment;

/*Created by Michael on 2016/02/15.
*/

public class FetchAdditionalInfoTask extends AsyncTask<String, Void, List<String>> {

    private String LOG_TAG = FetchAdditionalInfoTask.class.getSimpleName();
    private MovieDetailActivityFragment mMovieDetailActivityFragment;

    public FetchAdditionalInfoTask(MovieDetailActivityFragment mMovieDetailActivityFragment) {
        this.mMovieDetailActivityFragment = mMovieDetailActivityFragment;
    }

    @Override
    protected void onPostExecute(List<String> s) {
        super.onPostExecute(s);

        try {
            ArrayList<Trailer> trailers = parseTrailersFromJson(s.get(0));
            ArrayList<Review> reviews = parseReviewsFromJson(s.get(1));

            mMovieDetailActivityFragment.setAdditionalInfo(trailers, reviews);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException caught: " + e.getMessage());
        }
    }

    private ArrayList<Trailer> parseTrailersFromJson(String jsonData)
            throws JSONException {

        final String JSON_RESULTS = "results";
        final String JSON_MOVIE_ID = "id";
        final String JSON_LANGUAGE = "iso_639_1";
        final String JSON_KEY = "key";
        final String JSON_NAME = "name";
        final String JSON_SITE = "site";
        final String JSON_SIZE = "size";
        final String JSON_TYPE = "type";

        ArrayList<Trailer> trailerArray = new ArrayList<>();
        if (jsonData != null) {
            JSONObject jsonMovieObject = new JSONObject(jsonData);
            JSONArray jsonTrailersList = jsonMovieObject.getJSONArray(JSON_RESULTS);

            for (int i = 0; i < jsonTrailersList.length(); i++) {

                Trailer trailer = new Trailer();
                JSONObject jsonTrailer = jsonTrailersList.getJSONObject(i);

                trailer.setId(jsonTrailer.getString(JSON_MOVIE_ID) == null ? "" : jsonTrailer.getString(JSON_MOVIE_ID));
                trailer.setLanguage(jsonTrailer.getString(JSON_LANGUAGE) == null ? "" : jsonTrailer.getString(JSON_LANGUAGE));
                trailer.setKey(jsonTrailer.getString(JSON_KEY) == null ? "" : jsonTrailer.getString(JSON_KEY));
                trailer.setName(jsonTrailer.getString(JSON_NAME) == null ? "" : jsonTrailer.getString(JSON_NAME));
                trailer.setSite(jsonTrailer.getString(JSON_SITE) == null ? "" : jsonTrailer.getString(JSON_SITE));
                trailer.setSize(jsonTrailer.getString(JSON_SIZE) == null ? "" : jsonTrailer.getString(JSON_SIZE));
                trailer.setType(jsonTrailer.getString(JSON_TYPE) == null ? "" : jsonTrailer.getString(JSON_TYPE));

                trailerArray.add(trailer);
            }
        } else {
            Log.d(LOG_TAG, "json data for trailer(s) is null!");
        }

        return trailerArray;
    }

    private ArrayList<Review> parseReviewsFromJson(String jsonData)
            throws JSONException {

        final String JSON_RESULTS = "results";
        final String JSON_ID = "id";
        final String JSON_AUTHOR = "author";
        final String JSON_CONTENT = "content";
        final String JSON_URL = "url";

        ArrayList<Review> reviewArray = new ArrayList<>();
        if (jsonData != null) {
            JSONObject jsonMovieObject = new JSONObject(jsonData);
            JSONArray jsonReviewList = jsonMovieObject.getJSONArray(JSON_RESULTS);

            for (int i = 0; i < jsonReviewList.length(); i++) {

                Review review = new Review();
                JSONObject jsonReview = jsonReviewList.getJSONObject(i);

                review.setId(jsonReview.getString(JSON_ID) == null ? "" : jsonReview.getString(JSON_ID));
                review.setAuthor(jsonReview.getString(JSON_AUTHOR) == null ? "" : jsonReview.getString(JSON_AUTHOR));
                review.setContent(jsonReview.getString(JSON_CONTENT) == null ? "" : jsonReview.getString(JSON_CONTENT));
                review.setUrl(jsonReview.getString(JSON_URL) == null ? "" : jsonReview.getString(JSON_URL));

                reviewArray.add(review);
            }
        } else {
            Log.d(LOG_TAG, "json data for review(s) is null!");
        }

        return reviewArray;
    }

    @Override
    protected List<String> doInBackground(String... params) {

        String id = params[0];

        String trailerQueryUrl = generateTrailerQueryUrl(id);
        String reviewsQueryUrl = generateReviewsQueryUrl(id);

        String trailerListing = queryUsingUrl(trailerQueryUrl);
        String reviewsList = queryUsingUrl(reviewsQueryUrl);

        ArrayList<String> listings = new ArrayList<>();
        listings.add(trailerListing);
        listings.add(reviewsList);

        return listings;
    }

    private String queryUsingUrl(String queryUrl) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String queryResponse;

        try {
            URL url = new URL(queryUrl);

            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                // Stream was empty.  No point in parsing.
                return null;
            }
            queryResponse = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
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

        return queryResponse;
    }

    private String generateTrailerQueryUrl(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("videos")
                .appendQueryParameter("api_key", UrlBuilder.API_KEY);
        return builder.build().toString();
    }

    private String generateReviewsQueryUrl(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter("api_key", UrlBuilder.API_KEY);
        return builder.build().toString();
    }
}