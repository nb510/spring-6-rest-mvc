package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.mappers.BeerOrderShipmentMapper;
import guru.springframework.spring6restmvc.util.PageableUtil;
import jakarta.persistence.EntityManager;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("default")
class BeerOrderRepositoryTest {
    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    BeerOrderMapper beerOrderMapper;
    @Autowired
    BeerOrderShipmentMapper beerOrderShipmentMapper;

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
                .paymentAmount(BigDecimal.valueOf(101231661, 99_999))
                .build();

        BeerOrder savedOrder = beerOrderRepository.saveAndFlush(order);

        System.out.println(savedOrder.getCustomerRef());
        assertThat(savedOrder.getPaymentAmount().equals(order.getPaymentAmount()));
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

    @Test
    void test1() {
        Page<BeerOrder> orders = beerOrderRepository.findAll(PageableUtil.buildPageable(0, 10));

        BeerOrder order = orders.getContent().getFirst();
        BeerOrderShipment shipment = order.getBeerOrderShipment();

        assertDoesNotThrow(() -> {
            shipment.getId();
        });

        assertThrows(LazyInitializationException.class, () -> {
            shipment.getTrackingNumber();
        });
    }

    @Test
    void test2() {
        List<BeerOrder> orders = beerOrderRepository.findAll();

        BeerOrder order = orders.get(0);

        assertThrows(LazyInitializationException.class, () -> {
            order.getBeerOrderLines().iterator().next().getOrderQuantity();
        });
    }

    @Test
    void test3() {
        List<BeerOrder> orders = beerOrderRepository.findAll();

        BeerOrder order = orders.get(0);

        assertThrows(LazyInitializationException.class, () -> {
            beerOrderMapper.toDtoFull(order);
        });
    }

    @Test
    void testPaymentAmount() {

    }

}