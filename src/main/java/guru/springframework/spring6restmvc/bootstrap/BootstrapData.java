package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.*;
import guru.springframework.spring6restmvc.model.BeerCsvRecord;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repository.*;
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
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderShipmentRepository beerOrderShipmentRepository;
    private final BeerOrderLineRepository beerOrderLineRepository;

    @Override
    public void run(String... args) {
        if (!beerRepository.findAll().isEmpty()) {
            return;
        }

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
        loadOrders();
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

    private void loadOrders() {
        List<Beer> beers = beerRepository.findAll();
        List<Customer> customers = customerRepository.findAll();

        // first order
        BeerOrder order1 = BeerOrder.builder()
                .customer(customers.get(0))
                .build();

        order1 = beerOrderRepository.save(order1);

        BeerOrderLine orderLine1 = BeerOrderLine.builder()
                .beerOrder(order1)
                .beer(beers.get(0))
                .orderQuantity(10)
                .quantityAllocated(10)
                .status(BeerOrderLineStatus.NEW)
                .build();

        BeerOrderLine orderLine2 = BeerOrderLine.builder()
                .beerOrder(order1)
                .beer(beers.get(1))
                .orderQuantity(12)
                .quantityAllocated(12)
                .status(BeerOrderLineStatus.COMPLETE)
                .build();

        BeerOrderShipment orderShipment1 = BeerOrderShipment.builder()
                .trackingNumber("100-234-26426")
                .build();
        order1.setBeerOrderShipment(orderShipment1);

        beerOrderShipmentRepository.save(orderShipment1);
        beerOrderRepository.save(order1);
        beerOrderLineRepository.saveAll(List.of(orderLine1, orderLine2));

        // first order
        BeerOrder order2 = BeerOrder.builder()
                .customer(customers.get(1))
                .build();

        order2 = beerOrderRepository.save(order2);

        BeerOrderLine orderLine4 = BeerOrderLine.builder()
                .beerOrder(order2)
                .beer(beers.get(0))
                .orderQuantity(10)
                .quantityAllocated(10)
                .status(BeerOrderLineStatus.NEW)
                .build();

        BeerOrderLine orderLine5 = BeerOrderLine.builder()
                .beerOrder(order2)
                .beer(beers.get(1))
                .orderQuantity(12)
                .quantityAllocated(12)
                .status(BeerOrderLineStatus.COMPLETE)
                .build();

        BeerOrderShipment orderShipment2 = BeerOrderShipment.builder()
                .trackingNumber("133-000-134134")
                .build();
        order2.setBeerOrderShipment(orderShipment2);

        beerOrderShipmentRepository.save(orderShipment2);
        beerOrderRepository.save(order2);
        beerOrderLineRepository.saveAll(List.of(orderLine4, orderLine5));
    }
}
