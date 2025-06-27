package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.BeerCsvRecord;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("default")
public class BeerCsvServiceImplTest {


    BeerCsvServiceImpl subject = new BeerCsvServiceImpl();

    @Test
    void testCsvConverting() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csv/beers.csv");

        List<BeerCsvRecord> result = subject.convertCsv(file);

        assertThat(result.size()).isEqualTo(2410);
    }

}
