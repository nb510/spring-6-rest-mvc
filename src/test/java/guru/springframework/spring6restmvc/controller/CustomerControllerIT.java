package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDto;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("default")
class CustomerControllerIT {
    @Autowired
    CustomerController customerController;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerMapper customerMapper;

    @Rollback
    @Transactional
    @Test
    void testPatchCustomerNotFound() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDto customerDto = customerMapper.toCustomerDto(customer);

        customerRepository.deleteById(customer.getId());

        assertThrows(NotFoundException.class, () -> customerController.patchCustomer(customer.getId(), customerDto));
    }

    @Rollback
    @Transactional
    @Test
    void testPatchCustomer() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDto customerDto = customerMapper.toCustomerDto(customer);
        customerDto.setId(null);
        customerDto.setCustomerName("UPDATED");
        customerDto.setAge(null);

        customerController.patchCustomer(customer.getId(), customerDto);

        Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(updatedCustomer.getCustomerName()).isEqualTo("UPDATED");
        assertThat(updatedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Rollback
    @Transactional
    @Test
    void testDeleteCustomerNotFound() {
        Customer customer = customerRepository.findAll().get(0);

        customerRepository.deleteById(customer.getId());

        assertThrows(NotFoundException.class, () -> customerController.deleteCustomer(customer.getId()));
    }

    @Rollback
    @Transactional
    @Test
    void testDeleteCustomer() {
        Customer customer = customerRepository.findAll().get(0);

        customerController.deleteCustomer(customer.getId());

        assertThat(customerRepository.findById(customer.getId())).isEmpty();
    }

    @Rollback
    @Transactional
    @Test
    void testUpdateCustomerNotFound() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDto customerDto = customerMapper.toCustomerDto(customer);

        customerRepository.deleteById(customerDto.getId());

        assertThrows(NotFoundException.class, () -> customerController.updateCustomer(customer.getId(), customerDto));
    }

    @Rollback
    @Transactional
    @Test
    void testUpdateCustomer() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDto customerDto = customerMapper.toCustomerDto(customer);
        customerDto.setId(null);
        customerDto.setCustomerName("UPDATED");

        customerController.updateCustomer(customer.getId(), customerDto);

        Customer updatedCustomer = customerRepository.findById(customer.getId()).get();
        assertThat(updatedCustomer.getCustomerName()).isEqualTo("UPDATED");
    }

    @Rollback
    @Transactional
    @Test
    void testCreateCustomer() {
        CustomerDto customerDto = CustomerDto.builder()
                .customerName("Customer 1")
                .build();

        ResponseEntity<Void> response = customerController.createCustomer(customerDto);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] path = response.getHeaders().getLocation().getPath().split("/");
        assertThat(path[4]).isNotNull();
    }

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