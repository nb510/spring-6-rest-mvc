package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.events.BeerOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BeerOrderEventListener {

    @EventListener
    @Async
    public void onEvent(BeerOrderEvent event) {
        log.info("Something cool should be happening right now...");
    }
}
