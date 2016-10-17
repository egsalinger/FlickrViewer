package projects.egsal.flickrviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by egsal on 8/17/15.
 */
@Deprecated
public class FlickrAdapter extends ArrayAdapter {


    private final Context context;
    private final ArrayList<FlickrImage> data;
    private final int squareDimension;

    public FlickrAdapter(Context context, int layoutResourceId, ArrayList<FlickrImage> theData) {
        super(context, layoutResourceId, theData);
        this.context = context;
        data = theData;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        squareDimension = (p.x - 6 * 4) / 4;
    }

    public void loadBitmapFromURL(URL newURL) {
        BitmapImageLoader loader = new BitmapImageLoader(new BitmapDownloadListener() {
            @Override
            public void downloadComplete(FlickrImage b) {
                addBitmapWithURL(b);
            }
        });
        loader.execute(newURL);
    }

    private int calculateSampleSize(BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        // We're scaling down to the largest dimensions bigger than our square.
        // It's possible the image won't be a square, in which case we want to do this.
        if (height > squareDimension && width > squareDimension) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= squareDimension
                    && (halfWidth / inSampleSize) >= squareDimension) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Don't need to synchronize this because this is only called by one thread.
    public void addBitmapWithURL(FlickrImage image) {
        data.add(image);
        this.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cropped_image, null);
        }
        Bitmap item = getBitmapFrom(position);
        if (item != null) {
            ((ImageView) convertView).setImageBitmap(item);
        }
        return convertView;
    }

    private Bitmap getBitmapFrom(int position) {
        if ( data.size() < position){
            return null;
        }
        // This is slightly costly to the user.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        final byte [] bytes = data.get(position).getImageData();
        // get the image dimensions only.
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        // Now, calculate our sample size.
        options.inSampleSize = calculateSampleSize(options);
        options.inJustDecodeBounds = false;
        // Finally, get full sized image.
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public URL getURLForItemAtPosition(int position) {
        if (data.size() > position) {
            return data.get(position).getUrl();
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        super.clear();
        data.clear();
    }


}
