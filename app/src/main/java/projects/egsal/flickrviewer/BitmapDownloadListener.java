package projects.egsal.flickrviewer;

import android.graphics.Bitmap;

import java.net.URL;

/**
 * Created by Eric Salinger on 8/18/15.
 */
@Deprecated
public interface BitmapDownloadListener {

    /**
     * This function gets called when the download is complete. It returns a FlickrImage object.
     *
     * NOTE: For caching purposes (nothing is cached currently, but caches could be created in later
     * versions), the FULL byte array of the image at the URL is downloaded. If you want to save
     * memory, you must compress the bitmap manually when displaying it.
     * @param imageData the download image data (bitmap and URL).
     */
    void downloadComplete(FlickrImage imageData);
}
