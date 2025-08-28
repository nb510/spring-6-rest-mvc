package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BeerCreatedEventListener {

    @Async
    @EventListener
    public void onEvent(BeerCreatedEvent event) {
        log.info("BeerCreatedEvent has benn published");
        log.info("Thread name: {}", Thread.currentThread().getName());
        log.info("Thread ID: {}", Thread.currentThread().threadId());
    }
}
