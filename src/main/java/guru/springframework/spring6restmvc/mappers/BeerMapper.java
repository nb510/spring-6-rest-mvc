package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.BeerAudit;
import guru.springframework.spring6restmvcapi.BeerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeerMapper {

    BeerDto toBeerDto(Beer beer);

    Beer toBeer(BeerDto beerDto);

    @Mapping(target = "auditEventType", ignore = true)
    @Mapping(target = "createdDateAudit", ignore = true)
    @Mapping(target = "principalName", ignore = true)
    BeerAudit toBeerAudit(Beer beer);
}
