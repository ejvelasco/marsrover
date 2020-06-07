package marsrover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.Scanner;
import javax.annotation.PostConstruct;

import marsrover.client.NasaClient;

@SpringBootApplication
public class MarsroverApplication {
	@Autowired
	private NasaClient nasaClient;

	public static void main(String[] args) {
		SpringApplication.run(MarsroverApplication.class, args);
	}

	@PostConstruct
	public void saveImages() {
		getImagesFromDatesFile("dates.txt");
	}

	private final String fileName = "dates.txt";
	private final String roverName = "curiosity";
	private final int nThreads = 4;
	private final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

	public SimpleDateFormat strictFormat(String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setLenient(false);
		return dateFormat;
	}

	public void getImagesFromDatesFile(String filePath) {
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
			return nasaClient.getImage(roverName, date);
		}
	}
}
