package marsrover.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.Date;
import java.util.logging.Logger;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import marsrover.beans.Image;
import marsrover.beans.ImageList;

@Component
public class NasaClient {
    // nasa api
    private final String BASE_URL = "https://api.nasa.gov/mars-photos/api/v1/";
    private final String API_KEY = "AUsFDEFo5watSQ0tTOCbMMg1fnFS0kJUnuEjLK4x";
    // image fetching and saving
    private final String NASA_PATTERN = "yyyy-MM-dd";
    private final String CACHE_DIR = "cache/";
    private final String FILE_NAME = "dates.txt";
    private final String ROVER_NAME = "curiosity";
    private final String[] PATTERNS = { "MM/dd/yy", "MMM dd, yyyy", "MMM-dd-yyyy", "yyyy-MM-dd" };
    private SimpleDateFormat nasaDateFormat = strictFormat(NASA_PATTERN);
    private RestTemplate restTemplate = new RestTemplate();
    // concurrency
    private final int N_THREADS = 4;
    private ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
    // logging
    private static Logger logger = Logger.getLogger(NasaClient.class.getName());

    class FetchDateImageTask implements Callable<File> {
        private String date;

        FetchDateImageTask(String date) {
            this.date = date;
        }

        @Override
        public File call() throws Exception {
            return getImage(ROVER_NAME, date);
        }
    }

    public void getImagesFromDatesFile() {
        try {
            // read dates file
            logger.info("Getting images from file: " + FILE_NAME);
            InputStream inputStream = new ClassPathResource(FILE_NAME).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // fetch and save image from date
                Callable<File> task = new FetchDateImageTask(line);
                executorService.submit(task);
            }
            executorService.shutdown();
            reader.close();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public ImageList getImages(String rover, String date) {
        String url = this.BASE_URL + "rovers/" + rover + "/photos?earth_date=" + date + "&api_key=" + this.API_KEY;
        return restTemplate.getForObject(url, ImageList.class);
    }

    public File getImage(String rover, String date) throws DateFormatException, IOException {
        try {
            // format incoming date
            String nasaDate = dateToNasaFormat(date, 0);
            String id = rover + nasaDate;
            // create consistent uuid from rover name and date
            UUID uuid = UUID.nameUUIDFromBytes(id.getBytes());
            String imageFileName = CACHE_DIR + uuid;
            File imageFile = new File(imageFileName);
            // fetch image if it is not in cache
            if (!imageFile.isFile()) {
                logger.info("Getting image for date: " + nasaDate + " and rover: " + rover);
                ImageList imageList = getImages(rover, nasaDate);
                Image[] images = imageList.getImages();
                // check if there are images and use the first one
                if (images.length > 0) {
                    String source = images[0].getSource();
                    URL url = new URL(source);
                    URLConnection connection = url.openConnection();
                    // handle a redirect from the NASA api
                    String redirect = connection.getHeaderField("Location");
                    if (redirect != null) {
                        url = new URL(redirect);
                    }
                    // save image in cache
                    InputStream in = url.openStream();
                    Files.copy(in, imageFile.toPath());
                    in.close();
                } else {
                    logger.info("No images found for date: " + nasaDate + " and rover: " + rover);
                }
            } else {
                logger.info("File found in cache: " + imageFileName);
            }
            return imageFile;
        } catch (DateFormatException | IOException e) {
            logger.severe(e.getMessage());
            throw e;
        }
    }

    public String dateToNasaFormat(String dateLine, int idx) throws DateFormatException {
        if (idx >= PATTERNS.length) {
            throw new DateFormatException("Invalid date: " + dateLine);
        }
        try {
            SimpleDateFormat lineDateFormat = strictFormat(PATTERNS[idx]);
            Date date = lineDateFormat.parse(dateLine);
            String nasaDate = nasaDateFormat.format(date);
            return nasaDate;
        } catch (ParseException e) {
            return dateToNasaFormat(dateLine, idx + 1);
        }
    }

    public SimpleDateFormat strictFormat(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        // throw exception for invalid dates
        dateFormat.setLenient(false);
        return dateFormat;
    }
}