package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDto;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("default")
class BeerControllerIT {
    @Autowired
    BeerController beerController;
    @Autowired
    BeerRepository beerRepository;
    @Autowired
    BeerMapper beerMapper;

    @Autowired
    WebApplicationContext wac;
    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void testJpaConstraints() throws Exception {
        Beer beer = beerRepository.findAll().get(0);

        BeerDto beerDto = BeerDto.builder()
                .beerName(" ")
                .build();

        MvcResult mvcResult = mockMvc.perform(patch(BEER_PATH_ID, beer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Rollback
    @Transactional
    @Test
    void testPatchBeerNotFound() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDto beerDto = BeerDto.builder().build();
        beerDto.setBeerName("UPDATED");

        beerRepository.deleteById(beer.getId());

        assertThrows(NotFoundException.class, () -> beerController.patchBeer(beer.getId(), beerDto));
    }

    @Rollback
    @Transactional
    @Test
    void testPatchBeer() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDto beerDto = BeerDto.builder().build();
        beerDto.setBeerName("UPDATED");

        beerController.patchBeer(beer.getId(), beerDto);

        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertThat(updatedBeer.getBeerName()).isEqualTo("UPDATED");
        assertThat(updatedBeer.getVersion()).isEqualTo(beer.getVersion());
        assertThat(updatedBeer.getUpc()).isEqualTo(beer.getUpc());
    }

    @Rollback
    @Transactional
    @Test
    void testDeleteBeerByIdNotFound() {
        Beer beer = beerRepository.findAll().get(0);
        UUID id = beer.getId();
        beerRepository.deleteById(id);

        assertThrows(NotFoundException.class, () -> beerController.deleteBeer(beer.getId()));
    }

    @Rollback
    @Transactional
    @Test
    void testDeleteBeerById() {
        Beer beer = beerRepository.findAll().get(0);

        beerController.deleteBeer(beer.getId());

        assertThat(beerRepository.findById(beer.getId())).isEmpty();
    }

    @Rollback
    @Transactional
    @Test
    void testUpdateBeerNotFound() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDto beerDto = beerMapper.toBeerDto(beer);
        beerDto.setVersion(null);
        beerDto.setId(null);
        beerDto.setBeerName("UPDATED");

        UUID id = beer.getId();
        beerRepository.deleteById(id);

        assertThrows(NotFoundException.class, () -> beerController.updateBeer(beer.getId(), beerDto));
    }

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
        Page<BeerDto> result = beerController.listBeers(null, 2413);
        assertThat(result.getContent().size()).isEqualTo(2413);
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
        Page<BeerDto> beers = beerController.listBeers(null, null);

        beerRepository.deleteById(beers.getContent().getFirst().getId());

        assertThrows(NotFoundException.class, () -> beerController.getBeerById(beers.getContent().getFirst().getId()));
    }

    @Test
    void testGetBeerById() {
        Page<BeerDto> beers = beerController.listBeers(null, null);

        BeerDto result = beerController.getBeerById(beers.getContent().getFirst().getId());
        assertThat(result).isEqualTo(beers.getContent().getFirst());
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        beerRepository.deleteAll();

        Page<BeerDto> result = beerController.listBeers(null, null);
        assertThat(result.getContent().size()).isEqualTo(0);
    }

}