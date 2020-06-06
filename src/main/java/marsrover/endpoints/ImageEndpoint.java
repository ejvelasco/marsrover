package marsrover.endpoints;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import marsrover.beans.ImageList;
import marsrover.client.NasaClient;

@RestController
@RequestMapping("api/v1")
public class ImageEndpoint {
    @Autowired
    private NasaClient nasaClient;

    @RequestMapping(value = "/rovers/{rover}/images", method = RequestMethod.GET)
    public ImageList getRoverImages(@PathVariable String rover, @RequestParam(value = "date") String date) {
        return nasaClient.getImages(rover, date);
    }
}