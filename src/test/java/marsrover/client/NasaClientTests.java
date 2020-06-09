package marsrover.client;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class NasaClientTests {
    @Autowired
    private NasaClient nasaClient;
    private final String[] dates = { "02/27/17", "June 2, 2018", "Jul-13-2016" };
    private final String[] nasaDates = { "2017-02-27", "2018-06-02", "2016-07-13" };
    private final String[] badDates = { "2018-04-31", "06 04 1994", "Saturday, June 4th, 1994" };
    private final String rover = "curiosity";

    @Test
    void TestDateToNasaFormat() throws Exception {
        for (int i = 0; i < dates.length; i++) {
            Assertions.assertEquals(nasaDates[i], nasaClient.dateToNasaFormat(dates[i], 0));
        }

        for (String date : badDates) {
            Assertions.assertThrows(DateFormatException.class, () -> nasaClient.dateToNasaFormat(date, 0));
        }
    }

    @Test
    void TestGetImage() throws Exception {
        for (String date : dates) {
            File image = nasaClient.getImage(rover, date);
            Assertions.assertEquals(true, image.isFile());
        }

        for (String date : badDates) {
            Assertions.assertThrows(Exception.class, () -> nasaClient.getImage(rover, date));
        }
    }
}
