package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.configuration.KafkaConfig;
import guru.springframework.spring6restmvcapi.event.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@ActiveProfiles("default")
public class OrderPlacedKafkaListener {

    public AtomicInteger counter = new AtomicInteger(0);

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.ORDER_PLACED_TOPIC)
    public void receive(OrderPlacedEvent orderPlacedEvent) {
        System.out.println("Message from Kafka received");
        counter.incrementAndGet();
    }
}
