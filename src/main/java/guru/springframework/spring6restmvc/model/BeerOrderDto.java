package guru.springframework.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BeerOrderDto {

    private UUID id;
    private String customerRef;
    private BeerOrderShipmentDto beerOrderShipment;

}
