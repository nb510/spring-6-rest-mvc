package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.events.BeerOrderCreateEvent;
import guru.springframework.spring6restmvc.events.BeerOrderUpdateEvent;
import guru.springframework.spring6restmvc.repository.BeerOrderLineRepository;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import guru.springframework.spring6restmvc.repository.BeerOrderShipmentRepository;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvcapi.BeerDto;
import guru.springframework.spring6restmvcapi.BeerOrderDto;
import guru.springframework.spring6restmvcapi.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerControllerIT.jwtRequestPostProcessor;
import static guru.springframework.spring6restmvc.controller.BeerOrderController.BEER_ORDER_ID_PATH;
import static guru.springframework.spring6restmvc.controller.BeerOrderController.BEER_ORDER_PATH;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("default")
@RecordApplicationEvents
public class BeerOrderControllerIT {

    @Autowired
    WebApplicationContext wac;
    @Autowired
    BeerOrderRepository beerOrderRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BeerOrderShipmentRepository beerOrderShipmentRepository;
    @Autowired
    BeerOrderLineRepository beerOrderLineRepository;
    @Autowired
    BeerRepository beerRepository;
    @Autowired
    ApplicationEvents applicationEvents;

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
    void testCreateBeerOrder() throws Exception {
        List<Beer> beers = beerRepository.findAll();

        BeerOrderLineDto orderLine1 = BeerOrderLineDto.builder()
                .beer(BeerDto.builder()
                        .id(beers.get(21).getId())
                        .build())
                .orderQuantity(10)
                .build();

        BeerOrderLineDto orderLine2 = BeerOrderLineDto.builder()
                .beer(BeerDto.builder()
                        .id(beers.get(22).getId())
                        .build())
                .orderQuantity(10)
                .build();

        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef("new order")
                .beerOrderLines(List.of(orderLine1, orderLine2))
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
                .andExpect(jsonPath("$.customerRef", is(order.getCustomerRef())))
                .andExpect(jsonPath("$.beerOrderLines.length()", is(2)))
                .andExpect(jsonPath("$.beerOrderLines[0].beer.id").isNotEmpty())
                .andExpect(jsonPath("$.beerOrderLines[0].orderQuantity", is(orderLine1.getOrderQuantity())))
                .andExpect(jsonPath("$.beerOrderLines[0].status", is("NEW")));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testCreateBeerOrderEvent() throws Exception {
        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef("new order")
                .build();

        mockMvc.perform(post(BEER_ORDER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        assertThat(applicationEvents.stream(BeerOrderCreateEvent.class).count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testCreateBeerOrderBadRequestBody() throws Exception {
        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef(null)
                .build();

        mockMvc.perform(post(BEER_ORDER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testUpdateBeerOrder() throws Exception {
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

    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testUpdateBeerOrderEvent() throws Exception {
        UUID id = beerOrderRepository.findAll().get(0).getId();

        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef("new order $$$")
                .build();

        mockMvc.perform(put(BEER_ORDER_ID_PATH, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isNoContent());

        assertThat(applicationEvents.stream(BeerOrderUpdateEvent.class).count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testUpdateBeerOrderBadRequestBody() throws Exception {
        UUID id = beerOrderRepository.findAll().get(0).getId();

        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef(null)
                .build();

        mockMvc.perform(put(BEER_ORDER_ID_PATH, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testPatchBeerOrder() throws Exception {
        UUID id = beerOrderRepository.findAll().get(0).getId();

        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef("new order $$$%%")
                .build();

        mockMvc.perform(patch(BEER_ORDER_ID_PATH, id)
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

    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    void testPatchBeerOrderBadRequestBody() throws Exception {
        UUID id = beerOrderRepository.findAll().get(0).getId();

        BeerOrderDto order = BeerOrderDto.builder()
                .customerRef(null)
                .build();

        mockMvc.perform(patch(BEER_ORDER_ID_PATH, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_message.write")
    @Transactional
    @Rollback
    void testDeleteBeerOrder() throws Exception {
        BeerOrder order =  beerOrderRepository.findAll().get(0);
        order = beerOrderRepository.findByIdWithShipmentAndOrderLines(order.getId()).get();

        mockMvc.perform(delete(BEER_ORDER_ID_PATH, order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isNoContent())
                .andReturn();

        mockMvc.perform(get(BEER_ORDER_ID_PATH, order.getId())
                        .with(jwtRequestPostProcessor))
                .andExpect(status().isNotFound());

        assertThat(beerOrderShipmentRepository.findById(order.getBeerOrderShipment().getId())).isEmpty();

        order.getBeerOrderLines().forEach(orderLine ->
                assertThat(beerOrderLineRepository.findById(orderLine.getId())).isEmpty());
    }
}
