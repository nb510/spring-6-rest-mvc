package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.model.CustomerDto;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {
    @Autowired
    CustomerController customerController;
    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testListCustomer() {
        List<CustomerDto> result = customerController.listCustomers();
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @Transactional
    @Rollback
    void testEmptyListCustomer() {
        customerRepository.deleteAll();

        List<CustomerDto> result = customerController.listCustomers();
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void testGetCustomerById() {
        List<CustomerDto> customers = customerController.listCustomers();
        CustomerDto result = customerController.getCustomerById(customers.get(0).getId());
        assertThat(result).isEqualTo(customers.get(0));
    }

    @Test
    @Transactional
    @Rollback
    void testGetCustomerByIdNotFound() {
        List<CustomerDto> customers = customerController.listCustomers();
        customerRepository.deleteById(customers.get(0).getId());

        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(customers.get(0).getId()));
    }

}