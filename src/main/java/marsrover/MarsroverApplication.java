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
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.UUID;
import java.nio.file.Paths;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.net.URL;
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
	private final String nasaImageExtension = ".jpeg";
	private final String roverName = "curiosity";
	private final String cacheDir = "cache/";
	private final SimpleDateFormat nasaDateFormat = strictFormat(nasaPattern);
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
				// Callable<String> task = new FetchDateImageTask(scanner);
				// Future<String> future = executorService.submit(task);
				// System.out.println(future.get());
				String dateLine = scanner.nextLine();
				String date = dateToNasaFormat(dateLine, 0);
				UUID dateUUID = UUID.nameUUIDFromBytes(date.getBytes());
				String imageFileName = cacheDir + dateUUID + nasaImageExtension;
				File imageFile = new File(imageFileName);
				if (!imageFile.isFile()) {
					ImageList imageList = nasaClient.getImages(roverName, date);
					Image[] images = imageList.getImages();
					if (images.length > 0) {
						String url = images[0].getSource();
						InputStream in = new URL(url).openStream();
						Files.copy(in, imageFile.toPath());
						in.close();
					} else {
						// no images for date
					}
				}
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
			UUID dateUUID = UUID.nameUUIDFromBytes(date.getBytes());
			String imageFileName = cacheDir + dateUUID + nasaImageExtension;
			File imageFile = new File(imageFileName);
			if (!imageFile.isFile()) {
				ImageList imageList = nasaClient.getImages(roverName, date);
				Image[] images = imageList.getImages();
				if (images.length > 0) {
					String url = images[0].getSource();
					InputStream in = new URL(url).openStream();
					Files.copy(in, imageFile.toPath());
					in.close();
				} else {
					// no images for date
				}
			}
			return imageFileName;
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
