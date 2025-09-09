package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.entities.BeerOrder;
import guru.springframework.spring6restmvc.entities.BeerOrderLine;
import guru.springframework.spring6restmvc.events.BeerOrderCreateEvent;
import guru.springframework.spring6restmvc.events.BeerOrderUpdateEvent;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.repository.BeerOrderLineRepository;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.util.PageableUtil;
import guru.springframework.spring6restmvcapi.BeerOrderDto;
import guru.springframework.spring6restmvcapi.OrderPopulationOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.springframework.spring6restmvcapi.OrderPopulationOptions.FULL;

@RequiredArgsConstructor
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final BeerOrderLineRepository beerOrderLineRepository;
    private final BeerRepository beerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

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
        Set<BeerOrderLine> orderLines = null;
        if (orderDto.getBeerOrderLines() != null) {
            orderLines = orderDto.getBeerOrderLines()
                    .stream()
                    .map(orderLineDto -> BeerOrderLine.builder()
                            .beer(orderLineDto.getBeer() == null || orderLineDto.getBeer().getId() == null
                                    ? null
                                    : beerRepository.findById(orderLineDto.getBeer().getId()).orElseThrow(NotFoundException::new))
                            .orderQuantity(orderLineDto.getOrderQuantity())
                            .build())
                    .collect(Collectors.toSet());
        }

        BeerOrder savedOrder = beerOrderRepository.save(
                BeerOrder.builder()
                        .customerRef(orderDto.getCustomerRef())
                        .beerOrderLines(orderLines)
                        .build());

        applicationEventPublisher.publishEvent(
                new BeerOrderCreateEvent(savedOrder, SecurityContextHolder.getContext().getAuthentication()));

        return savedOrder.getId();
    }

    @Override
    public void updateBeer(UUID orderId, BeerOrderDto orderDto) {
        beerOrderRepository.findById(orderId).map(foundOrder -> {
            foundOrder.setCustomerRef(orderDto.getCustomerRef());
            BeerOrder savedOrder = beerOrderRepository.save(foundOrder);

            applicationEventPublisher.publishEvent(
                    new BeerOrderUpdateEvent(savedOrder, SecurityContextHolder.getContext().getAuthentication()));

            return savedOrder;
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
