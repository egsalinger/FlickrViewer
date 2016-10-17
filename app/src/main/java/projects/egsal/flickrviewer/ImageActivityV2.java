package projects.egsal.flickrviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageActivityV2 extends AppCompatActivity {

    private NetworkImageView theImage;
    private ImageLoader loader;
    private String downloadUrl;


    // We have one ImageView, a save button, and a and a back button here. I could have included some additional fields
    // with more info, but that was not in the spec.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity_v2);
        loader = VolleySingleton.getInstance(this).getImageLoader();
        theImage = (NetworkImageView) findViewById(R.id.v2_full_image);

        String id = getIntent().getStringExtra("id");
        if (id != null) {
            final String getSizes = "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=6343a66eb46c461c91934e8a7a981056&format=json&nojsoncallback=1&photo_id=" + id;
            RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();

            String displayUrl = getIntent().getStringExtra("image");
            theImage.setImageUrl(displayUrl, loader);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getSizes, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject queryInfo = response.getJSONObject("sizes");
                        JSONArray sizes = queryInfo.getJSONArray("size");
                        for (int i = 0; i < sizes.length(); i++) {
                            JSONObject size = sizes.getJSONObject(i);
                            if (size.getString("label").equals("Large")) {
                                downloadUrl = size.getString("source");
                            } else if (size.getString("label").equals("Original")){
                                // calls finally before returning.
                                downloadUrl = size.getString ("source");
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        // We could use the largest URL (DownloadUrl), but instead of doing that, we'll just use the display url instead.
                        // This is a gamble--the largest URL will be large enough to download, but not necessarily large enough to want to display.

                        Log.e("ImageActivityV2", downloadUrl);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Run Query", error.toString());
                }
            });
            queue.add(request);

        } else {
            Log.d("OnCreate", "resource is null ");
            Toast.makeText(ImageActivityV2.this, "There was a problem transferring the image URL over. Please hit back and try again.", Toast.LENGTH_SHORT).show();
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_v2, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        Log.d("Item " + id, "onOptionsItemSelected ");
        if (id == R.id.action_save) {
            saveImage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage() {


        Toast.makeText(ImageActivityV2.this, "Image Save Started.", Toast.LENGTH_SHORT).show();
        loader.get(downloadUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                try {
                    File outputFile = createImageFile();
                    FileOutputStream fileWriter = new FileOutputStream(outputFile);
                    Bitmap b =  response.getBitmap();
                    if (b == null) {
                        return;
                    }
                    b.compress(Bitmap.CompressFormat.JPEG, 100, fileWriter);
                    fileWriter.close();

                    MediaScannerConnection.scanFile(ImageActivityV2.this,
                            new String[] { outputFile.toString() }, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.d("ExternalStorage", "Scanned " + path + ":");
                                    Log.d("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                    Toast.makeText(ImageActivityV2.this, "Image Save Succeeded.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ImageActivityV2.this, "Image Save Failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ImageActivityV2.this, "Image Save Failed.", Toast.LENGTH_SHORT).show();
            }
        });

        if (theImage.getDrawable() == null) {
            return;
        }

    }

    private File createImageFile() throws IOException {

        // Create an image file name. This allows us to save many copies of the same file.
        // We could use the image URL as our stamp, which would stop us from saving multiple
        // copies of the same image. But maybe our user wants to do that. I know I save images
        // multiple times.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
}
