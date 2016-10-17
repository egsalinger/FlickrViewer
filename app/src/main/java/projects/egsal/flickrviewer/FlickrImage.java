package projects.egsal.flickrviewer;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.net.URL;

/**
 * Created by Eric Salinger on 10/16/2016.
 */

@Deprecated
public class FlickrImage {
    private final byte[] imageData;
    private final URL url;

    public FlickrImage(byte [] data, URL theUrl) {
        url = theUrl;
        imageData = data;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof FlickrImage &&
                TextUtils.equals(((FlickrImage) object).getUrl().toString(), getUrl().toString()));
    }

    public URL getUrl() {
        return url;
    }

    public byte[] getImageData() {
        return imageData;
    }
}
