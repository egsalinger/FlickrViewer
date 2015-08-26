package projects.egsal.flickrviewer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FlickrActivity extends AppCompatActivity {

    private static final String FEED_URL = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&tags=";
    private FlickrAdapter adapter;
    private FlickrAPIParser parser;
    private String lastQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_view);
        // Create an adapter for Flickr.
        adapter = new FlickrAdapter(this, 0, new ArrayList<Bitmap>());
        // Parse Flickr's feed. I link the feed parser directly up to the adapter, but I could
        // use the activity as the central controller for all of this work.
        parser = new FlickrAPIParser(adapter);
        parser.execute(FEED_URL);
        lastQuery = "";
        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // This application does not download files to local storage (although it easily could).
            // Therefore, we need to store the URLs that the items come from so that on click we can
            // grab them.
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                URL theURL = adapter.getURLForItemAtPosition(position);

                //Create intent
                Intent intent = new Intent(FlickrActivity.this, ImageActivity.class);
                if (theURL != null) {
                    intent.putExtra("image", theURL.toString());
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
        String theQuery = query;
        try {
            // Format for Flickr
            theQuery.replace(" ", ",");
            theQuery = URLEncoder.encode(query, "UTF-8");
            Log.d("Handle search", theQuery);
            adapter.clear();
            parser = new FlickrAPIParser(adapter);
            parser.execute(FEED_URL + theQuery);
        } catch (Exception e) {
            Log.e("handleSearch", "Problem Searching for " + theQuery);
            e.printStackTrace();
        }
        lastQuery = query;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flickr_view, menu);


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
