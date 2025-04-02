package guru.springframework.spring6restmvc.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Test
    void testLogging() {
        System.out.println(beerService.getBeerById(UUID.randomUUID()));
    }
}