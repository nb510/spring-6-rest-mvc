package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @Test
    public void testCreateCustomer() throws Exception {
        Customer customer = Customer.builder().id(UUID.randomUUID()).build();

        given(customerService.createCustomer(any())).willReturn(customer);

        mockMvc.perform(post("/api/v1/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    public void testListCustomers() throws Exception {
        Customer customer1 = Customer.builder().id(UUID.randomUUID()).build();
        Customer customer2 = Customer.builder().id(UUID.randomUUID()).build();
        given(customerService.listCustomers()).willReturn(List.of(customer1, customer2));

        mockMvc.perform(get("/api/v1/customer").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void testGetCustomerById() throws Exception {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .customerName("Classic")
                .build();
        given(customerService.getCustomerById(customer.getId())).willReturn(customer);

        mockMvc.perform(get("/api/v1/customer/%s".formatted(customer.getId())).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customer.getId().toString())))
                .andExpect(jsonPath("$.customerName", is(customer.getCustomerName())));
    }
}