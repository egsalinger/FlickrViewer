package projects.egsal.flickrviewer;

import android.graphics.Bitmap;

import java.net.URL;

/**
 * Created by egsal on 8/18/15.
 */
public interface BitmapDownloadListener {

    public void downloadComplete(Bitmap b, URL from);
}
