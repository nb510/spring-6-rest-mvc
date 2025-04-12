package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.CustomerDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    List<CustomerDto> listCustomers();

    Optional<CustomerDto> getCustomerById(UUID id);

    CustomerDto createCustomer(CustomerDto customer);

    void updateCustomerById(UUID id, CustomerDto customer);

    void deleteCustomerById(UUID id);

    void patchCustomerById(UUID id, CustomerDto customer);
}
