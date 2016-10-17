package projects.egsal.flickrviewer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Eric Salinger on 10/15/2016.
 *
 * This class is intended to replace FlickrActivity as a main entry point into the application. A
 * core difference between FlickrActivity and FlickrActivityV2 is that FlickrActivityV2 uses Volley,
 * something I've been meaning to try out for a while.
 */

public class FlickrActivityV2 extends AppCompatActivity {

    // All we'll need to do with this string is add our (url encoded) text.
    private final String baseURL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=6343a66eb46c461c91934e8a7a981056&format=json&nojsoncallback=1&text=";

    // can't be final, made in onCreate.
    private FlickrAdapterV2 adapter;
    private String lastQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_view);
        VolleySingleton volley = VolleySingleton.getInstance(this);
        // Create an adapter for Flickr.
        adapter = new FlickrAdapterV2(this, 0, new ArrayList<FlickrImageData>());
        // Parse Flickr's feed. I link the feed parser directly up to the adapter, but I could
        // use the activity as the central controller for all of this work.
        lastQuery = "";

        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // This application does not download files to local storage (although it easily could).
            // Therefore, we need to store the URLs that the items come from so that on click we can
            // grab them.
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                FlickrImageData data = adapter.getDataForItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(FlickrActivityV2.this, ImageActivityV2.class);
                if (data != null) {
                    //TODO: Do logic to find out if we can get original image here.
                    intent.putExtra("image", data.getLargeImageString());
                    intent.putExtra("id", data.getId());
                }
                //Start details activity
                startActivity(intent);
            }
        });
    }

    @Override
    // We're just handling the searches here.
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d(intent.getAction(), "onNewIntent ");
            handleSearch(intent);
        } else {
            Log.d(intent.getAction(), "onNewIntent ");
            super.onNewIntent(intent);
        }
    }


    // Process the new search.
    private void handleSearch(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        parseWithQuery(query);
    }

    private void parseWithQuery(String query) {
        // we want to keep query both formatted and unformatted.
        StringBuilder theQuery = new StringBuilder(baseURL);
        try {
            theQuery.append(URLEncoder.encode(query, "UTF-8"));
            Log.d("Handle search", theQuery.toString());
            adapter.clear();
            runQuery (theQuery.toString());
        } catch (Exception e) {
            Log.e("handleSearch", "Problem Searching for " + theQuery);
            e.printStackTrace();
        }
        finally {
            lastQuery = query;
        }
    }

    private void runQuery(final String theQuery) {
        adapter.runQuery(theQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flickr_view_v2, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        Log.d("Item " + id, "onOptionsItemSelected ");
        if (id == R.id.action_reset) {
            parseWithQuery("");
        }
        if (id == R.id.action_refresh) {
            parseWithQuery(lastQuery);
        }
        return super.onOptionsItemSelected(item);
    }
}
