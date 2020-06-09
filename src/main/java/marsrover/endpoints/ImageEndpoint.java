package marsrover.endpoints;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
import marsrover.client.DateFormatException;

@RestController
@RequestMapping("api")
public class ImageEndpoint {
    @Autowired
    private NasaClient nasaClient;

    @RequestMapping(value = "/rovers/{rover}/images", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable String rover, @RequestParam(value = "date") String date) {
        try {
            String decodedDate = URLDecoder.decode(date, StandardCharsets.UTF_8);
            File file = nasaClient.getImage(rover, decodedDate);
            byte[] bytes = new byte[0];
            if (file.isFile()) {
                bytes = Files.readAllBytes(file.toPath());
            }
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
        } catch (DateFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}