package marsrover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import marsrover.client.NasaClient;
import javax.annotation.PostConstruct;

@SpringBootApplication
public class MarsroverApplication {
	@Autowired
	private NasaClient nasaClient;

	public static void main(String[] args) {
		SpringApplication.run(MarsroverApplication.class, args);
	}

	@PostConstruct
	public void getImages() {
		nasaClient.saveImagesFromFile();
	}

}
