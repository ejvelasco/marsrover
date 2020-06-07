package marsrover.endpoints;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import marsrover.client.NasaClient;

@RestController
@RequestMapping("api")
public class ImageEndpoint {
    @Autowired
    private NasaClient nasaClient;

    @RequestMapping(value = "/rovers/{rover}/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImgUrl(@PathVariable String rover, @RequestParam(value = "date") String date) {
        try {
            File file = nasaClient.getImage(rover, date);
            byte[] bytes = Files.readAllBytes(file.toPath());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}