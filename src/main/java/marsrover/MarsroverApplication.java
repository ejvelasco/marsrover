package marsrover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Scanner;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import org.springframework.core.io.ClassPathResource;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import marsrover.client.NasaClient;
import marsrover.beans.ImageList;
import marsrover.beans.Image;
import org.springframework.beans.factory.annotation.Autowired;

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

	private final String[] patterns = { "MM/dd/yy", "MMM dd, yyyy", "MMM-dd-yyyy" };
	private final String fileName = "dates.txt";
	private final String nasaPattern = "yyyy-MM-dd";
	private final SimpleDateFormat nasaDateFormat = strictFormat(nasaPattern);
	private final int nThreads = 10;
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
				Callable<String> task = new FetchDateImageTask(scanner);
				Future<String> future = executorService.submit(task);
				System.out.println(future.get());
			}
			executorService.shutdown();
			scanner.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	class FetchDateImageTask implements Callable<String> {
		private final Scanner scanner;

		FetchDateImageTask(Scanner scanner) {
			this.scanner = scanner;
		}

		@Override
		public String call() throws Exception {
			String dateLine = scanner.nextLine();
			String date = dateToNasaFormat(dateLine, 0);
			ImageList imageList = nasaClient.getImages("curiosity", date);
			Image[] images = imageList.getImages();
			if (images.length > 0) {
				return images[0].getSource();
			}
			return "";
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
}
