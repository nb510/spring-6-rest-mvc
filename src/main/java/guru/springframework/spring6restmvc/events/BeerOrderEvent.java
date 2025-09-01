package guru.springframework.spring6restmvc.events;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import org.springframework.security.core.Authentication;

public interface BeerOrderEvent {

    BeerOrder getBeerOrder();

    Authentication getAuthentication();
}
