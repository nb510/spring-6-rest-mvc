package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.model.BeerDto;
import guru.springframework.spring6restmvc.service.BeerService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
public class BeerController {
    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = "/api/v1/beer/{beerId}";

    private final BeerService beerService;

    @PreAuthorize("hasAuthority('SCOPE_message.write')")
    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity<Void> patchBeer(@Validated @PathVariable("beerId") UUID id, @RequestBody BeerDto beer) {
        beerService.patchBeer(id, beer);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('SCOPE_message.write')")
    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity<Void> deleteBeer(@PathVariable("beerId") UUID id) {
        boolean isDeleted = beerService.deleteBeerById(id);
        if (!isDeleted) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('SCOPE_message.write')")
    @PutMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDto> updateBeer(@NotNull @PathVariable("beerId") UUID id, @Validated @RequestBody BeerDto beer) {
        beerService.updateBeerById(id, beer);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('SCOPE_message.write')")
    @PostMapping(BEER_PATH)
    public ResponseEntity<Void> createBeer(@Validated @RequestBody BeerDto beer) {
        BeerDto savedBeer = beerService.createBeer(beer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/v1/beer/%s".formatted(savedBeer.getId()));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_message.read')")
    @GetMapping(BEER_PATH)
    public Page<BeerDto> listBeers(@RequestParam(value = "page", required = false) Integer pageNumber,
                                   @RequestParam(value = "size", required = false) Integer pageSize) {
        return beerService.listBeers(pageNumber, pageSize);
    }

    @PreAuthorize("hasAuthority('SCOPE_message.read')")
    @GetMapping(BEER_PATH_ID)
    public BeerDto getBeerById(@PathVariable("beerId") UUID id){
        return beerService.getBeerById(id).orElseThrow(NotFoundException::new);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }
}
