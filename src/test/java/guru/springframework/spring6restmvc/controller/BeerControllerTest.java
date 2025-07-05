package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.configuration.SpringSecurityConfig;
import guru.springframework.spring6restmvc.model.BeerDto;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.service.BeerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.*;

import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH;
import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH_ID;
import static guru.springframework.spring6restmvc.controller.BeerControllerIT.jwtRequestPostProcessor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
@ActiveProfiles("default")
@Import(SpringSecurityConfig.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BeerService beerService;

    @Captor
    ArgumentCaptor<UUID> uuidCaptor;
    @Captor
    ArgumentCaptor<BeerDto> beerCaptor;

    @Test
    public void testAuth() throws Exception {
        BeerDto beer = BeerDto.builder()
                .id(UUID.randomUUID())
                .beerName("Oxota Krepkoe")
                .build();

        when(beerService.getBeerById(beer.getId())).thenReturn(Optional.of(beer));

        mockMvc.perform(get(BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateError() throws Exception {
        BeerDto beer = BeerDto.builder()
                .beerName("")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("2135135")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();

        given(beerService.createBeer(any(BeerDto.class))).willReturn(beer);

        MvcResult mvcResult = mockMvc.perform(put(BEER_PATH_ID, UUID.randomUUID())
                        .with(jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.beerName.length()", is(1)))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testInvalidPrice() throws Exception {
        BeerDto beer = BeerDto.builder()
                .beerName("some")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("2135135")
                .price(new BigDecimal("-11.99"))
                .quantityOnHand(392)
                .build();

        given(beerService.createBeer(any(BeerDto.class))).willReturn(beer);

        MvcResult mvcResult = mockMvc.perform(post(BEER_PATH)
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.price.length()", is(1)))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testEmptyBeerStyle() throws Exception {
        BeerDto beer = BeerDto.builder()
                .beerName("some")
                .beerStyle(null)
                .upc("2135135")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();

        given(beerService.createBeer(any(BeerDto.class))).willReturn(beer);

        MvcResult mvcResult = mockMvc.perform(post(BEER_PATH)
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.beerStyle.length()", is(1)))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testEmptyBeerNameAndUpc() throws Exception {
        BeerDto beer = BeerDto.builder()
                .beerName(null)
                .beerStyle(BeerStyle.PALE_ALE)
                .upc(null)
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();

        given(beerService.createBeer(any(BeerDto.class))).willReturn(beer);

        MvcResult mvcResult = mockMvc.perform(post(BEER_PATH)
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.beerName.length()", is(2)))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testException() throws Exception {
        given(beerService.getBeerById(any())).willReturn(Optional.empty());

        mockMvc.perform(get(BEER_PATH_ID, UUID.randomUUID())
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchBeer() throws Exception {
        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "Updated name");
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch(BEER_PATH_ID, id)
                        .with(jwtRequestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andExpect(status().isNoContent());

        verify(beerService).patchBeer(uuidCaptor.capture(), beerCaptor.capture());
        assertThat(id).isEqualTo(uuidCaptor.getValue());
        assertThat(beerMap.get("beerName")).isEqualTo(beerCaptor.getValue().getBeerName());
    }

    @Test
    public void createBeer() throws Exception {
        BeerDto beer = BeerDto.builder()
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();

        given(beerService.createBeer(any(BeerDto.class))).willReturn(beer);

        mockMvc.perform(post(BEER_PATH)
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void deleteBeer() throws Exception {
        UUID id = UUID.randomUUID();

        given(beerService.deleteBeerById(id)).willReturn(true);

        mockMvc.perform(delete(BEER_PATH_ID, id)
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<UUID> uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());

        assertThat(id).isEqualTo(uuidArgumentCaptor.getValue());
    }

    @Test
    void testUpdateBeer() throws Exception {
        BeerDto beer = BeerDto.builder()
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();

        mockMvc.perform(put(BEER_PATH_ID, UUID.randomUUID())
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeerById(any(UUID.class), any(BeerDto.class));
    }

    @Test
    void testListBeer() throws Exception {
        BeerDto beer1 = BeerDto.builder().id(UUID.randomUUID()).build();
        BeerDto beer2 = BeerDto.builder().id(UUID.randomUUID()).build();
        given(beerService.listBeers(null, null)).willReturn(new PageImpl<>(List.of(beer1, beer2)));

        mockMvc.perform(get(BEER_PATH)
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(2)));
    }

    @Test
    void testGetBeerById() throws Exception {

        BeerDto beer = BeerDto.builder()
                .id(UUID.randomUUID())
                .beerName("Oxota Krepkoe")
                .build();

        when(beerService.getBeerById(beer.getId())).thenReturn(Optional.of(beer));

        mockMvc.perform(get(BEER_PATH_ID, beer.getId())
                        .with(jwtRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(beer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
    }

}