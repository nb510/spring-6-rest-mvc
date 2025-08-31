package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.model.BeerOrderDto;
import guru.springframework.spring6restmvc.model.OrderPopulationOptions;
import guru.springframework.spring6restmvc.repository.BeerOrderLineRepository;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import guru.springframework.spring6restmvc.repository.BeerOrderShipmentRepository;
import guru.springframework.spring6restmvc.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.UUID;

import static guru.springframework.spring6restmvc.model.OrderPopulationOptions.FULL;

@RequiredArgsConstructor
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final BeerOrderLineRepository beerOrderLineRepository;
    private final BeerOrderShipmentRepository beerOrderShipmentRepository;

    @Override
    public Page<BeerOrderDto> listBeerOrders(Integer pageNumber, Integer pageSize, OrderPopulationOptions option) {
        if (option == FULL) {
            return beerOrderRepository.findAllWithShipment(PageableUtil.buildPageable(pageNumber, pageSize))
                    .map(beerOrderMapper::toDtoFull);
        } else {
            return beerOrderRepository.findAll(PageableUtil.buildPageable(pageNumber, pageSize))
                    .map(beerOrderMapper::toDtoBasic);
        }
    }

    @Override
    public Optional<BeerOrderDto> getBeerOrderById(UUID orderId) {
        return beerOrderRepository.findByIdWithShipmentAndOrderLines(orderId)
                .map(beerOrderMapper::toDtoFull);
    }

    @Override
    public UUID createBeerOrder(BeerOrderDto orderDto) {
        return beerOrderRepository.save(beerOrderMapper.toEntity(orderDto)).getId();
    }

    @Override
    public void updateBeer(UUID orderId, BeerOrderDto orderDto) {
        beerOrderRepository.findById(orderId).map(foundOrder -> {
            foundOrder.setCustomerRef(orderDto.getCustomerRef());
            beerOrderRepository.save(foundOrder);
            return foundOrder;
        }).orElseThrow(NotFoundException::new);
    }

    @Override
    public void patchBeer(UUID orderId, BeerOrderDto orderDto) {
        beerOrderRepository.findById(orderId).map(foundOrder -> {
            if (orderDto.getCustomerRef() != null) {
                foundOrder.setCustomerRef(orderDto.getCustomerRef());
            }
            beerOrderRepository.save(foundOrder);
            return foundOrder;
        }).orElseThrow(NotFoundException::new);
    }

    @Override
    public void deleteBeerOrder(UUID orderId) {
        BeerOrder order = beerOrderRepository.findByIdWithShipmentAndOrderLines(orderId)
                .orElseThrow(NotFoundException::new);

        if (!CollectionUtils.isEmpty(order.getBeerOrderLines())) {
            beerOrderLineRepository.deleteAll(order.getBeerOrderLines());
        }

        beerOrderRepository.deleteById(orderId);
    }
}
