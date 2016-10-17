package projects.egsal.flickrviewer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Eric Salinger on 8/18/15.
 *
 * This is a primitive Flickr API parser, which uses Flickr's public API to perform searches on tags.
 * When I created it (8/18/2015), I was having some trouble parsing the native JSON from Flickr's
 * responses. To deal with this, I used a Scanner object and some creative thinking to parse the
 * server output.
 *
 * Note that this API provides direct URLs, so we don't have to construct URLs, as we would with a
 * new approach.
 */
@Deprecated
public class FlickrAPIParser extends AsyncTask<String, URL, ArrayList<URL>> {

    private final FlickrAdapter theAdapter;

    public FlickrAPIParser(FlickrAdapter adapter) {
        theAdapter = adapter;
    }

    @Override
    protected ArrayList<URL> doInBackground(String... params) {
        URL feed;

        try {
            feed = new URL(params[0]);
            ArrayList<URL> imageLocations = new ArrayList<URL>();

            // For some reason I was having an issue parsing the stream as JSON. Since this is a
            //fast run, and I only care about the media locations, we'll use scanner
            Scanner scanner = new Scanner(feed.openStream());

            scanner.useDelimiter("\n");
            String media = "";
            while (scanner.hasNext()) {
                String[] tags;
                String next = scanner.next();
                // The only element we need here is 'media' as that's a link to the URL we want.
                if (next.contains("\"media\"")) {
                    // Some guessing and checking yielded these values (plus "magic number"s for
                    // offsets that give us a URL.
                    String imageURL = next.substring(next.indexOf("{") + 6, next.indexOf("}") - 1);
                    Log.d("media", imageURL);

                    publishProgress(new URL(imageURL));

                    imageLocations.add(new URL(imageURL));
                }
                // I would remove this in production, but for you guys to test the tag search, this
                // is really handy -- just look for the debug info regarding tags, and you can tailor
                // a search to just those tags.
                else if (next.contains("\"tags\"")) {
                    String toSplit = next.substring(next.indexOf(":") + 3, next.length() - 1);
                    if (toSplit.length() > 0) {
                        Log.d("tags", toSplit);
                        tags = toSplit.split(" ");
                        Log.d("tags", tags.length + " Tags long");
                    } else {
                        tags = new String[0];
                        Log.d("tags", "No tags");
                    }
                }
            }
            return imageLocations;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(URL... values) {
        theAdapter.loadBitmapFromURL(values[0]);
    }
}
