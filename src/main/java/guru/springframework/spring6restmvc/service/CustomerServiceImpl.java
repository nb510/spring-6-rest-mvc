package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.CustomerDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, CustomerDto> customerMap;

    public CustomerServiceImpl() {
        customerMap = new HashMap<>();

        CustomerDto customer1 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .customerName("Customer1")
                .version(0)
                .createdDate(LocalDateTime.now())
                .lastModifiedDat(LocalDateTime.now())
                .build();

        CustomerDto customer2 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .customerName("Customer2")
                .version(0)
                .createdDate(LocalDateTime.now())
                .lastModifiedDat(LocalDateTime.now())
                .build();

        CustomerDto customer3 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .customerName("Customer3")
                .version(0)
                .createdDate(LocalDateTime.now())
                .lastModifiedDat(LocalDateTime.now())
                .build();

        customerMap.put(customer1.getId(), customer1);
        customerMap.put(customer2.getId(), customer2);
        customerMap.put(customer3.getId(), customer3);
    }

    @Override
    public List<CustomerDto> listCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Optional<CustomerDto> getCustomerById(UUID id) {
        return Optional.ofNullable(customerMap.get(id));
    }

    @Override
    public CustomerDto createCustomer(CustomerDto customer) {
        CustomerDto savedCustomer = CustomerDto.builder()
                .id(UUID.randomUUID())
                .customerName(customer.getCustomerName())
                .version(customer.getVersion())
                .createdDate(LocalDateTime.now())
                .lastModifiedDat(LocalDateTime.now())
                .build();

        customerMap.put(savedCustomer.getId(), savedCustomer);
        return savedCustomer;
    }

    @Override
    public void updateCustomerById(UUID id, CustomerDto customer) {
        CustomerDto existingCustomer = customerMap.get(id);
        existingCustomer.setCustomerName(customer.getCustomerName());
    }

    @Override
    public boolean deleteCustomerById(UUID id) {
        if (customerMap.containsKey(id)) {
            customerMap.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void patchCustomerById(UUID id, CustomerDto customer) {
        CustomerDto existing = customerMap.get(id);
        existing.setCustomerName(customer.getCustomerName());
    }
}
