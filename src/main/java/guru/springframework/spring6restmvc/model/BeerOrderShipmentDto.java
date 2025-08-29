package guru.springframework.spring6restmvc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeerOrderShipmentDto {

    private String id;
    private String trackingNumber;
}
