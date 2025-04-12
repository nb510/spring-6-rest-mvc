package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Customer;
import guru.springframework.spring6restmvc.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static guru.springframework.spring6restmvc.controller.CustomerController.CUSTOMER_PATH;
import static guru.springframework.spring6restmvc.controller.CustomerController.CUSTOMER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @Captor
    ArgumentCaptor<UUID> uuidCaptor;
    @Captor
    ArgumentCaptor<Customer> customerCaptor;

    @Test
    public void testException() throws Exception {
        given(customerService.getCustomerById(any())).willReturn(Optional.empty());

        mockMvc.perform(get(CUSTOMER_PATH_ID, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchCustomer() throws Exception {
        UUID id = UUID.randomUUID();
        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("customerName", "Updated name");

        mockMvc.perform(patch(CUSTOMER_PATH_ID, id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerMap)))
                .andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(uuidCaptor.capture(), customerCaptor.capture());
        assertThat(id).isEqualTo(uuidCaptor.getValue());
        assertThat(customerMap.get("customerName")).isEqualTo(customerCaptor.getValue().getCustomerName());
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete(CUSTOMER_PATH_ID, id))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(uuidCaptor.capture());

        assertThat(id).isEqualTo(uuidCaptor.getValue());
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        Customer customer = Customer.builder().customerName("Updated").build();
        UUID id = UUID.randomUUID();

        mockMvc.perform(put(CUSTOMER_PATH_ID,id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(any(UUID.class), any(Customer.class));
    }

    @Test
    public void testCreateCustomer() throws Exception {
        Customer customer = Customer.builder().id(UUID.randomUUID()).build();

        given(customerService.createCustomer(any())).willReturn(customer);

        mockMvc.perform(post(CUSTOMER_PATH)
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

        mockMvc.perform(get(CUSTOMER_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void testGetCustomerById() throws Exception {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .customerName("Classic")
                .build();
        given(customerService.getCustomerById(customer.getId())).willReturn(Optional.of(customer));

        mockMvc.perform(get(CUSTOMER_PATH_ID, customer.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customer.getId().toString())))
                .andExpect(jsonPath("$.customerName", is(customer.getCustomerName())));
    }
}