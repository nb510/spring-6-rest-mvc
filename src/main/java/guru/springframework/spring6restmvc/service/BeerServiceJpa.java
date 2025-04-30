package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDto;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJpa implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public List<BeerDto> listBeers() {
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::toBeerDto)
                .toList();
    }

    @Override
    public Optional<BeerDto> getBeerById(UUID id) {
        return beerRepository.findById(id).map(beerMapper::toBeerDto);
    }

    @Override
    public BeerDto createBeer(BeerDto beer) {
        return beerMapper.toBeerDto(beerRepository.save(beerMapper.toBeer(beer)));
    }

    @Override
    public void updateBeerById(UUID id, BeerDto beer) {
        beerRepository.findById(id).map(foundBeer -> {
            foundBeer.setBeerName(beer.getBeerName());
            foundBeer.setBeerStyle(beer.getBeerStyle());
            foundBeer.setUpc(beer.getUpc());
            foundBeer.setPrice(beer.getPrice());
            beerRepository.save(foundBeer);
            return foundBeer;
        }).orElseThrow(NotFoundException::new);
    }

    @Override
    public boolean deleteBeerById(UUID id) {
        if (beerRepository.existsById(id)) {
            beerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void patchBeer(UUID id, BeerDto beer) {
        Optional<Beer> foundBeerOpt = beerRepository.findById(id);
        if (foundBeerOpt.isEmpty()) {
            throw new NotFoundException();
        }

        Beer fondBeer = foundBeerOpt.get();
        if (true){
            fondBeer.setBeerName(beer.getBeerName());
        }
        if (beer.getBeerStyle() != null) {
            fondBeer.setBeerStyle(beer.getBeerStyle());
        }
        if (beer.getPrice() != null) {
            fondBeer.setPrice(beer.getPrice());
        }
        if (beer.getQuantityOnHand() != null){
            fondBeer.setQuantityOnHand(beer.getQuantityOnHand());
        }
        if (StringUtils.hasText(beer.getUpc())) {
            fondBeer.setUpc(beer.getUpc());
        }
        beerRepository.save(fondBeer);
    }
}
