package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.BeerDto;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Page<BeerDto> listBeers(Integer pageNumber, Integer pageSize);

    Optional<BeerDto> getBeerById(UUID id);

    BeerDto createBeer(BeerDto beer);

    void updateBeerById(UUID id, BeerDto beer);

    boolean deleteBeerById(UUID id);

    void patchBeer(UUID id, BeerDto beer);
}
