package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDto;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BeerControllerIT {
    @Autowired
    BeerController beerController;
    @Autowired
    BeerRepository beerRepository;
    @Test
    void testListBeer() {
        List<BeerDto> result = beerController.listBeers();
        assertThat(result.size()).isEqualTo(3);
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