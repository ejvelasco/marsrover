package marsrover.client;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.UUID;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import marsrover.beans.ImageList;
import marsrover.beans.Image;

@Component
public class NasaClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "https://api.nasa.gov/mars-photos/api/v1/";
    private final String apiKey = "AUsFDEFo5watSQ0tTOCbMMg1fnFS0kJUnuEjLK4x";
    private final String cacheDir = "cache/";
    private final String nasaPattern = "yyyy-MM-dd";
    private final String[] patterns = { "MM/dd/yy", "MMM dd, yyyy", "MMM-dd-yyyy", "yyyy-MM-dd" };
    private final SimpleDateFormat nasaDateFormat = strictFormat(nasaPattern);

    public ImageList getImages(String rover, String date) {
        // TODO: default values
        // TODO: format date
        String url = this.baseUrl + "rovers/" + rover + "/photos?earth_date=" + date + "&api_key=" + this.apiKey;
        return restTemplate.getForObject(url, ImageList.class);
    }

    public File getImage(String rover, String date) throws Exception {
        try {
            System.out.println(date);
            String nasaDate = dateToNasaFormat(date, 0);
            String id = rover + nasaDate;
            UUID uuid = UUID.nameUUIDFromBytes(id.getBytes());
            String imageFileName = cacheDir + uuid;
            File imageFile = new File(imageFileName);
            if (!imageFile.isFile()) {
                ImageList imageList = getImages(rover, nasaDate);
                Image[] images = imageList.getImages();
                if (images.length > 0) {
                    String source = images[0].getSource();
                    URL url = new URL(source);
                    URLConnection connection = url.openConnection();
                    String redirect = connection.getHeaderField("Location");
                    if (redirect != null) {
                        url = new URL(redirect);
                    }
                    InputStream in = url.openStream();
                    Files.copy(in, imageFile.toPath());
                    in.close();
                } else {
                    // no images for date
                }

            }
            return imageFile;
        } catch (Exception e) {
            throw e;
        }
    }

    public String dateToNasaFormat(String dateLine, int idx) throws Exception {
        if (idx >= patterns.length) {
            throw new Exception("Date format not recognized");
        }
        try {
            SimpleDateFormat lineDateFormat = strictFormat(patterns[idx]);
            Date date = lineDateFormat.parse(dateLine);
            String nasaDate = nasaDateFormat.format(date);
            return nasaDate;
        } catch (ParseException e) {
            return dateToNasaFormat(dateLine, idx + 1);
        }
    }

    public SimpleDateFormat strictFormat(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        return dateFormat;
    }
}