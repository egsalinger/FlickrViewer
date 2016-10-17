package projects.egsal.flickrviewer;

/**
 * Created by Eric Salinger on 10/15/2016.
 */

public class FlickrImageData {
    private final String baseURLString;
    private final String id;
    private final String secret;
    private final String thumbnail;
    private final String large;
    // TODO: Figure out how to get original images.

    public FlickrImageData (String baseURL, String theId, String theSecret){
        baseURLString = baseURL;
        thumbnail = baseURL + "_m.jpg";
        large = baseURL + "_b.jpg";
        id = theId;
        secret = theSecret;
    }

    public String getSecret() {
        return secret;
    }

    public String getId() {
        return id;
    }

    public String getBaseURLString() {
        return baseURLString;
    }

    public String getThumbnailString() {
        return thumbnail;
    }

    public String getLargeImageString() {
        return large;
    }
}
