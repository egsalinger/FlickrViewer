package projects.egsal.flickrviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

@Deprecated
public class ImageActivity extends AppCompatActivity {

    ImageView theImage;


    // We have one ImageView and a back button here. I could have included some additional fields
    // with more info, but that was not in the spec.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        theImage = (ImageView) findViewById(R.id.full_image);

        String resource = getIntent().getStringExtra("image");
        if (resource == null) {
            Log.d("OnCreate", "resource is null ");
            Toast.makeText(ImageActivity.this, "There was a problem transferring the image URL over. Please hit back and try again.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                BitmapImageLoader load = new BitmapImageLoader(new BitmapDownloadListener() {
                    @Override
                    public void downloadComplete(FlickrImage b) {
                        theImage.setImageBitmap(BitmapFactory.decodeByteArray(b.getImageData(), 0, b.getImageData().length));
                    }
                });
                load.execute(new URL(resource));
            } catch (MalformedURLException e) {
                Toast.makeText(ImageActivity.this, "There was a problem transferring the image URL over. The URL could not be read.", Toast.LENGTH_SHORT).show();
                Log.e("Malformed URL", e.getMessage());
                e.printStackTrace();
            }
        }

    }

}
