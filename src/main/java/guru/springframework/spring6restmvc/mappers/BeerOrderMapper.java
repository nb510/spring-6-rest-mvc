package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.model.BeerOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {BeerOrderShipmentMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

    @Mapping(target = "beerOrderShipment", ignore = true)
    @Mapping(target = "beerOrderLines", ignore = true)
    BeerOrderDto toDtoBasic(BeerOrder order);

    BeerOrderDto toDtoFull(BeerOrder order);

    @Mapping(target = "beerOrderShipment", ignore = true)
    @Mapping(target = "beerOrderLines", ignore = true)
    BeerOrder toEntity(BeerOrderDto beerOrderDto);

}
