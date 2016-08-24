package pitman.co.za.popularmovies.Utility;

public class UrlBuilder {
    private static final String BASE = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185/";
    public static final String BASE_URL = BASE + IMAGE_SIZE;
    public static final String API_KEY = "";

    private UrlBuilder() {
        // No instances.
    }
}
