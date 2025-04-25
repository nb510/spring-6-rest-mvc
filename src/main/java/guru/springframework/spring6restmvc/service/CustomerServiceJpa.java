package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDto;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJpa implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> listCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toCustomerDto)
                .toList();
    }

    @Override
    public Optional<CustomerDto> getCustomerById(UUID id) {
        return customerRepository.findById(id).map(customerMapper::toCustomerDto);
    }

    @Override
    public CustomerDto createCustomer(CustomerDto customer) {
        return customerMapper.toCustomerDto(customerRepository.save(customerMapper.toCustomer(customer)));
    }

    @Override
    public void updateCustomerById(UUID id, CustomerDto customer) {
        customerRepository.findById(id).map(foundCustomer -> {
                    foundCustomer.setCustomerName(customer.getCustomerName());
                    return customer;
                })
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public boolean deleteCustomerById(UUID id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void patchCustomerById(UUID id, CustomerDto customer) {
        Optional<Customer> foundCustomerOpt = customerRepository.findById(id);
        if (foundCustomerOpt.isEmpty()) {
            throw new NotFoundException();
        }

        Customer foundCustomer = foundCustomerOpt.get();
        if (customer.getCustomerName() != null) {
            foundCustomer.setCustomerName(customer.getCustomerName());
        }
        if (customer.getAge() != null) {
            foundCustomer.setAge(foundCustomer.getAge());
        }
    }
}
