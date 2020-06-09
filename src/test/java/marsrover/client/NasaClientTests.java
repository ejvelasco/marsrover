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
    private final String[] DATES = { "02/27/17", "June 2, 2018", "Jul-13-2016" };
    private final String[] NASA_DATES = { "2017-02-27", "2018-06-02", "2016-07-13" };
    private final String[] BAD_DATES = { "2018-04-31", "06 04 1994", "Saturday, June 4th, 1994" };
    private final String ROVER = "curiosity";

    @Test
    void TestDateToNasaFormat() throws Exception {
        for (int i = 0; i < DATES.length; i++) {
            Assertions.assertEquals(NASA_DATES[i], nasaClient.dateToNasaFormat(DATES[i], 0));
        }

        for (String date : BAD_DATES) {
            Assertions.assertThrows(DateFormatException.class, () -> nasaClient.dateToNasaFormat(date, 0));
        }
    }

    @Test
    void TestGetImage() throws Exception {
        for (String date : DATES) {
            File image = nasaClient.getImage(ROVER, date);
            Assertions.assertEquals(true, image.isFile());
        }

        for (String date : BAD_DATES) {
            Assertions.assertThrows(Exception.class, () -> nasaClient.getImage(ROVER, date));
        }
    }
}
