package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.configuration.KafkaConfig;
import guru.springframework.spring6restmvcapi.BeerDto;
import guru.springframework.spring6restmvcapi.BeerOrderDto;
import guru.springframework.spring6restmvcapi.BeerOrderLineDto;
import guru.springframework.spring6restmvcapi.BeerStyle;
import guru.springframework.spring6restmvcapi.event.OrderPlacedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EmbeddedKafka(controlledShutdown = true, topics = {KafkaConfig.ORDER_PLACED_TOPIC}, partitions = 1, kraft = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderPlacedEventListenerTest {

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    OrderPlacedEventListener orderPlacedEventListener;
    @Autowired
    DrinkSplitterRouter drinkSplitterRouter;

    @Autowired
    OrderPlacedKafkaListener orderPlacedKafkaListener;
    @Autowired
    DrinkListenerKafkaConsumer drinkListenerKafkaConsumer;

    @BeforeEach
    void setUp() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
            ContainerTestUtils.waitForAssignment(container, 1);
        });
    }

    @Test
    void testKafka() {
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .beerOrderDto(BeerOrderDto.builder().id(UUID.randomUUID()).build())
                .build();

        orderPlacedEventListener.onEvent(event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals(1, orderPlacedKafkaListener.counter.get())
        );
    }

    @Test
    void testDrinkSplitter() {
        drinkSplitterRouter.receive(new OrderPlacedEvent(getOrder()));

        await().atMost(15, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(drinkListenerKafkaConsumer.iceColdMessageCount::get, greaterThan(0));

        await().atMost(15, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(drinkListenerKafkaConsumer.coldMessageCount::get, greaterThan(0));

        await().atMost(15, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(drinkListenerKafkaConsumer.coolMessageCount::get, greaterThan(0));
    }

    private BeerOrderDto getOrder() {
        BeerOrderLineDto iceColdLine = BeerOrderLineDto.builder()
                .beer(BeerDto.builder()
                        .beerStyle(BeerStyle.LAGER)
                        .build())
                .orderQuantity(10)
                .build();

        BeerOrderLineDto coldLine = BeerOrderLineDto.builder()
                .beer(BeerDto.builder()
                        .beerStyle(BeerStyle.STOUT)
                        .build())
                .orderQuantity(10)
                .build();

        BeerOrderLineDto coolLine = BeerOrderLineDto.builder()
                .beer(BeerDto.builder()
                        .beerStyle(BeerStyle.GOSE)
                        .build())
                .orderQuantity(10)
                .build();

        return BeerOrderDto.builder()
                .beerOrderLines(List.of(iceColdLine, coldLine, coolLine))
                .build();
    }

}