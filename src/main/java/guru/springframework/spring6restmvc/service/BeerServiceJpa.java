package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDto;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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

    }

    @Override
    public void deleteBeerById(UUID id) {

    }

    @Override
    public void patchBeer(UUID id, BeerDto beer) {

    }
}
