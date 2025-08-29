package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.model.BeerOrderDto;
import org.mapstruct.Mapper;

@Mapper
public interface BeerOrderMapper {

    BeerOrderDto orderToOrderDto(BeerOrder order);
}
