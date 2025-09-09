package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvcapi.event.OrderPlacedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static guru.springframework.spring6restmvc.configuration.KafkaConfig.ORDER_PLACED_TOPIC;

@AllArgsConstructor
@Slf4j
@Component
public class OrderPlacedEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    @EventListener
    public void onEvent(OrderPlacedEvent event) {
        kafkaTemplate.send(ORDER_PLACED_TOPIC, event);
    }
}
