package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BeerCreatedEventListener {

    @EventListener
    public void onEvent(BeerCreatedEvent event) {
        log.info("BeerCreatedEvent has benn published");
    }
}
