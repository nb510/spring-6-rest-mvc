package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.entities.BeerOrderLine;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.repository.BeerOrderLineRepository;
import guru.springframework.spring6restmvcapi.BeerOrderLineDto;
import guru.springframework.spring6restmvcapi.BeerOrderLineStatus;
import guru.springframework.spring6restmvcapi.event.DrinkPreparedEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static guru.springframework.spring6restmvc.configuration.KafkaConfig.DRINK_PREPARED_TOPIC;

@Component
@AllArgsConstructor
public class DrinkPreparedEventListener {

    private final BeerOrderLineRepository beerOrderLineRepository;

    @KafkaListener(groupId = "DrinkPrepared", topics = DRINK_PREPARED_TOPIC)
    public void listen(DrinkPreparedEvent event) {
        BeerOrderLineDto orderLineDto = event.getBeerOrderLineDTO();
        BeerOrderLine orderLine = beerOrderLineRepository.findById(orderLineDto.getId())
                .orElseThrow(NotFoundException::new);

        orderLine.setQuantityAllocated(orderLineDto.getQuantityAllocated());
        orderLine.setStatus(BeerOrderLineStatus.COMPLETE);

        beerOrderLineRepository.save(orderLine);
    }
}
