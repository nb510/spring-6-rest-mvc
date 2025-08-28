package guru.springframework.spring6restmvc.listener;

import guru.springframework.spring6restmvc.entities.BeerAudit;
import guru.springframework.spring6restmvc.events.*;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.repository.BeerAuditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class BeerCreatedEventListener {

    private final BeerAuditRepository beerAuditRepository;
    private final BeerMapper beerMapper;

    @Async
    @EventListener
    public void onEvent(BeerEvent event) {
        BeerAudit beerAudit = beerMapper.toBeerAudit(event.getBeer());

        String eventType = switch (event) {
            case BeerCreatedEvent e -> "BEER_CREATED";
            case BeerUpdateEvent e -> "BEER_UPDATED";
            case BeerPatchEvent e -> "BEER_PATCHED";
            case BeerDeleteEvent e -> "BEER_DELETE";
            default -> "";
        };
        beerAudit.setAuditEventType(eventType);

        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            beerAudit.setPrincipalName(event.getAuthentication().getName());
        }

        beerAuditRepository.save(beerAudit);
    }
}
