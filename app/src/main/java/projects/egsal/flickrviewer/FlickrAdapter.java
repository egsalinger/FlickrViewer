package projects.egsal.flickrviewer;

import android.content.Context;
import android.graphics.Bitmap;
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
public class FlickrAdapter extends ArrayAdapter {


    private Context context;
    private ArrayList<Bitmap> data;
    private ArrayList<URL> fullAddresses;
    private int squareDimension;

    public FlickrAdapter(Context context, int layoutResourceId, ArrayList<Bitmap> theData) {
        super(context, layoutResourceId, theData);
        this.context = context;
        data = theData;
        fullAddresses = new ArrayList<URL>();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        squareDimension = (p.x - 6 * 4) / 4;
    }

    public void loadBitmapFromURL(URL newURL) {
        BitmapImageLoader loader = new BitmapImageLoader(new BitmapDownloadListener() {
            @Override
            public void downloadComplete(Bitmap b, URL from) {
                // This is slightly costly to the user. We have to download the image first and
                // scale it down second, unless the compiler somehow optimizes this for us (which is
                // not really what you want to rely on).
                Bitmap bitmap = Bitmap.createScaledBitmap(b, squareDimension, squareDimension, false);
                addBitmapWithURL(from, bitmap);
            }
        });
        loader.execute(newURL);
    }

    // Don't need to synchronize this because this is only called by one thread.
    public void addBitmapWithURL(URL url, Bitmap bitmap) {
        data.add(bitmap);
        fullAddresses.add(url);
        this.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cropped_image, null);
        }
        Bitmap item = data.get(position);
        if (item != null) {
            ((ImageView) convertView).setImageBitmap(item);
        }
        return convertView;
    }

    public URL getURLForItemAtPosition(int position) {
        if (fullAddresses.size() > position) {
            return fullAddresses.get(position);
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        super.clear();
        fullAddresses.clear();
        data.clear();
    }


}
