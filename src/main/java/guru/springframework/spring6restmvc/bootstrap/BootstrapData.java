package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerCsvRecord;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import guru.springframework.spring6restmvc.service.BeerCsvService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerCsvService beerCsvService;

    @Override
    public void run(String... args) {
        Beer beer1 = Beer.builder()
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .build();

        Beer beer2 = Beer.builder()
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();

        Beer beer3 = Beer.builder()
                .beerName("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("12356")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .build();

        beerRepository.saveAll(List.of(beer1, beer2, beer3));

        Customer customer1 = Customer.builder()
                .customerName("Customer1")
                .age(23)
                .version(0)
                .createdDate(LocalDateTime.now())
                .lastModifiedDat(LocalDateTime.now())
                .build();

        Customer customer2 = Customer.builder()
                .customerName("Customer2")
                .age(30)
                .version(0)
                .createdDate(LocalDateTime.now())
                .lastModifiedDat(LocalDateTime.now())
                .build();

        Customer customer3 = Customer.builder()
                .customerName("Customer3")
                .version(0)
                .createdDate(LocalDateTime.now())
                .lastModifiedDat(LocalDateTime.now())
                .build();

        customerRepository.saveAll(List.of(customer1, customer2, customer3));

        loadCsvData();
    }

    private void loadCsvData() {
        File file;
        try {
            file = ResourceUtils.getFile("classpath:csv/beers.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<BeerCsvRecord> csvRecords = beerCsvService.convertCsv(file);

        List<Beer> beers = csvRecords.stream().map(record -> {
            BeerStyle style = switch (record.getStyle()) {
                case "American Pale Lager" -> BeerStyle.LAGER;
                case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                        BeerStyle.ALE;
                case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                case "American Porter" -> BeerStyle.PORTER;
                case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                case "English Pale Ale" -> BeerStyle.PALE_ALE;
                default -> BeerStyle.PILSNER;
            };

            return Beer.builder()
                    .beerName(StringUtils.abbreviate(record.getBeer(), 50))
                    .beerStyle(style)
                    .price(BigDecimal.TEN)
                    .upc(record.getRow().toString())
                    .quantityOnHand(record.getCount())
                    .build();
        }).toList();

        beerRepository.saveAll(beers);
    }
}
