package marsrover;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

import marsrover.client.NasaClient;

@SpringBootApplication
public class MarsroverApplication {
	public static void main(String[] args) {
		NasaClient nasaClient = new NasaClient();
		SpringApplication.run(MarsroverApplication.class, args);
		nasaClient.getImagesFromDatesFile();
	}
}
