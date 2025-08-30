package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.model.BeerOrderDto;
import guru.springframework.spring6restmvc.model.OrderPopulationOptions;
import guru.springframework.spring6restmvc.service.BeerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

import static guru.springframework.spring6restmvc.util.PageableUtil.DEFAULT_PAGE_NUMBER_S;
import static guru.springframework.spring6restmvc.util.PageableUtil.DEFAULT_PAGE_SIZE_S;

@RequiredArgsConstructor
@RestController
public class BeerOrderController {
    public static final String BEER_ORDER_PATH = "/api/v1/order";
    public static final String BEER_ORDER_ID_PATH = "/api/v1/order/{orderId}";

    private final BeerOrderService beerOrderService;

    @PreAuthorize("hasAuthority('SCOPE_message.read')")
    @GetMapping(BEER_ORDER_PATH)
    public Page<BeerOrderDto> listBeerOrders(@RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE_NUMBER_S) int pageNumber,
                                             @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE_S) int pageSize,
                                             @RequestParam(value = "option", required = false, defaultValue = "BASIC") OrderPopulationOptions option) {
        return beerOrderService.listBeerOrders(pageNumber, pageSize, option);
    }

    @PreAuthorize("hasAuthority('SCOPE_message.read')")
    @GetMapping(BEER_ORDER_ID_PATH)
    public BeerOrderDto getBeerOrderById(@PathVariable("orderId") UUID orderId) {
        return beerOrderService.getBeerOrderById(orderId).orElseThrow(NotFoundException::new);
    }

    @PreAuthorize("hasAuthority('SCOPE_message.write')")
    @PostMapping(BEER_ORDER_PATH)
    public ResponseEntity<Void> createBeerOrder(@RequestBody BeerOrderDto beerOrderDto) {
        UUID id = beerOrderService.createBeerOrder(beerOrderDto);

        return ResponseEntity.created(URI.create("/api/v1/order/%s".formatted(id))).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_message.write')")
    @PutMapping(BEER_ORDER_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createBeerOrder(@PathVariable("orderId") UUID orderId,
                                @RequestBody BeerOrderDto orderDto) {
        beerOrderService.updateBeer(orderId, orderDto);
    }
}
