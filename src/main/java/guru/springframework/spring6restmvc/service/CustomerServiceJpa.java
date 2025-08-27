package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDto;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJpa implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Cacheable(cacheNames = "customerListCache")
    @Override
    public List<CustomerDto> listCustomers() {
        log.info("Calling listCustomers");

        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toCustomerDto)
                .toList();
    }

    @Cacheable(cacheNames = "customerCache", key = "#id")
    @Override
    public Optional<CustomerDto> getCustomerById(UUID id) {
        log.info("Calling getCustomerById");

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
