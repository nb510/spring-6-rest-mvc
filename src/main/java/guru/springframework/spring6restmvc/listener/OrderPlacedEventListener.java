package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvcapi.event.OrderPlacedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedEventListener {

    @Async
    @EventListener
    public void onEvent(OrderPlacedEvent event) {
        //TODO: implement
    }
}
