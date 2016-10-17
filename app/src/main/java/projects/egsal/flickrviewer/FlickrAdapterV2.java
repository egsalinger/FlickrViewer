package projects.egsal.flickrviewer;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by egsal on 8/15/15.
 */
public class FlickrAdapterV2 extends ArrayAdapter {


    private final Context context;
    private final ArrayList<FlickrImageData> data;
    private final ImageLoader loader;
    private final RequestQueue queue;
    private int page = 1;
    private String lastQuery;

    public FlickrAdapterV2(Context context, int layoutResourceId, ArrayList<FlickrImageData>
            theData) {
        super(context, layoutResourceId, theData);
        loader = VolleySingleton.getInstance(context).getImageLoader();
        queue = VolleySingleton.getInstance(context).getRequestQueue();
        this.context = context;
        data = theData;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.network_image, null);
        }
        ((NetworkImageView) convertView).setImageUrl(data.get(position).getThumbnailString(), loader);

        // Slightly convoluted logic here, because Page is 1 indexed, while position is 0 indexed.
        // We'll load more pages iff our position is >= 80 + 100* number of pages loaded after
        // the first. Image caching is handled by our singleton (no more than 40 will be cached).
        // While we *can* get up to ~ 5000 results  total, that'll be no more than 5mb of RAM for
        // our data structure (assuming 1kb/item, which is utterly absurd--memory footprint will be)
        // closer to 500kb tops.
        if (position > page * 100 - 20 && position < 4980) {
            page++;
            runQuery(lastQuery +"&page="+page);
        }

        return convertView;
    }

    public FlickrImageData getDataForItemAtPosition (int position) {
       return data.get(position);
    }

    @Override
    public void clear() {
        super.clear();
        data.clear();
    }

    public void runQuery (String query) {
        lastQuery = query;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList <FlickrImageData> responseData = new ArrayList<>();
                try {
                    JSONObject queryInfo = response.getJSONObject("photos");
                    JSONArray photos = queryInfo.getJSONArray("photo");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photo = photos.getJSONObject(i);
                        String farm = photo.getString("farm");
                        String server = photo.getString("server");
                        String secret = photo.getString("secret");
                        String id = photo.getString("id");
                        String baseString = String.format("https://farm%s.staticflickr.com/%s/%s_%s", farm, server, id, secret);
                        responseData.add(new FlickrImageData(baseString, id, secret));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    addAll(responseData);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Run Query", error.toString());
            }
        });
        queue.add(request);
    }

}
