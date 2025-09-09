package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import guru.springframework.spring6restmvc.events.BeerDeleteEvent;
import guru.springframework.spring6restmvc.events.BeerPatchEvent;
import guru.springframework.spring6restmvc.events.BeerUpdateEvent;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.util.PageableUtil;
import guru.springframework.spring6restmvcapi.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJpa implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Cacheable(cacheNames = "beerListCache")
    @Override
    public Page<BeerDto> listBeers(Integer pageNumber, Integer pageSize) {
        log.info("Calling listBeers");

        Page<Beer> result = beerRepository.findAll(PageableUtil.buildPageable(pageNumber, pageSize));
        return result.map(beerMapper::toBeerDto);
    }

    @Cacheable(cacheNames = "beerCache", key = "#id")
    @Override
    public Optional<BeerDto> getBeerById(UUID id) {
        log.info("Calling getBeerById");
        return beerRepository.findById(id).map(beerMapper::toBeerDto);
    }

    @Override
    public BeerDto createBeer(BeerDto beer) {
        cacheManager.getCache("beerListCache").clear();

        Beer createdBeer = beerRepository.save(beerMapper.toBeer(beer));

        applicationEventPublisher.publishEvent(
                new BeerCreatedEvent(
                        createdBeer,
                        SecurityContextHolder.getContext().getAuthentication()));

        return beerMapper.toBeerDto(createdBeer);
    }

    @Override
    public void updateBeerById(UUID id, BeerDto beer) {
        clearBeerCache(id);

        beerRepository.findById(id).map(foundBeer -> {
            foundBeer.setBeerName(beer.getBeerName());
            foundBeer.setBeerStyle(beer.getBeerStyle());
            foundBeer.setUpc(beer.getUpc());
            foundBeer.setPrice(beer.getPrice());
            beerRepository.save(foundBeer);

            applicationEventPublisher.publishEvent(
                    new BeerUpdateEvent(
                            foundBeer,
                            SecurityContextHolder.getContext().getAuthentication()));

            return foundBeer;
        }).orElseThrow(NotFoundException::new);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "beerCache", key = "#id"),
            @CacheEvict(cacheNames = "beerListCache", allEntries = true)
    })
    @Override
    public boolean deleteBeerById(UUID id) {
        if (beerRepository.existsById(id)) {
            beerRepository.deleteById(id);

            applicationEventPublisher.publishEvent(
                    new BeerDeleteEvent(
                            Beer.builder().id(id).build(),
                            SecurityContextHolder.getContext().getAuthentication()));

            return true;
        }
        return false;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "beerCache", key = "#id"),
            @CacheEvict(cacheNames = "beerListCache", allEntries = true)
    })
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

        applicationEventPublisher.publishEvent(
                new BeerPatchEvent(
                        fondBeer,
                        SecurityContextHolder.getContext().getAuthentication()));
    }

    private void clearBeerCache(UUID beerId) {
        cacheManager.getCache("beerCache").evict(beerId);
        cacheManager.getCache("beerListCache").clear();
    }
}
