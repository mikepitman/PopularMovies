package pitman.co.za.popularmovies.Utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pitman.co.za.popularmovies.Domain.MovieInfo;
import pitman.co.za.popularmovies.R;

/**
 * Created by Michael on 2016/01/29.
 * Built using example at http://www.mkyong.com/android/android-gridview-example/
 */
public class MovieGridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MovieInfo> movieInformation;
    private String LOG_TAG = MovieGridViewAdapter.class.getSimpleName();

    public MovieGridViewAdapter(Context context, ArrayList<MovieInfo> movieInformation) {
        this.context = context;
        this.movieInformation = movieInformation;
    }

    @Override
    public int getCount() {
        return movieInformation.size();
    }

    @Override
    public Object getItem(int position) {
        return movieInformation.get(position);
    }

    public String getMoviePosterPath(int position) {
        return UrlBuilder.BASE_URL + movieInformation.get(position).getPosterPath();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

//    public void addAll(ArrayList<MovieInfo> movieInfoArrayList) {
//        this.movieInformation.clear();
//        this.movieInformation.addAll(movieInfoArrayList);
//        notifyDataSetChanged();
//    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d(LOG_TAG, "notifyDataSetChanged() called in parent class");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridview;

        if (convertView == null) {

            // get layout
            gridview = inflater.inflate(R.layout.thumbnail_imageview, null);

            // set image based on selected text
            ImageView imageView = (ImageView) gridview.findViewById(R.id.gridItem_image);

            Log.d(LOG_TAG, "Image for " + movieInformation.get(position).getOriginalTitle());
            String url = getMoviePosterPath(position);
            Picasso.with(context)
                    .load(url)
                    .resize(300, 450)
                    .into(imageView);

        } else {
            Log.d(LOG_TAG, "getView(), convertView != null for " + movieInformation.get(position).getOriginalTitle());
            gridview = (View) convertView;
        }

        return gridview;
    }

    public void swapData(ArrayList<MovieInfo> movies) {
//        movieInformation = movies;
        int numberOfOldEntries = movieInformation.size();
        movieInformation.clear();
        movieInformation.addAll(movies);
        Log.d(LOG_TAG, "data swapped out! " + numberOfOldEntries + " items removed, items added: " + movies.size());
//        this.notifyDataSetInvalidated();
        notifyDataSetChanged();
    }
}
