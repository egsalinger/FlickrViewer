package projects.egsal.flickrviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by egsal on 8/18/15.
 */
@Deprecated
public class BitmapImageLoader extends AsyncTask<URL, Void, FlickrImage> {

    private URL theURL;

    private BitmapDownloadListener listener;

    BitmapImageLoader(BitmapDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected FlickrImage doInBackground(URL... urls) {
        Bitmap bitmap = null;
        theURL = urls[0];
        InputStream connection = null;
        ByteArrayOutputStream imageBytes = null;
        URL downloadFrom = urls[0];
        try {
            connection = downloadFrom.openConnection().getInputStream();
            imageBytes = new ByteArrayOutputStream();
            int readByte;
            while ((readByte = connection.read()) != -1) {
                imageBytes.write(readByte);
            }
            bitmap = BitmapFactory.decodeStream(downloadFrom.openConnection().getInputStream());
            byte[] bytes = imageBytes.toByteArray();

            return new FlickrImage(bytes, theURL) ;
        } catch (IOException e) {
            Log.e("Error Loading BMP", "Error loading bitmap!");
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    // we've made a best effort. NOOP ok.
                }
            }
            if (imageBytes != null) {
                try {
                    imageBytes.close();
                } catch (IOException e) {
                    // we've made a best effort. NOOP ok.
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(FlickrImage bitmap) {
        listener.downloadComplete(bitmap);
    }
}

