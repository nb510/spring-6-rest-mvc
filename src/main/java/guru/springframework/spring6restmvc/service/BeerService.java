package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.BeerDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    List<BeerDto> listBeers();

    Optional<BeerDto> getBeerById(UUID id);

    BeerDto createBeer(BeerDto beer);

    void updateBeerById(UUID id, BeerDto beer);

    void deleteBeerById(UUID id);

    void patchBeer(UUID id, BeerDto beer);
}
