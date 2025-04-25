package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDto;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BeerControllerIT {
    @Autowired
    BeerController beerController;
    @Autowired
    BeerRepository beerRepository;
    @Autowired
    BeerMapper beerMapper;

    @Test
    void testUpdateBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDto beerDto = beerMapper.toBeerDto(beer);
        beerDto.setVersion(null);
        beerDto.setId(null);
        beerDto.setBeerName("UPDATED");

        beerController.updateBeer(beer.getId(), beerDto);

        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo("UPDATED");
    }

    @Test
    void testListBeer() {
        List<BeerDto> result = beerController.listBeers();
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @Transactional
    @Rollback
    void testCreateBeer() {
        BeerDto beerDto = BeerDto.builder()
                .beerName("super Beer")
                .upc("1")
                .build();

        ResponseEntity<Void> response = beerController.createBeer(beerDto);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] path = response.getHeaders().getLocation().getPath().split("/");
        assertThat(path[4]).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void testGetBeerByIdNotFound() {
        List<BeerDto> beers = beerController.listBeers();

        beerRepository.deleteById(beers.get(0).getId());

        assertThrows(NotFoundException.class, () -> beerController.getBeerById(beers.get(0).getId()));
    }

    @Test
    void testGetBeerById() {
        List<BeerDto> beers = beerController.listBeers();

        BeerDto result = beerController.getBeerById(beers.get(0).getId());
        assertThat(result).isEqualTo(beers.get(0));
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        beerRepository.deleteAll();

        List<BeerDto> result = beerController.listBeers();
        assertThat(result.size()).isEqualTo(0);
    }

}