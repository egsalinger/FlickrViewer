package projects.egsal.flickrviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by egsal on 8/18/15.
 */
public class BitmapImageLoader extends AsyncTask<URL, Void, Bitmap> {

    private URL theURL;

    private BitmapDownloadListener listener;

    BitmapImageLoader(BitmapDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(URL... urls) {
        Bitmap bitmap = null;
        theURL = urls[0];
        URL downloadFrom = urls[0];
        try {
            bitmap = BitmapFactory.decodeStream(downloadFrom.openConnection().getInputStream());
        } catch (IOException e) {
            Log.e("Error Loading BMP", "Error loading bitmap!");
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        listener.downloadComplete(bitmap, theURL);
    }
}

