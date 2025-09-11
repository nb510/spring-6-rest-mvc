package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.entities.BeerOrderLine;
import guru.springframework.spring6restmvc.mappers.BeerOrderLineMapper;
import guru.springframework.spring6restmvc.repository.BeerOrderLineRepository;
import guru.springframework.spring6restmvcapi.BeerOrderLineDto;
import guru.springframework.spring6restmvcapi.BeerOrderLineStatus;
import guru.springframework.spring6restmvcapi.event.DrinkPreparedEvent;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("default")
class DrinkPreparedEventListenerTest {

    @Autowired
    DrinkPreparedEventListener drinkPreparedEventListener;

    @Autowired
    BeerOrderLineRepository beerOrderLineRepository;
    @Autowired
    BeerOrderLineMapper beerOrderLineMapper;
    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    public void testEvent() {
        BeerOrderLine orderLine = beerOrderLineRepository.save(
                BeerOrderLine.builder()
                        .orderQuantity(10)
                        .quantityAllocated(null)
                        .build());

        BeerOrderLineDto orderLineDto = beerOrderLineMapper.toDto(orderLine);
        orderLineDto.setQuantityAllocated(10);

        drinkPreparedEventListener.listen(new DrinkPreparedEvent(orderLineDto));

        entityManager.flush();
        entityManager.clear();

        BeerOrderLine foundOrderLine = beerOrderLineRepository.findById(orderLine.getId()).get();
        assertThat(foundOrderLine.getQuantityAllocated()).isEqualTo(orderLineDto.getQuantityAllocated());
        assertThat(foundOrderLine.getStatus()).isEqualTo(BeerOrderLineStatus.COMPLETE);
    }
}