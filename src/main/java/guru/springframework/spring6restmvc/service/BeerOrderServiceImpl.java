package guru.springframework.spring6restmvc.service;

import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.model.BeerOrderDto;
import guru.springframework.spring6restmvc.model.OrderPopulationOptions;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import guru.springframework.spring6restmvc.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static guru.springframework.spring6restmvc.model.OrderPopulationOptions.FULL;

@RequiredArgsConstructor
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

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
}
