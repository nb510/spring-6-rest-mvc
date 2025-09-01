package guru.springframework.spring6restmvc.events;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BeerOrderCreateEvent implements BeerOrderEvent {

    private final BeerOrder beerOrder;
    private final Authentication authentication;

}
