package marsrover.client;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import marsrover.beans.Image;
import marsrover.beans.ImageList;

@Component
public class NasaClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "https://api.nasa.gov/mars-photos/api/v1/";
    private final String apiKey = "AUsFDEFo5watSQ0tTOCbMMg1fnFS0kJUnuEjLK4x";
    private final String cacheDir = "cache/";
    private final String nasaPattern = "yyyy-MM-dd";
    private final String[] patterns = { "MM/dd/yy", "MMM dd, yyyy", "MMM-dd-yyyy", "yyyy-MM-dd" };
    private final SimpleDateFormat nasaDateFormat = strictFormat(nasaPattern);
    private final String fileName = "dates.txt";
    private final String roverName = "curiosity";
    private final int nThreads = 4;
    private final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    @PostConstruct
    public void getImagesFromDatesFile() {
        try {
            File file = new ClassPathResource(fileName).getFile();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                Callable<File> task = new FetchDateImageTask(scanner);
                Future<File> future = executorService.submit(task);
                future.get();
            }
            executorService.shutdown();
            scanner.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    class FetchDateImageTask implements Callable<File> {
        private final Scanner scanner;

        FetchDateImageTask(Scanner scanner) {
            this.scanner = scanner;
        }

        @Override
        public File call() throws Exception {
            String date = scanner.nextLine();
            return getImage(roverName, date);
        }
    }

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