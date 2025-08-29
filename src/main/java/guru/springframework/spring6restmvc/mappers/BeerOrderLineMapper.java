package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.BeerOrderLine;
import guru.springframework.spring6restmvc.model.BeerOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = {BeerMapper.class})
public interface BeerOrderLineMapper {

    BeerOrderLineDto toDto(BeerOrderLine beerOrderLine);
}
