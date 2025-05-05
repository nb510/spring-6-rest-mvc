package guru.springframework.spring6restmvc.service;

import com.opencsv.bean.CsvToBeanBuilder;
import guru.springframework.spring6restmvc.model.BeerCsvRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {

    @Override
    public List<BeerCsvRecord> convertCsv(File csvFile) {
        try (var reader = new FileReader(csvFile)) {
            return new CsvToBeanBuilder<BeerCsvRecord>(reader)
                    .withType(BeerCsvRecord.class)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
