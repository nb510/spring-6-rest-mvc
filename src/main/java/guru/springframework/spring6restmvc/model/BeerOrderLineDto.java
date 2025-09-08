package guru.springframework.spring6restmvc.model;

import guru.springframework.spring6restmvc.entities.BeerOrderLineStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeerOrderLineDto {
    private BeerDto beer;
    private Integer orderQuantity;
    private Integer quantityAllocated;
    private BeerOrderLineStatus status;
}
