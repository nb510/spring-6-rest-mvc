package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.configuration.KafkaConfig;
import guru.springframework.spring6restmvcapi.BeerOrderDto;
import guru.springframework.spring6restmvcapi.BeerOrderLineDto;
import guru.springframework.spring6restmvcapi.event.DrinkRequestEvent;
import guru.springframework.spring6restmvcapi.event.OrderPlacedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static guru.springframework.spring6restmvc.configuration.KafkaConfig.*;

@Component
@AllArgsConstructor
@Slf4j
public class DrinkSplitterRouter {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(groupId = "DrinkSplitterRouter", topics = KafkaConfig.ORDER_PLACED_TOPIC)
    public void receive(@Payload OrderPlacedEvent orderPlacedEvent) {
        BeerOrderDto orderDto = orderPlacedEvent.getBeerOrderDto();
        
        if (orderDto == null || CollectionUtils.isEmpty(orderDto.getBeerOrderLines())) {
            log.error("Order Placed Event cannot be processed due to the corrupted data");
            return;
        }

        orderDto.getBeerOrderLines().forEach(orderLine -> {
            switch (orderLine.getBeer().getBeerStyle()) {
                case LAGER:
                    log.debug("Splitting LAGER Order");
                    sendToIceColdTopic(orderLine);
                    break;
                case STOUT:
                    log.debug("Splitting STOUT Order");
                    sendToColdTopic(orderLine);
                    break;
                case GOSE:
                    log.debug("Splitting Gose Order");
                    sendToCoolTopic(orderLine);
                    break;
                case PORTER:
                    log.debug("Splitting PORTER Order");
                    sendToColdTopic(orderLine);
                    break;
                case ALE:
                    log.debug("Splitting ALE Order");
                    sendToColdTopic(orderLine);
                    break;
                case WHEAT:
                    log.debug("Splitting WHEAT Order");
                    sendToCoolTopic(orderLine);
                    break;
                case IPA:
                    log.debug("Splitting IPA Order");
                    sendToColdTopic(orderLine);
                    break;
                case PALE_ALE:
                    log.debug("Splitting PALE_ALE Order");
                    sendToColdTopic(orderLine);
                    break;
                case SAISON:
                    log.debug("Splitting SAISON Order");
                    sendToIceColdTopic(orderLine);
                    break;
                default:
                    log.error("Beer style is unknown");
                    break;
            }
        });
    }
    
    private void sendToIceColdTopic(BeerOrderLineDto beerOrderLineDto) {
        kafkaTemplate.send(DRINK_REQUEST_ICE_COLD_TOPIC, new DrinkRequestEvent(beerOrderLineDto));
    }

    private void sendToColdTopic(BeerOrderLineDto beerOrderLineDto) {
        kafkaTemplate.send(DRINK_REQUEST_COLD_TOPIC, new DrinkRequestEvent(beerOrderLineDto));
    }

    private void sendToCoolTopic(BeerOrderLineDto beerOrderLineDto) {
        kafkaTemplate.send(DRINK_REQUEST_COOL_TOPIC, new DrinkRequestEvent(beerOrderLineDto));
    }
}
