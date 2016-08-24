package pitman.co.za.popularmovies.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Michael on 2016/02/01.
 */
public class MovieInfo implements Parcelable {

    private String movieId;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private String originalTitle;
    private String viewerRating;
//    private String voteCount;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;

    private boolean favourite = false;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getViewerRating() {
        return viewerRating;
    }

    public void setViewerRating(String viewerRating) {
        this.viewerRating = viewerRating;
    }

//    public String getVoteCount() {
//        return voteCount;
//    }

//    public void setVoteCount(String voteCount) {
//        this.voteCount = voteCount;
//    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieId);
        dest.writeString(this.posterPath);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeString(this.originalTitle);
        dest.writeString(this.viewerRating);
//        http://stackoverflow.com/questions/6201311/how-to-read-write-a-boolean-when-implementing-the-parcelable-interface
        dest.writeInt(this.isFavourite() ? 1 : 0);
//        dest.writeString(this.voteCount);
    }

    public MovieInfo() {
    }

    protected MovieInfo(Parcel in) {
        this.movieId = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.originalTitle = in.readString();
        this.viewerRating = in.readString();
        this.favourite = in.readInt() != 0;
//        this.voteCount = in.readString();
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
