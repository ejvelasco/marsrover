package marsrover.client;

import org.springframework.web.client.RestTemplate;

import org.springframework.stereotype.Component;

import marsrover.beans.ImageList;

@Component
public class NasaClient {
    RestTemplate restTemplate = new RestTemplate();
    String baseUrl = "https://api.nasa.gov/mars-photos/api/v1/";
    String apiKey = "AUsFDEFo5watSQ0tTOCbMMg1fnFS0kJUnuEjLK4x";

    public ImageList getImages(String rover, String date) {
        // TODO: default values
        // TODO: format date
        String url = this.baseUrl + "rovers/" + rover + "/photos?earth_date=" + date + "&api_key=" + this.apiKey;
        return restTemplate.getForObject(url, ImageList.class);
    }
}