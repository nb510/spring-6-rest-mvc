package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.configuration.KafkaConfig;
import guru.springframework.spring6restmvcapi.event.DrinkRequestEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DrinkListenerKafkaConsumer {
    public AtomicInteger iceColdMessageCount = new AtomicInteger(0);
    public AtomicInteger coldMessageCount = new AtomicInteger(0);
    public AtomicInteger coolMessageCount = new AtomicInteger(0);

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.DRINK_REQUEST_ICE_COLD_TOPIC)
    public void receiveIceCold(@Payload DrinkRequestEvent event) {
        iceColdMessageCount.incrementAndGet();
    }

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.DRINK_REQUEST_COLD_TOPIC)
    public void receiveCold(@Payload DrinkRequestEvent event) {
        coldMessageCount.incrementAndGet();
    }

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.DRINK_REQUEST_COOL_TOPIC)
    public void receive(@Payload DrinkRequestEvent event) {
        coolMessageCount.incrementAndGet();
    }
}
