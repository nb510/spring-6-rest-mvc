package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvcapi.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    CustomerDto toCustomerDto(Customer customer);

    Customer toCustomer(CustomerDto customerDto);
}
