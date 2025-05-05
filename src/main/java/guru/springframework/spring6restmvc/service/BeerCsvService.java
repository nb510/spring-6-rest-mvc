package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.BeerCsvRecord;

import java.io.File;
import java.util.List;

public interface BeerCsvService {

    List<BeerCsvRecord> convertCsv(File csvFile);
}
