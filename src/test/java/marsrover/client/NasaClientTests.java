package marsrover.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class MarsroverApplicationTests {
    @Autowired
    private NasaClient nasaClient;

    @Test
    void TestDateToNasaFormat() throws Exception {
        Assertions.assertEquals("2017-02-27", nasaClient.dateToNasaFormat("02/27/17", 0));
        Assertions.assertEquals("2018-06-02", nasaClient.dateToNasaFormat("June 2, 2018", 0));
        Assertions.assertEquals("2016-07-13", nasaClient.dateToNasaFormat("Jul-13-2016", 0));

        Assertions.assertThrows(Exception.class, () -> nasaClient.dateToNasaFormat("2018-04-31", 0));
        Assertions.assertThrows(Exception.class, () -> nasaClient.dateToNasaFormat("06 04 1994", 0));
        Assertions.assertThrows(Exception.class, () -> nasaClient.dateToNasaFormat("Saturday, June 4th, 1994", 0));
    }
}
