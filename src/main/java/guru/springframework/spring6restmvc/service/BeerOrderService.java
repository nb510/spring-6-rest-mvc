package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.BeerOrderDto;
import guru.springframework.spring6restmvc.model.OrderPopulationOptions;
import org.springframework.data.domain.Page;

public interface BeerOrderService {

    Page<BeerOrderDto> listBeerOrders(Integer pageNumber, Integer pageSize, OrderPopulationOptions option);
}
