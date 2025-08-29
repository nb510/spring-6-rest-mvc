package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.BeerOrderShipment;
import guru.springframework.spring6restmvc.model.BeerOrderShipmentDto;
import org.mapstruct.Mapper;

@Mapper
public interface BeerOrderShipmentMapper {

    BeerOrderShipmentDto toDto(BeerOrderShipment beerOrderShipment);
}
