package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.model.BeerOrderDto;
import guru.springframework.spring6restmvc.model.OrderPopulationOptions;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerOrderService {

    Page<BeerOrderDto> listBeerOrders(Integer pageNumber, Integer pageSize, OrderPopulationOptions option);

    Optional<BeerOrderDto> getBeerOrderById(UUID orderId);

    UUID createBeerOrder(BeerOrderDto orderDto);

    void updateBeer(UUID orderId, BeerOrderDto orderDto);

    void patchBeer(UUID orderId, BeerOrderDto orderDto);
}
