package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.BeerOrderDto;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerControllerIT.jwtRequestPostProcessor;
import static guru.springframework.spring6restmvc.controller.BeerOrderController.BEER_ORDER_ID_PATH;
import static guru.springframework.spring6restmvc.controller.BeerOrderController.BEER_ORDER_PATH;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("default")
public class BeerOrderControllerIT {

    @Autowired
    WebApplicationContext wac;
    @Autowired
    BeerOrderRepository beerOrderRepository;
    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.read")
    void testListBeerOrder() throws Exception {
        mockMvc.perform(get(BEER_ORDER_PATH)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.read")
    void testListBeerOrderPageable() throws Exception {
        mockMvc.perform(get(BEER_ORDER_PATH)
                        .param("page", "0")
                        .param("size", "1")
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(1)));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.read")
    void testListBeerOrderWithOptionBasic() throws Exception {
        mockMvc.perform(get(BEER_ORDER_PATH)
                        .param("option", "BASIC")
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].beerOrderShipment").isEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.read")
    void testListBeerOrderWithOptionFull() throws Exception {
        mockMvc.perform(get(BEER_ORDER_PATH)
                        .param("option", "FULL")
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].beerOrderShipment").isNotEmpty())
                .andExpect(jsonPath("$.content[0].beerOrderShipment.trackingNumber").isNotEmpty())
                .andExpect(jsonPath("$.content[0].beerOrderLines").isNotEmpty())
                .andExpect(jsonPath("$.content[0].beerOrderLines[0].orderQuantity").isNotEmpty())
                .andExpect(jsonPath("$.content[0].beerOrderLines[0].beer").isNotEmpty())
                .andExpect(jsonPath("$.content[0].beerOrderLines[0].beer.beerName").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.read")
    void testGetBeerOrderById() throws Exception {
        UUID id = beerOrderRepository.findAll().get(0).getId();

        mockMvc.perform(get(BEER_ORDER_ID_PATH, id)
                        .param("option", "FULL")
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beerOrderShipment").isNotEmpty())
                .andExpect(jsonPath("$.beerOrderShipment.trackingNumber").isNotEmpty())
                .andExpect(jsonPath("$.beerOrderLines").isNotEmpty())
                .andExpect(jsonPath("$.beerOrderLines[0].orderQuantity").isNotEmpty())
                .andExpect(jsonPath("$.beerOrderLines[0].beer").isNotEmpty())
                .andExpect(jsonPath("$.beerOrderLines[0].beer.beerName").isNotEmpty());

    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testCreateBeer() throws Exception {
        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef("new order")
                .build();

        MvcResult mvcResult = mockMvc.perform(post(BEER_ORDER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = mvcResult.getResponse().getHeaders("Location").getFirst();
        String path = UriComponentsBuilder.fromUriString(location).build().getPath();

        mockMvc.perform(get(path)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerRef", is(order.getCustomerRef())));
    }


    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testUpdateBeer() throws Exception {
        UUID id = beerOrderRepository.findAll().get(0).getId();

        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef("new order $$$")
                .build();

        mockMvc.perform(put(BEER_ORDER_ID_PATH, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isNoContent())
                .andReturn();

        mockMvc.perform(get(BEER_ORDER_ID_PATH, id)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerRef", is(order.getCustomerRef())));
    }
}
