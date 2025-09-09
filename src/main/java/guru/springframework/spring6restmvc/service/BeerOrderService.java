package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvcapi.BeerOrderDto;
import guru.springframework.spring6restmvcapi.OrderPopulationOptions;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerOrderService {

    Page<BeerOrderDto> listBeerOrders(Integer pageNumber, Integer pageSize, OrderPopulationOptions option);

    Optional<BeerOrderDto> getBeerOrderById(UUID orderId);

    UUID createBeerOrder(BeerOrderDto orderDto);

    void updateBeerOrder(UUID orderId, BeerOrderDto orderDto);

    void patchBeerOrder(UUID orderId, BeerOrderDto orderDto);

    void deleteBeerOrder(UUID orderId);
}
