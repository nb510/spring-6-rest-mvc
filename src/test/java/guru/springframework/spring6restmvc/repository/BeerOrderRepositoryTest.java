package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
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

}