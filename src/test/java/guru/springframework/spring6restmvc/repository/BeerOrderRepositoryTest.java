package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
import guru.springframework.spring6restmvc.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("default")
class BeerOrderRepositoryTest {
    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;
    @Autowired
    CustomerRepository customerRepository;

    Customer testCustomer;

    @BeforeEach
    void setup() {
        testCustomer = customerRepository.findAll().getFirst();
    }

    @Test
    @Transactional
    void testCreateBeerOrder() {
        BeerOrder order = BeerOrder.builder()
                .customer(testCustomer)
                .customerRef("Test order")
                .build();

        BeerOrder savedOrder = beerOrderRepository.saveAndFlush(order);

        System.out.println(savedOrder.getCustomerRef());
    }

    @Test
    @Transactional
    void testCascade() {
        BeerOrder order = BeerOrder.builder()
                .beerOrderShipment(BeerOrderShipment
                        .builder()
                        .trackingNumber("123")
                        .build()
                )
                .build();

        BeerOrder savedOrder = beerOrderRepository.saveAndFlush(order);

        assertThat(savedOrder.getBeerOrderShipment().getId()).isNotNull();
        assertThat(savedOrder.getBeerOrderShipment().getBeerOrder()).isEqualTo(savedOrder);
    }

}